package com.taskmanager.taskmanagerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.taskmanager.taskmanagerapp.model.EmailDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Async
    public void sendMail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMsg = new SimpleMailMessage();
            mailMsg.setFrom(username);
            mailMsg.setTo(emailDetails.getToAddress());
            
            mailMsg.setText(emailDetails.getMailBody());
            mailMsg.setSubject(emailDetails.getSubject());
            log.debug("-------MailSenderService.sendMail.getSubject: "+emailDetails.getSubject());
            log.info("=======before sending email ======");
            javaMailSender.send(mailMsg);
            log.info("=============mail sent successfully====");
        } catch (Exception exception) {
            log.error("========== Mail cannot be sent ============== :" + exception);            
        }
    };
}
