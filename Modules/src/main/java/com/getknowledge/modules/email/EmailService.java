package com.getknowledge.modules.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {

    @Autowired
    private MailSender mailSender;

    public void send(String toAddress, String fromAddress, String subject, String msgBody) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toAddress);
        msg.setSubject(subject);
        msg.setText(msgBody);
        mailSender.send(msg);
    }

}
