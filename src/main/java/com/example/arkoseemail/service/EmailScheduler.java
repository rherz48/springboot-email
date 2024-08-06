package com.example.arkoseemail.service;

import com.example.arkoseemail.model.EmailModel;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EmailScheduler implements Job {

    private final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);


    public void execute(JobExecutionContext context) throws JobExecutionException {

        EmailModel email = (EmailModel) context.getJobDetail().getJobDataMap().get("email");

        logger.info("Email: {}", email.getEmailAddress());
        logger.info("Subject: {}", email.getSubject());
        logger.info("Body: {}", email.getBody());

        //Send email to recipient
        try {
            sendEmail(email.getEmailAddress(), email.getSubject(), email.getBody());
            logger.info("Successfully sent email to recipient");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(String recipient, String subject, String body) throws IOException {
//        Email from = new Email(System.getenv("EMAIL_FROM"));
        Email from = new Email("ryan@ryanherzog.com");
//        String subject = "Sending with Twilio SendGrid is Fun";
        Email to = new Email(recipient);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, to, content);

//        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        SendGrid sg = new SendGrid("");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Status code: {}", response.getStatusCode());
            logger.info("Body: {}", response.getBody());
            logger.info("Headers: {}", response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

}
