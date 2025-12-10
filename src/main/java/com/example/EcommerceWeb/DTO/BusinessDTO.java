package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.User;

public class BusinessDTO {
    private String businessName;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String address;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public BusinessDTO (Business business){
        this.businessName=business.getBusinessName();
        this.ownerName=business.getOwnerName();
        this.email=business.getEmail();
        this.phoneNumber=business.getPhoneNumber();
        this.address=business.getAddress();
    }

    public BusinessDTO() {
    }
}
