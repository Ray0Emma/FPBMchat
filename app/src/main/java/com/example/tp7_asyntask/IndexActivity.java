package com.example.tp7_asyntask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tp7_asyntask.activities.SignInActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IndexActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 0;

    private FloatingActionButton add_room;
    private BottomNavigationView navView ;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private ListView list_view;
    private GroupsListAdapter adapter;
    private Intent grp;
    private FirebaseAuth mAuth;
    private ArrayList<Groups> groups = new ArrayList<>();
    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mAuth = FirebaseAuth.getInstance();
        list_view = findViewById(R.id.list_of_groups);
//        navView = findViewById(R.id.nav_view);
//        navView.inflateMenu(R.menu.bottom_nav_menu);
//
//        navView.setSelectedItemId(R.id.navigation_dashboard);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity

            Intent intent = new Intent(IndexActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
//            Toast.makeText(this,
//                    "Welcome " + FirebaseAuth.getInstance()
//                            .getCurrentUser()
//                            .getDisplayName(),
//                    Toast.LENGTH_LONG)
//                    .show();

            displayGroupes();
        }

        add_room = (FloatingActionButton) findViewById(R.id.add_grp);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);
            }
        });
        

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    private void displayGroupes() {
        DatabaseReference usersRef = root.child("GroupDetail");

        Log.d("TAG", "farah");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups.clear();
                Set<String> set = new HashSet<>();
                i=0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    i=0;
                    String icon = ds.child("icon").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String last_msg = ds.child("last_msg").getValue(String.class);
                    long members = ds.child("members").getChildrenCount();
                    String grpId = ds.child("id").getValue(String.class);

//                    Log.d("TAG", icon);
//                    Log.d("TAG", name);
//                    Log.d("TAG", String.valueOf(members));

                    i=0;
                    while (i < members){

                        String userId = ds.child("members").child(String.valueOf(i)).getValue(String.class);

//                        Log.d("TAG2", userId);

                        String currentUserID = mAuth.getCurrentUser().getUid();
                        if (currentUserID.equals(userId)) {
                            Groups gr = new Groups(name,icon);
//                            Groups gr = new Groups(name,icon,last_msg);
                            groups.add(gr);
                        }

                        i++;

                    }


                }
                adapter = new GroupsListAdapter(IndexActivity.this, R.layout.groups, groups);
                list_view.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int listItem, long l) {



//                        list_view.
                        TextView textView = (TextView) view.findViewById(R.id.grp_name);
                        ImageView icon = (ImageView) view.findViewById(R.id.group_image);

                        String room_name = textView.getText().toString();

                        if(room_name != null){

                            int room_img = icon.getId();

                            BitmapDrawable drawable = (BitmapDrawable) icon.getDrawable();



                            grp = new Intent(IndexActivity.this,GroupsActivity.class);
                            Log.d("fff", String.valueOf(getResources().getResourceName(room_img)));
                            grp.putExtra("room_name",room_name);

                            if (drawable != null){
                                Bitmap bitmap = drawable.getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                grp.putExtra("drawable",  byteArray);
                            }

                            startActivity(grp);

                        }


                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                HashMap<String, String> userInfo = new HashMap<>();

                userInfo.put("id", currentUserID);
                userInfo.put("name", currentUserName);
                userInfo.put("email", currentUserEmail);

//                add profile

                root.child("Users").child(currentUserID).setValue(userInfo);

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

    }


}