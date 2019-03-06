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
    private TextView startStopTV;
    private long startTime;
    private int minute = 0;
    private int isClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        setTheme(R.style.StyleSplash);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        showPopup();
    }


    public void startOnClick(View v){


        Timer timer = new Timer();

        if(isClicked == 0){

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


            isClicked = 1;

        }

        else if(isClicked == 1){

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
