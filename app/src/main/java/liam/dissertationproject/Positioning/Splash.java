/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 13:50
 */

package liam.dissertationproject.Positioning;

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

    private void doWork() {
        while (progressStatus < 100) {
            progressStatus += 10;
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

    private void startApp() {
        Intent intent = new Intent(Splash.this, StartMenu.class);
        startActivity(intent);
    }

}


