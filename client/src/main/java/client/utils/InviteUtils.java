package client.utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class InviteUtils {
    public ServerUtils server = new ServerUtils();

    public String generateRandomInviteCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public void sendInvitation(String recipientEmail, String inviteCode) {
        final String username = "testoopp@outlook.com"; //  email
        final String password = ".KWn6.WL#)m9KuL"; // password

        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com"); // Outlook SMTP host
        props.put("mail.smtp.port", "587"); // Outlook SMTP port

        // Create a Session object
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });



        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);
            // Set From: header field
            message.setFrom(new InternetAddress(username));
            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            // Set Subject: header field
            message.setSubject("Invitation to Our Event");
            String server = LanguageManager.getInstance().getServer(); // server.getURL()
            String serverUrl = server + "/";

            // Set Content: text
            message.setText("Dear friend,\n\nYou are invited to our event!\n" +
                    " Your invitation code is: " + inviteCode + "\n"+
                    " You can join the event at: " + serverUrl + "\n\nBest regards,\nEvent Team");
            // Send message
            Transport.send(message);
            System.out.println("Sent invitation to " + recipientEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
