package com.example.tp7_asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sayHi(View view){

        ExecutorService mExecutorService;
        mExecutorService = Executors.newSingleThreadExecutor();

        if (mExecutorService.isTerminated()) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }


        ExecutorService finalMExecutorService = mExecutorService;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                finalMExecutorService.shutdown();
                try {
                    if (!finalMExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        finalMExecutorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}