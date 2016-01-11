package com.getknowledge.modules.email;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Service("EmailService")
public class EmailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private TraceService trace;

    public void send(String toAddress, String fromAddress, String subject, String msgBody) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toAddress);
        msg.setSubject(subject);
        msg.setText(msgBody);
        mailSender.send(msg);
    }

    public void sendTemplate(String toAddress, String fromAddress,String subject, String templateName, String [] args)  {

        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true,"UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(getMessageFromTemplate(templateName , args),true);
        } catch (MessagingException | IOException e) {
            trace.logException("Can not receive email for " + toAddress,e, TraceLevel.Warning);
        }
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
