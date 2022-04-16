package com.example.tp7_asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class IndexActivity extends AppCompatActivity {

    private FloatingActionButton add_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        add_room = (FloatingActionButton) findViewById(R.id.add_grp);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);
            }
        });
        
        displayGroupes();
    }

    private void displayGroupes() {
    }
}