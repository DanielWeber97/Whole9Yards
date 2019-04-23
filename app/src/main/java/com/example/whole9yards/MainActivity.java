package com.example.whole9yards;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.signin.SignInOptions;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private TextView timerTV;
    private TextView startStopTV;
    private long startTime;
    private int minute = 0;
    private int isClicked = 0;
    private Toast toast;
    private final int IMAGE_CODE = 123;
    ImageView image;
    private File confImgFile;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference dbRef;

    private SignInButton signIn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_Code = 9000;


    private LinearLayout linearLayoutProfile;
    private LinearLayout linearLayoutSignIn;
    private Button signOut;
    private TextView Name, Email;
    private ImageView profilepic;

    private LinearLayout send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayoutProfile = (LinearLayout) findViewById(R.id.profileSectionLL); //1922
        linearLayoutSignIn = (LinearLayout) findViewById(R.id.SignInButtonLL);
        signOut = (Button) findViewById(R.id.SignOutButton);
        Name = (TextView) findViewById(R.id.nameID);
        Email = (TextView) findViewById(R.id.emailID);
        profilepic = (ImageView) findViewById(R.id.profilePicID);
        signOut.setOnClickListener(this);
        linearLayoutProfile.setVisibility(View.GONE);


        signIn = (SignInButton) findViewById(R.id.login_button);
        signIn.setOnClickListener(this);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();


        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("FIREBASE");
        confImgFile = null;

        //showPopup();
    }

    //This method starts the timer. This will eventually
    //be automated to start and stop based on the Geofence.
    public void startOnClick(View v) {


        Timer timer = new Timer();

        if (isClicked == 0) {

            startStopTV = findViewById(R.id.startStopTV);
            startStopTV.setText("Stop Job");


            startTime = System.currentTimeMillis();


            timer.scheduleAtFixedRate(new TimerTask() {


                @Override
                public void run() {

                    //long minutesStart = TimeUnit.MILLISECONDS.toMinutes(startTime);
                    //long secondsStart = TimeUnit.MILLISECONDS.toSeconds(startTime);
                    long secondsCalc = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);

                    Log.v("MYTAG", secondsCalc + "");

                    timerTV = findViewById(R.id.timer);
                    if (secondsCalc % 60 == 0) {
                        minute++;
                    }
                    if (minute >= 10) {
                        if (secondsCalc % 60 < 10) {
                            timerTV.setText(minute + ":" + "0" + secondsCalc % 60);
                        } else {
                            timerTV.setText(minute + ":" + secondsCalc % 60 + "");
                        }
                    } else {
                        if (secondsCalc % 60 < 10) {
                            timerTV.setText("0" + minute + ":" + "0" + secondsCalc % 60);
                        } else {
                            timerTV.setText("0" + minute + ":" + secondsCalc % 60 + "");
                        }

                    }

                    // Your database code here
                }
            }, 1000, 1000);    /// set to run every second


            isClicked = 1;

        } else if (isClicked == 1) {

            //cancel timer
            timer.cancel();

            //reset instance variable
            minute = 0;

            //rest boolean variable
            isClicked = 0;

            //reset step
            timerTV.setText("00:00");
            startStopTV.setText("Start Job");


        }


    }

    //This method takes the user to the Google Calendar API
    public void calendar(View v) {
        showToast("Open calendar activity");
    }

    //This method takes the user to the Google Maps API
    public void map(View v) {
        showToast("Open map activity");
    }

    //This method opens the camera to take a confirmation picture.
    public void takePicture(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for the file of the image taken in the Camera application
        confImgFile = getPhotoUri(System.currentTimeMillis() + ".jpg");

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", confImgFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, IMAGE_CODE);
        }
    }

    //Used in testing to make sure that the file was properly saved.
    //Now only useful to remind user that they didn't take the picture.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent x) {
        if (requestCode == IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(confImgFile.getAbsolutePath());
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public File getPhotoUri(String name) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Whole9Yards");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.v("mytag", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + name);

        return file;
    }


    //This method takes the user to the client screen where
    //they can view all of the current clients. They can
    //also add or delete clients.
    public void clients(View v) {
        Intent x = new Intent(this, ClientsActivity.class);
        startActivity(x);
    }

    //This method creates a popup to let the user know that their confirmation
    //picture has been emailed to the boss and client.
    public void showPopup() {
        String[] singleChoiceItems = {"Thanks for working today!\nA confirmation email has been sent to your boss and client."};
        int itemSelected = 0;
        android.app.Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("All Done!")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                    }
                })
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .show();

        //dialog.show();
    }

    //Helper method to show a toast with the given String.
    //Overrides current toast to prevent them from staying
    //on screen for too long.
    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void taskCompleted() {
        //send email to boss and client

    }


    public void pushTestData(View v) {
        EditText t = findViewById(R.id.text);
        String s = t.getText().toString();

        dbRef.push().setValue(s);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.login_button:
                signIn();
                break;
            case R.id.SignOutButton:
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){


        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_Code);


    }

    private void signOut(){

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                 updateUI(false);
            }
        });


    }

    private void handleResult(GoogleSignInResult result){

        if(result.isSuccess() ){

            GoogleSignInAccount signInAccount = result.getSignInAccount();
            String name = signInAccount.getDisplayName();
            String email = signInAccount.getEmail();
            //String img_url = signInAccount.getPhotoUrl().toString();

            Name.setText(name);
            Email.setText(email);
            //Glide.with(this).load(img_url).into(profilepic);

            updateUI(true);
        }
        else{

            updateUI(false);
        }



    }

    private void updateUI(boolean isLogin){

        if(isLogin){

            linearLayoutProfile.setVisibility(View.VISIBLE);
            linearLayoutSignIn.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
        }
        else{

            linearLayoutProfile.setVisibility(View.GONE);
            linearLayoutSignIn.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQ_Code == requestCode){

            GoogleSignInResult resultOfGoogleSignIn = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(resultOfGoogleSignIn);
        }
    }

    public void sendEmail(View v){


        Log.v("MyTAGE","about to go into thread" );



        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {




                    Log.v("MyTAGE","Email method is called" );
                    Mail mail = new Mail();
                    try {
                        mail.send();

                        Log.v("MyTAGE","Email is sent" );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }







                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();














    }

    public void calendarIntent(View v){
        //Intent x = new Intent(this,ClientsActivity.class);
        //startActivity(x);
    }



}
