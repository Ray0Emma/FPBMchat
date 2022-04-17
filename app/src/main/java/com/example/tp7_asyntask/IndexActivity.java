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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IndexActivity extends AppCompatActivity {

    private FloatingActionButton add_room;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private ListView list_view;
    private GroupsListAdapter adapter;
    private FirebaseAuth mAuth;
    private ArrayList<Groups> groups = new ArrayList<>();
    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mAuth = FirebaseAuth.getInstance();
        list_view = findViewById(R.id.list_of_groups);
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
                Set<String> set = new HashSet<>();
                i=0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    i=0;
                    String icon = ds.child("icon").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String last_msg = ds.child("last_msg").getValue(String.class);
                    long members = ds.child("members").getChildrenCount();
                    String grpId = ds.child("id").getValue(String.class);

                    Log.d("TAG", icon);
                    Log.d("TAG", name);
                    Log.d("TAG", String.valueOf(members));

                    i=0;
                    while (i < members){

                        String userId = ds.child("members").child(String.valueOf(i)).getValue(String.class);

                        Log.d("TAG2", userId);

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

                        int room_img = icon.getId();

                        BitmapDrawable drawable = (BitmapDrawable) icon.getDrawable();



                        Intent grp = new Intent(IndexActivity.this,GroupsActivity.class);
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
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}