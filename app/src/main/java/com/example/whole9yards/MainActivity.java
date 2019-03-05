package com.example.whole9yards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView timerTV;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
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
                timerTV.setText(secondsCalc + "");   // eventually convert this to minutes and seconds

                // Your database code here
            }
        }, 1000, 1000);    /// set to run every second


    }

}
