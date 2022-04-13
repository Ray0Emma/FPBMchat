package com.example.tp7_asyntask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CreateGroup extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final String TAG = "farah";
    private static final java.util.UUID UUID = null;
    // view for image view
    private ShapeableImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    private FloatingActionButton add_room;
    private TextInputLayout room_name;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        add_room = (FloatingActionButton) findViewById(R.id.nex);
        room_name = (TextInputLayout) findViewById(R.id.room_name_edittext);

        imageView = findViewById(R.id.group_image);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
        }

        // on pressing btnSelect SelectImage() is called
        imageView.bringToFront();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Objects.requireNonNull(room_name.getEditText()).getText().toString().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), AddMembers.class);
                    intent.putExtra("room_name", room_name.getEditText().getText().toString());
                    intent.putExtra("filePath", filePath);
                    startActivity(intent);
//                    room_name.getEditText().setText(null);
                }
            }
        });

    }

    //    log in
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String currentUserID = mAuth.getCurrentUser().getUid();
                String currentUserName = mAuth.getCurrentUser().getDisplayName();
                String currentUserEmail = mAuth.getCurrentUser().getEmail();

                HashMap<String,String> userInfo = new HashMap<>();

                userInfo.put("id",currentUserID);
                userInfo.put("name", currentUserName);
                userInfo.put("email", currentUserEmail);

                rootRef.child("Users").child(currentUserID).setValue(userInfo);

                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();


                // Sign in failed

                if (resultCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this,
                            "no internet con",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                Toast.makeText(this,
                        resultCode,
                        Toast.LENGTH_LONG)
                        .show();
                // Close the app
                finish();
            }

        }
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
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
            imageView.setImageBitmap(bitmap);
        }
        if (requestCode == 2) {
            uploadImage();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CreateGroup.this,
                                    "RAK khrejtyyy!",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
//    @Override
//    protected void onActivityResult(int requestCode,
//                                    int resultCode,
//                                    Intent data) {
//
//        super.onActivityResult(requestCode,
//                resultCode,
//                data);
//
//        // checking request code and result code
//        // if request code is PICK_IMAGE_REQUEST and
//        // resultCode is RESULT_OK
//        // then set image in the image view
//        if (requestCode == PICK_IMAGE_REQUEST
//                && resultCode == RESULT_OK
//                && data != null
//                && data.getData() != null) {
//
//            // Get the Uri of data
//            filePath = data.getData();
//
//            // Setting image on image view using Bitmap
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore
//                        .Images
//                        .Media
//                        .getBitmap(
//                                getContentResolver(),
//                                filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            imageView.setImageBitmap(bitmap);
//        }
//        if(requestCode==2)
//        {
//            uploadImage();
//        }
//    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
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
                                            .makeText(CreateGroup.this,
                                                    "Image Uploaded!!",
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
                                    .makeText(CreateGroup.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

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
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }
}