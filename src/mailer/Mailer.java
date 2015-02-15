package mailer;

/**
 *
 * @author blah-blah
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {

        //Variable Declerations
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        /* ThreadPoolExecutor arguments are:
         First Argument = corePoolSize = 5
         Second Arguement = maximumPoolSize = 5
         Third Argument = keepAliveTime = 5
         Fourth Argument = unit (Time-unit) = TimeUnit.SECONDS
         Fifth Argument = workQueue
         Sixth Argument = threadFactory
        
         We can increase ThreadPoolExecutor according to our requirement
         */
        ThreadPoolExecutor Executor = new ThreadPoolExecutor(5, 5, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());

        // List of Emails = 5 (We can change accordingly)
        List<String[]> EmailList = new ArrayList<>(5);
        String query = "SELECT * FROM EmailQueue where status = '0'";   //SQL Query to get email queue

        try {
            conn = DBUtils.getConnection();             //Connection with singleton
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();

            while (result.next()) {
                String user[] = new String[4];
                user[0] = result.getString("from_email_address");
                user[1] = result.getString("to_email_address");
                user[2] = result.getString("subject");
                user[3] = result.getString("body");

                //Add Each element in EmailList
                EmailList.add(user);

                //Testing Size = 5. We can change it accordingly
                if (EmailList.size() == 5) {

                    //Final List of 5 Emails to send
                    final List<String[]> EList = new ArrayList<>(EmailList);
                    Executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            sendMail(EList);
                        }
                    });
                    //After sending clearing EmailList so that next 5 enteries will be inserted in EmailList
                    EmailList.clear();
                }
            }
        } catch (Exception e) {
            DBUtils.errorHandler("Error while retriving:", e);
        } finally {
            DBUtils.closeConnection();          // Closing Connection
        }

        //Checking whether all has been sent or not from Email List
        //If not then send first
        if (!EmailList.isEmpty()) {
            final List<String[]> Elist = EmailList;
            Executor.execute(new Runnable() {
                @Override
                public void run() {
                    sendMail(Elist);
                }
            });
            EmailList.clear();
        }

        //Finally Terminate the Executor
        while (!Executor.isTerminated()) {
            Executor.shutdown();
            Executor.awaitTermination(10, TimeUnit.DAYS);
        }
    }

    /**
     * Method for Extracting final mail data.
     *
     * @param {list} Email
     * @return void
     */
    private static void sendMail(List<String[]> EList) {
        for (String[] EList1 : EList) {
            String from = EList1[0];
            String to = EList1[1];
            String subject = EList1[2];
            String body = EList1[3];
            Send(from, to, subject, body);
        }
    }

    /**
     * Method for sending the mail using Gmail SMTP Server
     *
     * @param {String} from
     * @param {String} to
     * @param {String} subject
     * @param {String} body
     */
    private static void Send(final String from, String to, String subject, String body) {

        //SMTP settings
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Authentication 
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(from, "xxxxxx");
                    }
                });

        try {
            //Create meassge using MimeMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            //Send message
            Transport.send(message);

            System.out.println("message sent successfully");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
