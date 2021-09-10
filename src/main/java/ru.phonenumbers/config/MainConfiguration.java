package ru.phonenumbers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import ru.phonenumbers.clients.ExternalRestClient;
import ru.phonenumbers.clients.ExternalSoapClient;
import ru.phonenumbers.service.UserService;

@Configuration
@EnableWebSecurity
public class MainConfiguration {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }

    @Bean
    public ExternalRestClient externalRestClient(@Value("${rs.endpoint:http://localhost:9080}") String baseUrl, RestTemplate restTemplate) {
        return new ExternalRestClient(baseUrl, restTemplate);
    }

    @Bean
    public ExternalSoapClient externalSoapClient(@Value("${ws.endpoint:http://localhost:9080/ws}") String endpoint,
                                                 Jaxb2Marshaller marshaller) {
        return new ExternalSoapClient(endpoint, marshaller);
    }

    @Bean
    public UserService userService(ExternalRestClient externalRestClient, ExternalSoapClient externalSoapClient) {
        return new UserService(externalRestClient, externalSoapClient);
    }


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("soapService.wsdl");
        return marshaller;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setReadTimeout(timeout);
        return clientHttpRequestFactory;
    }
}
