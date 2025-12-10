package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.User;

public class UserDTO {
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserDTO (User user){
        this.username=user.getUsername();
        this.name=user.getName();
        this.email=user.getEmail();
        this.phoneNumber=user.getPhoneNumber();
        this.address=user.getAddress();
    }

    public UserDTO() {
    }
}
