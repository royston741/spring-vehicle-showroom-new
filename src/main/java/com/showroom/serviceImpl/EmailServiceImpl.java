package com.showroom.serviceImpl;

import com.showroom.dto.EmailDto;
import com.showroom.service.EmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

//    @Autowired
//    private MailSender simpleMailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("#{'${spring.mail.cc}'.split(',')}")
    private String[] ccEmails;

    @Override
    @Async
    public void sendMail(EmailDto email) {
//        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
//        simpleMailMessage.setFrom("roy71ston@gmail.com");
//        simpleMailMessage.setTo(toMail);
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(message);
//        simpleMailSender.send(simpleMailMessage);

        // create mine message for additional things like attachments
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            // from
            mimeMessageHelper.setFrom(emailFrom);
            // to
            mimeMessageHelper.setTo(email.getEmailTo());
            // subject
            mimeMessageHelper.setSubject(email.getSubject());
            if (email.getHtmlContentLink()!=null) {
                //Html
                mimeMessageHelper.setText(getHtml(email.getHtmlContentLink()), true);
            } else {
                mimeMessageHelper.setText(email.getTextMessage());
            }
            // for every cc
            Arrays.stream(ccEmails).toList().forEach(mail -> {
                try {
                    mimeMailMessage.addRecipients(Message.RecipientType.CC, mail);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });

            if(email.getAttachments().size()>0) {
                // for every attachment
                email.getAttachments().forEach((attachment) -> {
                    try {
                        mimeMessageHelper.addAttachment(attachment.getFilename(), new ByteArrayResource(attachment.getAttachment()));
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            log.error("Error in sendMail {}", e);
            throw new RuntimeException("Email not sent");
        }
    }

    public String getHtml(String link) {
        // creating a String builder object
        StringBuilder html = new StringBuilder();
        String result = "";
        // read from a file location
        try (FileReader fr = new FileReader(link); BufferedReader br = new BufferedReader(fr);) {
            String val;
            // while the next line is present append in html
            while ((val = br.readLine()) != null) {
                html.append(val);
            }
            // convert to string
            result = html.toString();
        } catch (Exception e) {
            log.error("Error in getHtml {}", e);
            throw new RuntimeException("Html not generate");
        }
        return result;
    }

}
