package ru.phonenumbers.clients;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import ru.phonenumbers.dto.ExternalSoapUser;
import soapService.wsdl.GetUserRequest;
import soapService.wsdl.GetUserResponse;

import java.net.SocketTimeoutException;

public class ExternalSoapClient extends WebServiceGatewaySupport {

    private final String wsEndpoint;

    public ExternalSoapClient(String wsEndpoint, Jaxb2Marshaller marshaller) {
        this.wsEndpoint = wsEndpoint;
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setConnectionTimeout(5000);
        messageSender.setReadTimeout(5000);
        setMessageSender(messageSender);
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    public ExternalSoapUser getUser(int userId) {
        GetUserRequest request = new GetUserRequest();
        request.setUserId(userId);
        try {
            GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
                    .marshalSendAndReceive(wsEndpoint,  request,
                            new SoapActionCallback(
                                    "http://hilariousstartups.ru/soap/gen/getUserRequest"));
            return new ExternalSoapUser(response);
        } catch (WebServiceIOException e) {
            e.printStackTrace();
            if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                return ExternalSoapUser.timeouted();
            } else {
                return ExternalSoapUser.bad();
            }
        }
    }
}
