package ru.phonenumbers.dto;

public class UserResponse {
    private final int code;
    private final String name;
    private final String phone;

    public UserResponse(int code, String name, String phone) {
        this.code = code;
        this.name = name;
        this.phone = phone;
    }

    public static UserResponse fail() {
        return new UserResponse(2, null, null);
    }

    public static UserResponse timeout() {
        return new UserResponse(1, null, null);
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
