package com.example.arkoseemail.controller;

import com.example.arkoseemail.model.DateModel;
import com.example.arkoseemail.service.EmailScheduler;
//import java.io.IOException;


import com.example.arkoseemail.model.EmailModel;
import jakarta.validation.Valid;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


@Controller
@RequestMapping("/email")
public class EmailController {

    private final Logger logger = LoggerFactory.getLogger(EmailController.class);

    SchedulerFactory sf = new StdSchedulerFactory();
    Scheduler sched = sf.getScheduler();

    public EmailController() throws SchedulerException {
        sched.start();
    }

    @GetMapping("/edit")
//    @RequestMapping("/edit")
    public String getEditPage(Model model) {
        model.addAttribute("email", new EmailModel());
        model.addAttribute("date", new DateModel());


        logger.info("ENV PROPS: {} ", System.getenv("SENDGRID_API_KEY") );

        return "email_edit_page";
    }

    @PostMapping("/edit")
    public String postEditPageSubmit(@Valid @ModelAttribute("email") EmailModel email, @ModelAttribute DateModel date,
                                     BindingResult bindingResult, Model model ) throws SchedulerException {

        if(bindingResult.hasErrors()) {
            return "email_edit_page";
        }

//        model.addAttribute("email", email);
        scheduleEmailJob(email, date);

        return "email_edit_submission";
    }

    private void scheduleEmailJob(EmailModel email, DateModel date) throws SchedulerException {

        LocalDateTime ldt = date.getLocalDateTime();

        JobDetail job = newJob(EmailScheduler.class)
                .withIdentity("job-" + ldt, "group1")
                .build();

        //Add details to job
        job.getJobDataMap().put("email", email);
        job.getJobDataMap().put("date", date);

        // Trigger the job to run at the local date time selected
        Trigger trigger = newTrigger()
                .withIdentity("trigger-" + ldt, "group1")
//                .startAt(runTime)
                .startAt(DateBuilder.dateOf(ldt.getHour(),
                                ldt.getMinute(),
                                ldt.getSecond(),
                                ldt.getDayOfMonth(), ldt.getMonthValue(), ldt.getYear()))
                .build();

        // Schedule job using trigger
        sched.scheduleJob(job, trigger);

    }

    }





