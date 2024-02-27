package org.example.emailaservice1.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.example.emailaservice1.dtos.SendEmailEventDto;
import org.example.emailaservice1.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



import java.util.Properties;

@Service
public class SendEmailEventConsumer {

    private  ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailEventDto eventDto = objectMapper.readValue(message, SendEmailEventDto.class);

        String to = eventDto.getTo();
        String from = eventDto.getFrom();
        String subject = eventDto.getSubject();
        String body = eventDto.getBody();

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        properties.put("mail.smtp.port", "587"); //TLS Port
        properties.put("mail.smtp.auth", "true"); //enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        Authenticator authenticator = new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("$GMAIL_USER","${GMAIL_PASS}");
            }
        };

        Session session = Session.getInstance(properties, authenticator);

        EmailUtil.sendEmail(session, to,subject,body);

    }


}
