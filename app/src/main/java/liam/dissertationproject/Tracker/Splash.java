/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 30/03/18 15:40
 */

package liam.dissertationproject.Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Splash extends AppCompatActivity {
    private TextView tv;
    private TextView iv;
    private ProgressBar progressBar;

    private int progressStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.splash_progress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();
    }

        private void doWork(){
            while (progressStatus < 100) {
                progressStatus+=10;
                progressBar.setProgress(progressStatus);
                {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    private void startApp(){
            Intent intent = new Intent(Splash.this, StartMenu.class);
            startActivity(intent);
    }

    }


