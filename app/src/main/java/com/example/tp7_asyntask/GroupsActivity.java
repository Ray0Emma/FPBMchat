package com.example.tp7_asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class GroupsActivity extends AppCompatActivity {

    private static final String TAG = "Farah";
    private TextView room ;
    private ImageView prev;

    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        room= findViewById(R.id.room_name_header);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            String room_name = (String) b.get("room_name");
            String room_id = (String) b.get("room_id");
            room.setText(room_name);
            displayChatMessages();
        }

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("GroupMessages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });

        prev = findViewById(R.id.btn_left);
        prev.bringToFront();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        Query query = FirebaseDatabase.getInstance().getReference().child("GroupMessages");

        FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .setLayout(R.layout.message)
//                        .setLifecycleOwner(this)   //Added this
                        .build();
        Log.d(TAG, "displayChatMessages:1 ");
        adapter = new FirebaseListAdapter<ChatMessage>(options){

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                Log.d(TAG, "displayChatMessages:2 ");
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                Log.e(TAG, "displayChatMessages3: "+messageUser);

                // Format the date before showing it
                messageTime.setText(DateFormat.format("HH:mm",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

    }

}