package ru.phonenumbers.clients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.phonenumbers.dto.ExternalRestUserResponse;

import java.net.URI;
import java.util.Optional;

public class ExternalRestClient {

    private final String baseUrl;
    private final String userPath = "/api/v1/phones/";
    private final RestTemplate restTemplate;

    public ExternalRestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public Optional<ExternalRestUserResponse> getUser(int id) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<ExternalRestUserResponse> result = restTemplate.exchange(
                    getUserURI(id), HttpMethod.GET, entity, ExternalRestUserResponse.class);
            return Optional.ofNullable(result.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private URI getUserURI(int id) throws Exception {
        return new URI(baseUrl + userPath + id);
    }
}
