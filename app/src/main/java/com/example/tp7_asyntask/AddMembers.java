package com.example.tp7_asyntask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddMembers extends AppCompatActivity {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private TextView room;
    private FloatingActionButton add_room;



    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        room = (TextView) findViewById(R.id.room_name_header);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            String room_name = (String) b.get("room_name");
            room.setText("Add members to " + room_name);

            add_room = (FloatingActionButton) findViewById(R.id.nex);
            add_room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    add icon, admin, recentMessage,members (int iduser)
                    HashMap<String, String> groupdetail = new HashMap();
                    String id = UUID.randomUUID().toString();
                    groupdetail.put("id",id);
                    groupdetail.put("name", room_name);
                    root.child("GroupDetail").child(id).setValue(groupdetail);
//                    root.updateChildren(map);

                    uploadImage();

                }
            });
        }

        displayAddedUsers();

        final TextInputLayout edittext = (TextInputLayout) findViewById(R.id.add_members);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(AddMembers.this, "hi", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });


    }

    private void uploadImage() {

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Uri filePath = (Uri) b.get("filePath");
        if (filePath != null) {

            // Code for showing progressDialog while uploading
                            ProgressDialog progressDialog
                                    = new ProgressDialog(this);
                            progressDialog.setTitle("Creating...");
                            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                                    progressDialog.dismiss();
                                                    Toast
                                                            .makeText(AddMembers.this,
                                                                    "Group Created!!",
                                                                    Toast.LENGTH_SHORT)
                                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                                            progressDialog.dismiss();
                                            Toast
                                                    .makeText(AddMembers.this,
                                                            "Failed " + e.getMessage(),
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                // Progress Listener for loading
                                                // percentage on the dialog box
                                                @Override
                                                public void onProgress(
                                                        UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress
                                                            = (100.0
                                                            * taskSnapshot.getBytesTransferred()
                                                            / taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage(
                                                            "Created "
                                                                    + (int) progress + "%");
                                                }
                                            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void displayAddedUsers() {
        ListView mListView = (ListView)findViewById(R.id.list_of_members);
        TextView txtv = findViewById(R.id.member_email);
        DatabaseReference usersRef = root.child("Users");

        Log.d("TAG","farah");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);

                    Log.d("TAG", email);

                    Members memb = new Members(name,email);

                    Members[] members = new Members[]{memb};

//                 txtv.setText(email);
//                        array.add(email);

                    ArrayAdapter<Members> adapter = new ArrayAdapter<Members>(AddMembers.this, android.R.layout.simple_list_item_1, members);
                    mListView.setAdapter(adapter);

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersRef.addListenerForSingleValueEvent(eventListener);

    }



}