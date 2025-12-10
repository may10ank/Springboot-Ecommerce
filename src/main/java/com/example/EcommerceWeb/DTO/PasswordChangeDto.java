package com.example.EcommerceWeb.DTO;

public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;

    public PasswordChangeDto() {}

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
