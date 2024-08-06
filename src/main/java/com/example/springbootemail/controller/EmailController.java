package com.example.springbootemail.controller;

import com.example.springbootemail.model.DateModel;
import com.example.springbootemail.model.RegistrationModel;
import com.example.springbootemail.service.EmailScheduler;

import com.example.springbootemail.model.EmailModel;
import jakarta.validation.Valid;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


@Controller
@RequestMapping("/email")
public class EmailController {

    private final Logger logger = LoggerFactory.getLogger(EmailController.class);

    SchedulerFactory sf = new StdSchedulerFactory();
    Scheduler sched = sf.getScheduler();

    @Autowired
    private SpringTemplateEngine templateEngine;

    public EmailController() throws SchedulerException {
        sched.start();
    }

    @GetMapping("/edit")
    public String getEditPage(Model model) {
        model.addAttribute("email", new EmailModel());
        model.addAttribute("date", new DateModel());

        return "email_edit_page";

    }

    @PostMapping("/edit")
    public String postEditPageSubmit(@Valid @ModelAttribute("email") EmailModel email, @ModelAttribute DateModel date,
                                     BindingResult bindingResult, Model model ) throws SchedulerException {

        if(bindingResult.hasErrors()) {
            return "email_edit_page";
        }

        scheduleEmailJob(email, date);

        return "email_edit_submission";
    }

    @PostMapping("/register")
    public String postRegisterPageSubmit(@Valid @ModelAttribute("registration") RegistrationModel registration,
                                         BindingResult bindingResult, Model model) throws SchedulerException {

        if(bindingResult.hasErrors()) {
            return "email_edit_page";
        }

        //Setup properties for registration model
        DateModel date = new DateModel();
        date.setLocalDateTime(LocalDateTime.now());

        registration.setEmailAddress(registration.getEmailAddress());
        registration.setName(registration.getName());
        registration.setCompanyName(registration.getCompanyName());
        registration.setSubject("Registration Confirmation");

        //Fill HTML template with registration model properties,
        //and set the template to the body of email
        String templateHTMLFilled = renderRegistrationContext(registration);
        registration.setBody(templateHTMLFilled);

        //Schedule email job
        scheduleEmailJob(registration, date);

        return "email_register_submission";
    }


    @ResponseBody
    private String renderRegistrationContext(RegistrationModel registration) {
        //Fill HTML template with values provided and return HTML as string
        Context context = new Context();
        context.setVariable("registration", registration);
        return templateEngine.process("email_register", context);
    }

    private void scheduleEmailJob(EmailModel email, DateModel date) throws SchedulerException {

        //Create a LocalDateTime object and set to method parameter value,
        //or to now if none provided
        LocalDateTime ldt = date.getLocalDateTime();
        if(ldt == null) ldt = LocalDateTime.now();

        JobDetail job = newJob(EmailScheduler.class)
                .withIdentity("job-" + ldt, "group1")
                .build();

        //Add details to job
        job.getJobDataMap().put("email", email);
        job.getJobDataMap().put("date", date);

        //Temporary: For rescheduling, the trigger should be made more specific/related to item id clicked.
        //This is so that it can be updated when edited a second time removing the previous trigger made

        // Trigger the job to run at the local date time selected
        Trigger trigger = newTrigger()
                .withIdentity("trigger-" + ldt, "group1")
                .startAt(DateBuilder.dateOf(ldt.getHour(),
                                ldt.getMinute(),
                                ldt.getSecond(),
                                ldt.getDayOfMonth(), ldt.getMonthValue(), ldt.getYear()))
                .build();

        // Schedule job using trigger
        sched.scheduleJob(job, trigger);

    }

}





