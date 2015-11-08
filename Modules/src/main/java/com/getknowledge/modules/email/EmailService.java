package com.getknowledge.modules.email;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.*;

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

    public void sendTemplate(String toAddress, String fromAddress,String subject, String templateName, String [] args) throws IOException {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toAddress);
        msg.setSubject(subject);
        msg.setText(getMessageFromTemplate(templateName , args));
        mailSender.send(msg);
    }

    private String getMessageFromTemplate(String name, String[] args) throws IOException {
        String templateName = "com.getknowledge.modules/mailTemplates/" + name + ".html";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer , "utf-8");
        String template = writer.toString();
        if (args != null) {
            int i = 0;
            while (template.contains(":?")) {
                if (i < args.length) {
                    template = StringUtils.replaceOnce(template , ":?", args[i]);
                    i++;
                }
                else break;
            }
        }
        return template;
    }
}
