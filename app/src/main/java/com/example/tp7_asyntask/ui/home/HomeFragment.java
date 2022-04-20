package com.example.tp7_asyntask.ui.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp7_asyntask.MainActivity;
import com.example.tp7_asyntask.R;
import com.example.tp7_asyntask.activities.SignInActivity;
import com.example.tp7_asyntask.activities.User;
//import com.example.tp7_asyntask.databinding.FragmentHomeBinding;
import com.example.tp7_asyntask.databinding.HomeFragmentBinding;
import com.example.tp7_asyntask.localdatabase.GroupsLocalDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        Button btn = binding.button;
        TextView textView = binding.textHome;
        ImageView profileImage = binding.imageProfile;


        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null)
                {
                    profileImage.setImageBitmap(getCodedImage(user.image));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });




        textView.setText("Profile");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GroupsLocalDB db = new GroupsLocalDB();
                //String text = db.getUserName();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public Bitmap getCodedImage(String imageStr)
    {
        byte[] bytes = Base64.decode(imageStr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}