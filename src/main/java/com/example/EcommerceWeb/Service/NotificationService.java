package com.example.EcommerceWeb.Service;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationService {
    @Value("${twilio.accountSid}")
    private String twilioAccountSid;
    @Value("${twilio.authToken}")
    private String twilioAuthToken;
    @Value("${twilio.fromNumber}")
    private String twilioFromNumber;
    @Value("${sendgrid.apiKey}")
    private String sendGridApiKey;
    @Value("${sendgrid.fromEmail}")
    private String sendGridFromEmail;
    @Async("notification")
    public void sendSms(String phone, String message) {
        Twilio.init(twilioAccountSid, twilioAuthToken);

        Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber(twilioFromNumber),
                message
        ).create();

        System.out.println("SMS sent to " + phone);
    }

    @Async("notification")
    public void sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(sendGridFromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email sent to " + toEmail + " | Status: " + response.getStatusCode());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email: " + ex.getMessage(), ex);
        }
    }
}
