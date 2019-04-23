package com.example.whole9yards;


//imports for this class to work

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


// When I used this class and if you use gmail, I had to go into gmail and allow gmail emails to be sent with
// lesser secure application


public class Mail extends javax.mail.Authenticator {  //after extending, must override PasswordAuthentication

    //instance variables for methods to work

    private String _user;
    private String _pass;

    private String[] _to ;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;


    public Mail() {
        _host = "smtp.gmail.com"; // server to gmail to work
        _port = "465"; // gmail port number
        _sport = "465"; // gmail socketfactory port

        _user = "whole9yardsapp"; // username
        _pass = "Iphone$1234"; // password
        _from = "whole9yardsapp@gmail.com"; // email is sent from this email account

        _to = new String[] {"zglontz@gmail.com"};  // the address where the email is going to
        _subject = "test email"; // the subject of the email
        _body = "This is very cool. First email sent through app"; // the body of the email

        _debuggable = false; // says the mail server is not in debug mode.
        _auth = true; // just a boolean to say that this constructor has run and set all the variables to its correct information

        _multipart = new MimeMultipart();  // creating a mimepart to be added to the main mimemessage body in the future


        // these statements below are needed to add content handlers to the class.
        // these content handlers allow data to come in from the mail servers and turn them into something that java can understand

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }


    // method to set the username and password to log into your mailserver

    public Mail(String user, String pass) {
        _user = user;
        _pass = pass;
    }

    // this method is to send the message through the server (basically the final step)

    public boolean send() throws Exception {


        Properties props = _setProperties();

                //checking if the enteries are not null for the parts of the email
        if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {

            //this creates a session on the mail server that can be used to send the message
            Session session = Session.getInstance(props, this);


            // Mime stands for Multipurpose Internet Mail Extensions
            // a mimemessage is basically the standard format the all emails with javamil are sent through
            // basically like a protocol of how each email should be formatted
            MimeMessage msg = new MimeMessage(session);

            //setting the email address of the mimemessage that the email will be sent from
            msg.setFrom(new InternetAddress(_from));

            //creating an array with values of the string to where the message is being sent
            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }

            //setting more of the format protocols of a mime message
            // setting the recepient address
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            // setting the subject and the send date
            msg.setSubject(_subject);
            msg.setSentDate(new Date());

            // setting the message and also creating the body
            // the message body has to be added to the mimemessage
            // its like the message is a body part of the mimemessage that hasn't been added yet
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

            // if you want to attach a file to the message
            // method is defined below
            addAttachment("filename");

            // adding the message body part to the mimemessage
            msg.setContent(_multipart);

            // object that allows the mimemessage to be transported to the server and sent
            Transport transport = session.getTransport("smtps");
            transport.connect(_host, 465,_user, _pass);
            Transport.send(msg);

            return true;  //if the message is sent, return true
        } else {
            return false;  // if the initial conditions were not correct, return false
        }
    }

    // sending an attachement method
    // like the adding a message part to the body of the mimemessage, it is the same concept for the attachement part
    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart); // adding the part to the body
    }


    // just a method to hold the username and password in an object
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }



    //this method sets the properties for the mail server
    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);

        if(_debuggable) {  //sets the server to be able to be debugged
            props.put("mail.debug", "true");
        }

        if(_auth) {   //sets the property that the server should be authenticated if all the information about the message has been set
            props.put("mail.smtp.auth", "true");
        }

        //setting more properties for mail server to work
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");

        return props;
    }

    // getter method to get the body
    public String getBody() {

        return _body;
    }

    // setter method to set the body
    public void setBody(String _body) {

        this._body = _body;
    }

}