package com.example.tp7_asyntask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLDisplay;

public class AddMembers extends AppCompatActivity {

    private DatabaseReference root;
    private TextView room;
    private ListView list_view;
    private MembersListAdapter adapter;
    private ArrayList<Members> members = new ArrayList<>();
    private FloatingActionButton add_room;
    private String id = UUID.randomUUID().toString();
    private FirebaseAuth mAuth;
    private Uri downloadUrl;
    private ImageView prev;
    static int i = 0;


    FirebaseStorage storage;
    StorageReference storageReference;
    String room_name;
    HashMap<String, String> groupdetail = new HashMap();
    HashMap<String, String> groupmembers = new HashMap();
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        root = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        room = (TextView) findViewById(R.id.room_name_header);
        list_view = findViewById(R.id.list_of_members);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        intent = new Intent(AddMembers.this, GroupsActivity.class);


        final EditText edittext = findViewById(R.id.add_members);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if (b != null) {
            room_name = (String) b.get("room_name");
            room.setText("Add members to " + room_name);

            add_room = (FloatingActionButton) findViewById(R.id.nex);
            add_room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    add icon, admin, recentMessage,members (int iduser)
                    String currentUserID = mAuth.getCurrentUser().getUid();
//                    String currentUserName = mAuth.getCurrentUser().getDisplayName();
                    if (!groupmembers.isEmpty()) {

                        groupdetail.put("id", id);
                        groupdetail.put("name", room_name);
                        groupdetail.put("adminId", currentUserID);
//                        groupdetail.put("adminName", currentUserName);
                        groupdetail.put("createdAt", String.valueOf(new Date().getTime()));
                        root.child("GroupDetail").child(id).setValue(groupdetail);
                        root.child("GroupDetail").child(id).child("members").setValue(groupmembers);
                        intent.putExtra("room_name", room_name);
                        intent.putExtra("room_id", id);

                        uploadImage();

                    }

                }
            });
        }


        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    displayAddedUsers(edittext);
                }
                return false;
            }
        });
        prev = findViewById(R.id.btn_left);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    private void uploadImage() {


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Uri filePath = (Uri) b.get("filePath");
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(AddMembers.this);
            progressDialog.setTitle("Creating...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "groupIcons/"
                                    + id + ".jpg");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {


//                        Log.d("fffdown","test");

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

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri;
                                    //Do what you want with the url
                                    Log.d("fffdown", String.valueOf(downloadUrl));
                                    groupdetail.put("icon", downloadUrl.toString());
                                    root.child("GroupDetail").child(id).setValue(groupdetail);

                                    root.child("GroupDetail").child(id).child("members").setValue(groupmembers);

                                    startAct();

                                }

                            });

//                            startActivity(intent);
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
                        @NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress
                            = (100.0
                            * taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Created "
                                    + (int) progress + "%");

                    Log.d("fffpro", String.valueOf(progress));
                    if( progress == 100.0){

                    }
                }
            });
        } else {
            Log.d("TAGfilep", String.valueOf(filePath));
        }
    }

    private void startAct() {
        intent.putExtra("icon", downloadUrl.toString());
        startActivity(intent);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


    private void displayAddedUsers(EditText E) {
        DatabaseReference usersRef = root.child("Users");

        Log.d("TAG", "farah");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String profile = ds.child("profile").getValue(String.class);
                    String usersID = ds.child("id").getValue(String.class);


                    Log.d("TAG", email);

                    if (E.getText().toString().equals(email)) {

                        Members memb = new Members(usersID);
                        Members m = new Members(name, profile);

//                        Log.d("TAG", memb.getName());

                        members.add(m);
                        groupmembers.put(String.valueOf(i), usersID);
                        i++;
                    }

                }
                String currentUserID = mAuth.getCurrentUser().getUid();
                groupmembers.put(String.valueOf(i), currentUserID);
                adapter = new MembersListAdapter(AddMembers.this, R.layout.added_member, members);
                list_view.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                E.setText("");

                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int listItem, long l) {

//                        adapter.remove(listItem);
                        members.remove(adapterView.getItemAtPosition(listItem));
                        adapter.notifyDataSetChanged();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}