package com.example.goddragonfish.customprogressdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.goddragonfish.customprogressdemo.CustomProgressBar.CustomCircleBar;
import com.example.goddragonfish.customprogressdemo.CustomProgressBar.CustomLinearBar;
import com.example.goddragonfish.customprogressdemo.CustomProgressBar.OnProgressBarListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnProgressBarListener{

    private CustomLinearBar pbLinear;
    private CustomCircleBar pbCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLinear=findViewById(R.id.pb_linear);
        pbCircle=findViewById(R.id.pb_cir);
        pbLinear.setOnProgressBarListener(this);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbLinear.incrementProgressBy(1);
                        pbCircle.incrementProgressBy(1);
                    }
                });
            }
        }, 1000, 100);
    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
            pbLinear.setProgress(0);
        }
    }
}
