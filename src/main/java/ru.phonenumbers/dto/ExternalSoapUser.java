package ru.phonenumbers.dto;

import soapService.wsdl.GetUserResponse;
import soapService.wsdl.User;

public class ExternalSoapUser {

    private final int code;
    private final String name;

    public ExternalSoapUser(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public ExternalSoapUser(GetUserResponse response) {
        User user = response.getUser();
        this.code = 0;
        this.name = String.join(" ", user.getFirstName(), user.getLastName());
    }

    public static ExternalSoapUser timeouted() {
        return new ExternalSoapUser(1, null);
    }

    public static ExternalSoapUser bad() {
        return new ExternalSoapUser(2, null);
    }

    public boolean isOk() {
        return code == 0;
    }

    public boolean isTimeout() {
        return code == 1;
    }

    public String getName() {
        return name;
    }
}
