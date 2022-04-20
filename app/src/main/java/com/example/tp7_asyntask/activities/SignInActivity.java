package com.example.tp7_asyntask.activities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7_asyntask.IndexActivity;
import com.example.tp7_asyntask.MainActivity;
import com.example.tp7_asyntask.R;
import com.example.tp7_asyntask.databinding.ActivitySignInBinding;

import com.example.tp7_asyntask.localdatabase.GroupsLocalDB;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // ********************
        /*BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();*/
        // ********************

        mAuth = FirebaseAuth.getInstance();

        setListeners();
    }

    private void setListeners()
    {
        binding.texCreateNewAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });

        binding.submitSignIn.setOnClickListener(v -> {

            String username = binding.inputEmail.getText().toString();

            Pattern pattern = Pattern.compile("[A-Za-z0-9._-]+@[A-Za-z0-9._-]+.[A-Za-z]");
            Matcher mat = pattern.matcher(username);
            if (mat.matches() && username.length() > 10)
            {
                binding.inputEmail.setBackgroundResource(R.drawable.background_input);
                binding.erreurUsername.setVisibility(View.INVISIBLE);
                String password = binding.inputPassword.getText().toString();
                if (password.length() > 6)
                {
                    binding.inputPassword.setBackgroundResource(R.drawable.background_input);
                    binding.erreurPassword.setVisibility(View.INVISIBLE);
                    signin(username, password);
                }
                else
                {
                    binding.inputPassword.setBackgroundResource(R.drawable.error_input_shadow);
                    binding.erreurPassword.setText("utilisez 6 caractères au minimum");
                    binding.erreurPassword.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                binding.inputEmail.setBackgroundResource(R.drawable.error_input_shadow);
                binding.erreurUsername.setText("entrez une adresse email valide");
                binding.erreurUsername.setVisibility(View.VISIBLE);
            }


        });
    }

    public void signin(String username, String password)
    {
        loading(true);
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Welcome in FPBMChatGroups", Toast.LENGTH_SHORT).show();
                            gohome();
                        } else {
                            loading(false);
                            Toast.makeText(SignInActivity.this, "Authentication faild", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void gohome()
    {
        new CountDownTimer(1500, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SignInActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        }.start();

    }


    public void loading(boolean isLoading)
    {
        if(isLoading)
        {
            binding.submitSignIn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.texCreateNewAccount.setVisibility(View.INVISIBLE);
        }
        else
        {
            binding.submitSignIn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.texCreateNewAccount.setVisibility(View.VISIBLE);
        }

    }


}