//package com.example.whole9yards;
//
//import android.Manifest;
//import android.accounts.AccountManager;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
//import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
//import com.google.api.client.util.ExponentialBackOff;
//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.Message;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Properties;
//
//import javax.activation.DataHandler;
//import javax.activation.DataSource;
//import javax.activation.FileDataSource;
//import javax.mail.BodyPart;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.Session;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//
//import com.example.whole9yards.InternetDetector;
//import com.example.whole9yards.Utils;
//
//public class TestGmailClass extends AppCompatActivity {
//
//    FloatingActionButton sendFabButton;
//    EditText edtToAddress, edtSubject, edtMessage, edtAttachmentData;
//    Toolbar toolbar;
//    GoogleAccountCredential mCredential;
//    ProgressDialog mProgress;
//    private static final String PREF_ACCOUNT_NAME = "accountName";
//    private static final String[] SCOPES = {
//            GmailScopes.GMAIL_LABELS,
//            GmailScopes.GMAIL_COMPOSE,
//            GmailScopes.GMAIL_INSERT,
//            GmailScopes.GMAIL_MODIFY,
//            GmailScopes.GMAIL_READONLY,
//            GmailScopes.MAIL_GOOGLE_COM
//    };
//    private InternetDetector internetDetector;
//    private final int SELECT_PHOTO = 1;
//    public String fileName = "";
//
//    private com.google.api.services.gmail.Gmail mService = null;
//    private Exception mLastError = null;
//    private View view = sendFabButton;
//    private MainActivity activity;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        HttpTransport transport = AndroidHttp.newCompatibleTransport();
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//        mService = new com.google.api.services.gmail.Gmail.Builder(transport, jsonFactory, credential).setApplicationName(getResources().getString(R.string.app_name)).build();
//        this.activity = activity;
//
//        mimeMessage = createEmail(to, from, subject, body);
//        response = sendMessage(mService, user, mimeMessage);
//
//        init();
//
//
//
//    }
//
//    private void init() {
//        // Initializing Internet Checker
//
//        // Initialize credentials and service object.
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());
//
//        // Initializing Progress Dialog
//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Sending...");
//
//
//    }
//
//    private void showMessage(View view, String message) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
//    }
//
//
//    // Method for Checking Google Play Service is Available
//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        return connectionStatusCode == ConnectionResult.SUCCESS;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        // Method to send email
//        private String sendMessage(Gmail service,
//                                   String userId,
//                                   MimeMessage email)
//                throws MessagingException, IOException {
//            Message message = createMessageWithEmail(email);
//            // GMail's official method to send email with oauth2.0
//            message = service.users().messages().send(userId, message).execute();
//
//            System.out.println("Message id: " + message.getId());
//            System.out.println(message.toPrettyString());
//            return message.getId();
//        }
//
//        // Method to create email Params
//        private MimeMessage createEmail(String to,
//                                        String from,
//                                        String subject,
//                                        String bodyText) throws MessagingException {
//            Properties props = new Properties();
//            Session session = Session.getDefaultInstance(props, null);
//
//            MimeMessage email = new MimeMessage(session);
//            InternetAddress tAddress = new InternetAddress(to);
//            InternetAddress fAddress = new InternetAddress(from);
//
//            email.setFrom(fAddress);
//            email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
//            email.setSubject(subject);
//
//            // Create Multipart object and add MimeBodyPart objects to this object
//            Multipart multipart = new MimeMultipart();
//
//            // Changed for adding attachment and text
//            // email.setText(bodyText);
//
//            BodyPart textBody = new MimeBodyPart();
//            textBody.setText(bodyText);
//            multipart.addBodyPart(textBody);
//
//
//            //Set the multipart object to the message object
//            email.setContent(multipart);
//            return email;
//        }
//
//        private Message createMessageWithEmail(MimeMessage email)
//                throws MessagingException, IOException {
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            email.writeTo(bytes);
//            String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
//            Message message = new Message();
//            message.setRaw(encodedEmail);
//            return message;
//        }
//
//
//
//
//}
