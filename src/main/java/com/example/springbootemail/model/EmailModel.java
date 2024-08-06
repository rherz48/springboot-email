package com.example.springbootemail.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailModel {

    @NotBlank(message = "Email cannot be left blank")
    @Email(message = "Invalid email")
    private String emailAddress;

//    @NotBlank(message = "Subject cannot be left blank")
    private String subject;

//    @NotBlank(message = "Body cannot be left blank")
    private String body;

    public EmailModel() {
        this.emailAddress = "";
        this.subject = "";
        this.body = "";
    }

    public EmailModel(String emailAddress, String subject, String body) {
        this.emailAddress = emailAddress;
        this.subject = subject;
        this.body = body;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getSubject() {
        return subject;
    }

}
