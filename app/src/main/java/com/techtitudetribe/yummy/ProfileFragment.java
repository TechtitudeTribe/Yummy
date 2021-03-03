package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String currentUser;
    private TextView username, userEmail, userContactNumber,userLogo;
    private ProgressBar progressBar;
    private Animation logOutOpen, logOutClose;
    private RelativeLayout profileLogOut,logOutContainer,profileMyOrders,profileMyAddress;
    private TextView cancelAlert, confirmAlert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        username = (TextView) v.findViewById(R.id.profile_name);
        userEmail = (TextView) v.findViewById(R.id.profile_email);
        userContactNumber = (TextView) v.findViewById(R.id.profile_number);
        userLogo = (TextView) v.findViewById(R.id.profile_logo);
        progressBar = (ProgressBar) v.findViewById(R.id.profile_progress_bar);
        profileLogOut = (RelativeLayout) v.findViewById(R.id.profile_log_out);
        profileMyOrders = (RelativeLayout) v.findViewById(R.id.profile_my_orders);
        profileMyAddress = (RelativeLayout) v.findViewById(R.id.profile_my_adress);
        logOutContainer = (RelativeLayout) v.findViewById(R.id.log_out_alert_container);
        cancelAlert = (TextView) v.findViewById(R.id.cancel_alert);
        confirmAlert = (TextView) v.findViewById(R.id.confirm_alert);

        logOutOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.log_out_open);
        logOutClose = AnimationUtils.loadAnimation(getActivity(),R.anim.log_out_close);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if (snapshot.exists())
             {
                 String name = snapshot.child("Name").getValue().toString();
                 String email = snapshot.child("Email").getValue().toString();
                 String number = snapshot.child("Contact Number").getValue().toString();
                 char logo = name.charAt(0);
                 String logoString = String.valueOf(logo);

                 username.setText(name);
                 userEmail.setText(email);
                 userContactNumber.setText(number);
                 userLogo.setText(logoString);
                 progressBar.setVisibility(View.GONE);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyOrdersActivity.class);
                startActivity(intent);
            }
        });

        profileMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyAddressActivity.class);
                startActivity(intent);
            }
        });

        profileLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutContainer.setVisibility(View.VISIBLE);
                logOutContainer.startAnimation(logOutOpen);
            }
        });

        cancelAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutContainer.startAnimation(logOutClose);
                logOutContainer.setVisibility(View.GONE);
            }
        });

        confirmAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent mainIntent = new Intent(getActivity(),LoginActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });

        return v;
    }
}