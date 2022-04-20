package com.example.tp7_asyntask.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7_asyntask.AddMembers;
import com.example.tp7_asyntask.R;
import com.example.tp7_asyntask.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private Uri downloadUrl;
    private DatabaseReference root;

    FirebaseStorage storage;
    StorageReference storageReference;

    HashMap<String, String> userInfo = new HashMap();
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private String currentUserID;
    private String encodedImgae = "default";
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                         imageUri = result.getData().getData();
//                        try {
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            binding.imageProfile.setImageBitmap(bitmap);
//                            encodedImgae = encodeimage(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
                        // Setting image on image view using Bitmap
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(
                                            getContentResolver(),
                                            imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        binding.imageProfile.setImageBitmap(bitmap);
                        encodedImgae = encodeimage(bitmap);
//
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setListeners();
    }

    private void setListeners() {
        binding.texSignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });

        binding.editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);

            }
        });

        binding.submitSignUp.setOnClickListener(v -> {

            String name = binding.inputName.getText().toString();
            if (name.matches("[a-zA-Z]+")) {
                binding.erreurName.setVisibility(View.INVISIBLE);
                binding.inputName.setBackgroundResource(R.drawable.background_input);
                String firstname = binding.inputFirstName.getText().toString();
                if (firstname.matches("[a-zA-Z]+")) {
                    binding.erreurFirstname.setVisibility(View.INVISIBLE);
                    binding.inputFirstName.setBackgroundResource(R.drawable.background_input);
                    String username = binding.inputEmail.getText().toString();
                    if (username.length() > 10) {
                        binding.erreurUsername.setVisibility(View.INVISIBLE);
                        binding.inputEmail.setBackgroundResource(R.drawable.background_input);
                        String password = binding.inputPassword.getText().toString();
                        if (password.length() > 6) {
                            binding.erreurPassword.setVisibility(View.INVISIBLE);
                            binding.inputPassword.setBackgroundResource(R.drawable.background_input);
                            String confirmpassword = binding.inputConfirmPassword.getText().toString();
                            if (password.equals(confirmpassword)) {
                                binding.erreurConfirmpassword.setVisibility(View.INVISIBLE);
                                binding.inputConfirmPassword.setBackgroundResource(R.drawable.background_input);
                                signUp(username, password, firstname, name);
                            } else {
                                binding.erreurConfirmpassword.setText("le mot de passe n'est pas le même");
                                binding.erreurConfirmpassword.setVisibility(View.VISIBLE);
                                binding.inputConfirmPassword.setBackgroundResource(R.drawable.error_input_shadow);
                            }
                        } else {
                            binding.erreurPassword.setText("entrez plus de 10 caractères");
                            binding.erreurPassword.setVisibility(View.VISIBLE);
                            binding.inputPassword.setBackgroundResource(R.drawable.error_input_shadow);
                        }

                    } else {
                        binding.erreurUsername.setText("utilisez une adresse email correcte");
                        binding.erreurUsername.setVisibility(View.VISIBLE);
                        binding.inputEmail.setBackgroundResource(R.drawable.error_input_shadow);
                    }
                } else {
                    binding.erreurFirstname.setText("utilisez juste des alphabets");
                    binding.erreurFirstname.setVisibility(View.VISIBLE);
                    binding.inputFirstName.setBackgroundResource(R.drawable.error_input_shadow);
                }
            } else {
                binding.erreurName.setText("utilisez juste des alphabets");
                binding.erreurName.setVisibility(View.VISIBLE);
                binding.inputName.setBackgroundResource(R.drawable.error_input_shadow);
            }

//            uploadImage();


        });
    }

    public void signUp(String username, String password, String firstname, String lastname) {
        loading(true);
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            currentUserID = mAuth.getCurrentUser().getUid();
                            HashMap<String, String> userInfo = new HashMap<>();

                            userInfo.put("id", currentUserID);
                            userInfo.put("name", firstname+" "+lastname);
                            userInfo.put("email", username);
                            userInfo.put("profile", encodedImgae);
                            User user = new User(username, firstname, lastname, encodedImgae);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    goToSignIN();
                                }
                            });
                        } else {
                            loading(false);
                            Toast.makeText(SignUpActivity.this, "Authentication faild", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToSignIN() {
        Intent intent = new Intent(this.getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }

    public void loading(boolean isLoading) {
        if (isLoading) {
            binding.submitSignUp.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.texSignIn.setVisibility(View.INVISIBLE);
        } else {
            binding.submitSignUp.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.texSignIn.setVisibility(View.INVISIBLE);
        }

    }

    public String encodeimage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayInputStream);
        byte[] bytes = byteArrayInputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }


}