package ru.phonenumbers.dto;

import java.util.List;

public class ExternalRestUserResponse {

    private List<String> phones;

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
