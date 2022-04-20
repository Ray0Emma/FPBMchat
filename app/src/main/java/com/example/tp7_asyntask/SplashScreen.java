package com.example.tp7_asyntask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7_asyntask.activities.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private DatabaseReference root;
    private FirebaseAuth mAuth;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else {

            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference usersRef = root.child("Users").child(userId);

            Log.d("TAG", "farah");

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = (String) dataSnapshot.child("name").getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            Toast.makeText(this,
                    "Welcome " + name,
                    Toast.LENGTH_LONG)
                    .show();



            Intent intent = new Intent(this, IndexActivity.class);
            startActivity(intent);
            finish();
        }


    }
}
