package ru.phonenumbers.service;

import ru.phonenumbers.clients.ExternalRestClient;
import ru.phonenumbers.clients.ExternalSoapClient;
import ru.phonenumbers.dto.ExternalRestUserResponse;
import ru.phonenumbers.dto.ExternalSoapUser;
import ru.phonenumbers.dto.UserResponse;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserService {

    private final ExternalRestClient externalRestClient;
    private final ExternalSoapClient externalSoapClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public UserService(ExternalRestClient externalRestClient, ExternalSoapClient externalSoapClient) {
        this.externalRestClient = externalRestClient;
        this.externalSoapClient = externalSoapClient;
    }

    public UserResponse getUser(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Future<ExternalSoapUser> soapF = executorService.submit(() -> externalSoapClient.getUser(id));
            Future<Optional<ExternalRestUserResponse>> restF = executorService.submit(() -> externalRestClient.getUser(id));

            ExternalSoapUser externalSoapUser = soapF.get();
            if (externalSoapUser.isTimeout()) {
                return UserResponse.timeout();
            } else if (!externalSoapUser.isOk()) {
                return UserResponse.fail();
            }
            String name = externalSoapUser.getName();
            Optional<ExternalRestUserResponse> externalRestUserResponse = restF.get();
            return new UserResponse(0, name, externalRestUserResponse.flatMap(r -> r.getPhones().stream().findAny()).orElse(null));
        } catch (Exception e) {
            e.printStackTrace();
            return UserResponse.fail();
        }
    }
}
