package com.example.whole9yards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks, LocationListener {

    private TextView timerTV;
    //private TextView startStopTV;
    private long startTime;
    private int minute = 0;
    private int isClicked = 0;
    private Toast toast;
    private final int IMAGE_CODE = 123;
    ImageView image;
    File confImgFile;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference dbRef;
    DatabaseReference dbClients;

    private SignInButton signIn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_Code = 9000;


    private LinearLayout linearLayoutProfile;
    private LinearLayout linearLayoutSignIn;
    private Button signOut;
    private TextView Name, Email;
    private ImageView profilepic;

    private GoogleApiClient locationClient;
    private Geocoder g;

    private LinearLayout send;

    public ArrayList<String> addresses;
    private ArrayList<Fence> fences;
    private boolean wasInFence;
    private Timer timer;
    private boolean timerRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  linearLayoutProfile = (LinearLayout) findViewById(R.id.profileSectionLL); //1922
      //  linearLayoutSignIn = (LinearLayout) findViewById(R.id.SignInButtonLL);
      //  signOut = (Button) findViewById(R.id.SignOutButton);
      //  Name = (TextView) findViewById(R.id.nameID);
      //  Email = (TextView) findViewById(R.id.emailID);
      //  profilepic = (ImageView) findViewById(R.id.profilePicID);
      //  signOut.setOnClickListener(this);
      //  linearLayoutProfile.setVisibility(View.GONE);


      //  signIn = (SignInButton) findViewById(R.id.login_button);
      //  signIn.setOnClickListener(this);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        if (locationClient == null) {
            locationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("FIREBASE");
        confImgFile = null;

        g = new Geocoder(this);
        addresses = new ArrayList<String>();
        fences = new ArrayList<Fence>();

        SharedPreferences pref = getSharedPreferences("Fence", Context.MODE_PRIVATE);
        Log.v("mytag", "sharedPref boolean value " + pref.getBoolean("wasInFence", false));
        wasInFence = pref.getBoolean("wasInFence", false);
       //showPopup();
    }


    @Override
    protected void onStart() {
        locationClient.connect();
        super.onStart();

        dbClients = FirebaseDatabase.getInstance().getReference("dbClients");
        dbClients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String addressArg = snapshot.child("clientAddress").getValue().toString();
                    addresses.add(addressArg);

                }
                for (String s : addresses) {
                    Fence f = createFence(s);
                    if (f != null) {
                        fences.add(f);
                    }
                }
                dbClients.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Fence createFence(String s) {
        Fence f = null;
        try {
            List<Address> a = g.getFromLocationName(s, 1);
            if (a.size() > 0) {
                Address fenceAddress = a.get(0);
                f = new Fence(fenceAddress.getLongitude() + .01, fenceAddress.getLongitude() - .01,
                        fenceAddress.getLatitude() - .01, fenceAddress.getLatitude() + .01);
                return f;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (f != null) {
            Log.v("mytag", "in createFence " + f.toString());
        }
        return f;
    }

    public boolean inAFence(double lat, double lon) {
        for (Fence f : fences) {
            //Log.v("mytag", f.toString());
            if (lat >= f.getLeft() && lat <= f.getRight()
                    && lon >= f.getBottom() && lon <= f.getTop()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("mytag", "GPS Connected.");
        try {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(locationClient);
            if (loc != null) {
                Log.v("mytag", "" + loc.getLatitude() + ", " + loc.getLongitude());
            }
            LocationRequest r = new LocationRequest();
            r.setInterval(1000);
            r.setFastestInterval(500);
            r.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, r, this);
        } catch (SecurityException ex) {//security exception is not a subclass of Exception
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
         if(timerTV!= null && timerTV.getText().toString().equals("00:00")){
            timerRunning = false;
        } else {
            timerRunning = true;
        }

        try {
            Location currentLoc = LocationServices.FusedLocationApi.getLastLocation(locationClient);
            Log.v("mytag", "in onLocationChanged" + currentLoc.getLatitude() + ", " + currentLoc.getLongitude());


            boolean currentlyInFence = inAFence(currentLoc.getLatitude(), currentLoc.getLongitude());
            if(currentlyInFence){
                //showToast("In a fence");
            }
            Log.v("mytag", "\nwasInFence: " + wasInFence + "\n currentlyInFence: " + currentlyInFence);
            checkTransitions(currentlyInFence);


        } catch (SecurityException ex) {//security exception is not a subclass of Exception
            ex.printStackTrace();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    public void checkTransitions(boolean currentlyInFence) {
        SharedPreferences sharedPref = getSharedPreferences("Fence",
                Context.MODE_PRIVATE);



        if ((wasInFence) && currentlyInFence) {
            //was already in the fence
            wasInFence = true;
            timerRunning = true;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("wasInFence", true);
            editor.apply();

        } else if ((!wasInFence|| !timerRunning) && currentlyInFence) {
            //just entered the fence
            startTimer();
            timerRunning = true;
            wasInFence = true;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("wasInFence", true);
            editor.apply();
        } else if (wasInFence && !currentlyInFence) {
            wasInFence = false;
            View v = new View(this);
            stopTimer(v);
            //SEND EMAIL HERE
            showPopup();


            timerRunning = false;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("wasInFence", false);
            editor.apply();
            showToast("Leaving fence");
        } else if (!wasInFence && !currentlyInFence) {
            wasInFence = false;
            View v = new View(this);
            stopTimer(v);
            timerRunning = false;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("wasInFence", false);
            editor.apply();
        }
    }

    public void stopTimer(View v) {
        timer.cancel();
        timerTV.setText("00:00");
        //startStopTV.setText("Start Job");
        minute = 0;

    }

    //This method starts the timer. This will eventually
    //be automated to start and stop based on the Geofence.
    public void startTimer() {


        timer = new Timer();

        if (isClicked == 0) {

            //startStopTV = findViewById(R.id.startStopTV);
            //startStopTV.setText("Stop Job");


            startTime = System.currentTimeMillis();


            timer.scheduleAtFixedRate(new TimerTask() {


                @Override
                public void run() {

                    //long minutesStart = TimeUnit.MILLISECONDS.toMinutes(startTime);
                    //long secondsStart = TimeUnit.MILLISECONDS.toSeconds(startTime);
                    long secondsCalc = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);

                    //Log.v("MYTAG", secondsCalc + "");

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
            //startStopTV.setText("Start Job");


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

        super.onActivityResult(requestCode, resultCode, x);


        if (requestCode == IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(confImgFile.getAbsolutePath());
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else {


            if (REQ_Code == requestCode) {

                GoogleSignInResult resultOfGoogleSignIn = Auth.GoogleSignInApi.getSignInResultFromIntent(x);
                handleResult(resultOfGoogleSignIn);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        //    case R.id.login_button:
        //        signIn();
        //        break;
        //    case R.id.SignOutButton:
        //        signOut();
        //        break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {


        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_Code);


    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount signInAccount = result.getSignInAccount();
            String name = signInAccount.getDisplayName();
            String email = signInAccount.getEmail();
            //String img_url = signInAccount.getPhotoUrl().toString();

            Name.setText(name);
            Email.setText(email);
            //Glide.with(this).load(img_url).into(profilepic);

            updateUI(true);
        } else {

            updateUI(false);
        }


    }

    private void updateUI(boolean isLogin) {

        if (isLogin) {
            Log.v("mytag", "in updateUI, isLogin");
            linearLayoutProfile.setVisibility(View.VISIBLE);
            linearLayoutSignIn.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
        } else {

            Log.v("mytag", "in updateUI, isnotLogin");
            linearLayoutProfile.setVisibility(View.GONE);
            linearLayoutSignIn.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
        }


    }


    public void sendEmail(View v) {


        Log.v("MyTAGE", "about to go into thread");


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {


                    Log.v("MyTAGE", "Email method is called");
                    Mail mail = new Mail();
                    try {
                        mail.send();

                        Log.v("MyTAGE", "Email is sent");

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

    public void calendarIntent(View v) {
        //Intent x = new Intent(this,ClientsActivity.class);
        //startActivity(x);
    }


}
