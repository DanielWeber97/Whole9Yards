package com.example.whole9yards;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView timerTV;
    private long startTime;
    private int minute =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        showPopup();
    }


    public void startOnClick(View v){

        startTime = System.currentTimeMillis();

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                //long minutesStart = TimeUnit.MILLISECONDS.toMinutes(startTime);
                //long secondsStart = TimeUnit.MILLISECONDS.toSeconds(startTime);
                long secondsCalc = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);

                Log.v("MYTAG", secondsCalc + "");

                timerTV = findViewById(R.id.timer);
                if(secondsCalc % 60 == 0){
                    minute++;
                }
                if(minute>=10){
                    if(secondsCalc%60 < 10) {
                        timerTV.setText(minute + ":" +  "0"+secondsCalc % 60 );
                    } else{
                        timerTV.setText(minute + ":" + secondsCalc % 60 + "");
                    }
                } else{
                    if(secondsCalc%60 < 10) {
                        timerTV.setText("0"+minute + ":" + "0" +secondsCalc % 60);
                    } else{
                        timerTV.setText("0"+minute + ":" + secondsCalc % 60 + "");
                    }

                }

                // Your database code here
            }
        }, 1000, 1000);    /// set to run every second


    }

    public void showPopup(){
        String[] singleChoiceItems = {"Thanks for working today!\nA confirmation email has been sent to your boss and client."};
        int itemSelected = 0;
        new AlertDialog.Builder(this)
                .setTitle("All Done!")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                    }
                })
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .show();
    }

}
