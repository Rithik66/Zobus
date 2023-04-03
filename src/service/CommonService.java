package service;

import com.adventnet.mfw.service.Service;
import com.adventnet.persistence.DataObject;
import log.MyLogger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;

public class CommonService implements Service {
    public void sendMail(String subject,String messageToBeSent,String to){
        try{
            String email = "rithik.demo.1@gmail.com";
            String password = "hdzdztmiipygtqjf";
            MyLogger.run(email+" : "+password+" : "+to,Level.INFO);
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.google.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(email));
            message.setSubject(subject);
            message.setText(messageToBeSent);
            Transport.send(message);
        }catch(Exception e){
            MyLogger.run("Exception occured while mailing", Level.SEVERE);
            MyLogger.exceptionLogger(e);
        }
    }

    @Override
    public void create(DataObject dataObject) throws Exception {
        MyLogger.run("Server COLD START",Level.INFO);
    }

    @Override
    public void start() throws Exception {
        MyLogger.run("Server WARM START",Level.INFO);
    }

    @Override
    public void stop() throws Exception {
        MyLogger.run("Server STOP",Level.INFO);
    }

    @Override
    public void destroy() throws Exception {
        MyLogger.run("Server DESTROY",Level.INFO);
    }
}
