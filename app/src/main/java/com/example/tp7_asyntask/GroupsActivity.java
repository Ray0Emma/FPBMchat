package com.example.tp7_asyntask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class GroupsActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 22;

    private static final String TAG = "Farah";
    private TextView room ;
    private ImageView prev, iconImg , imageView;
    private TextInputLayout addfile;
    private DatabaseReference root;
    private ScrollView scV;
    private FirebaseAuth mAuth;
    private String room_name;
    private Uri filePath;

    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        room= findViewById(R.id.room_name_header);
        iconImg = findViewById(R.id.group_image);
        addfile = findViewById(R.id.file);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            room_name = (String) b.get("room_name");
            String room_id = (String) b.get("room_id");
            String icon = (String) b.get("icon");
            byte[] drawable = (byte[]) b.get("drawable");
            room.setText(room_name);
            if (icon != null){
                Picasso.get().load(icon).into(iconImg);
            }
            if (drawable != null){
                Bitmap bmp = BitmapFactory.decodeByteArray(drawable, 0, drawable.length);
                iconImg.setImageBitmap(bmp);
            }

            displayChatMessages();
        }
        root = FirebaseDatabase.getInstance().getReference().child("GroupMessages").child(room_name);

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
                        .child(room_name)
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

        addfiles();

        prev = findViewById(R.id.btn_left);
        prev.bringToFront();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addfiles() {

        addfile.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CharSequence options[] = new CharSequence[]
//                        {
//                              "Images"  ,
//                                "PDF files",
//                                "Ms Word Files"
//                        };
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        PICK_IMAGE_REQUEST);
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

        Query query = FirebaseDatabase.getInstance().getReference().child("GroupMessages").child(room_name);

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
//                @SuppressLint("ResourceType") TextView my_msg = v.findViewById(R.layout.message);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                Log.e(TAG, "displayChatMessages3: "+messageUser);

                // Format the date before showing it
                messageTime.setText(DateFormat.format("HH:mm AA",
                        model.getMessageTime()));

//                String currentUserName = mAuth.getCurrentUser().getDisplayName();
//                if(model.getMessageUser().equals(currentUserName)){
////
//                }
            }
        };

        listOfMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

            // Setting image on image view using Bitmap
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView = findViewById(R.id.img_file);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    };

}