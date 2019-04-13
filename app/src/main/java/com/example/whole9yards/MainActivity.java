package com.example.whole9yards;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private TextView timerTV;
    private TextView startStopTV;
    private long startTime;
    private int minute = 0;
    private int isClicked = 0;
    private Toast toast;
    private final int IMAGE_CODE = 123;
    ImageView image;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("FIREBASE");
        image = findViewById(R.id.imageView);

        //showPopup();
    }


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
        capture();
        showToast("Open camera");
    }


    public void capture() {
        File file  = new File(Environment.getExternalStorageDirectory(), (System.currentTimeMillis() + ".jpg"));
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext()
                        .getPackageName() + ".provider", file);
      //  i.setDataAndType(uri, "image/*");
        File imageFile = new File(uri.getPath());
        Log.v("mytag", uri.getPath());
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.putExtra("fileUri", uri);
        startActivityForResult(i,IMAGE_CODE);


        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
            Log.v("mytag",bitmap.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("mytag","file not found");
        }
        image.setImageBitmap(bitmap);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent x) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_CODE) {
            Log.v("mytag", "file saved");
        }



    }


    //This method takes the user to the client screen where
    //they can view all of the current clients. They can
    //also add or delete clients.
    public void clients(View v) {
        Intent x = new Intent(this, ClientsActivity.class);
        startActivity(x);
    }


    public void showPopup(View v) {
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

}
