package com.example.springbootemail.service;

import com.example.springbootemail.model.EmailModel;
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

    public void execute(JobExecutionContext context) {

        //Get job context for the EmailModel to be used when sending the email
        EmailModel email = (EmailModel) context.getJobDetail().getJobDataMap().get("email");

        logger.info("Email: {}", email.getEmailAddress());

        //Send email to recipient
        try {
            sendEmail(email.getEmailAddress(), email.getSubject(), email.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(String recipient, String subject, String body) throws IOException {

        //Setup email parameters and send email request
        Email from = new Email("ryan@ryanherzog.com");
        Email to = new Email(recipient);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Status code: {}", response.getStatusCode());
            logger.info("Headers: {}", response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

}
