package com.example.tp7_asyntask;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.example.tp7_asyntask.activities.SignInActivity;
import com.example.tp7_asyntask.localdatabase.GroupsLocalDB;
import com.google.firebase.auth.FirebaseAuth;

public class reception extends AppCompatActivity {

    GroupsLocalDB db ;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception);


        db = new GroupsLocalDB(reception.this);
        mAuth = FirebaseAuth.getInstance();


        new CountDownTimer(3000,100){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                /*if(mAuth.getCurrentUser() != null)
                {
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                }*/
                if (mAuth.getCurrentUser() != null)
                {
                    Intent intent = new Intent(reception.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(reception.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        }.start();
    }
}