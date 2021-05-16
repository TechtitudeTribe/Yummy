package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String currentUser;
    private TextView username, userEmail, userContactNumber,userLogo;
    private ProgressBar progressBar;
    private Animation logOutOpen, logOutClose;
    private RelativeLayout profileLogOut,logOutContainer,profileMyOrders,profileMyAddress;
    private TextView cancelAlert, confirmAlert;
    private ImageView editUserProfile;

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
        editUserProfile = (ImageView) v.findViewById(R.id.edit_user_profile);
        profileMyAddress = (RelativeLayout) v.findViewById(R.id.profile_my_adress);
        logOutContainer = (RelativeLayout) v.findViewById(R.id.log_out_alert_container);
        cancelAlert = (TextView) v.findViewById(R.id.cancel_alert);
        confirmAlert = (TextView) v.findViewById(R.id.confirm_alert);

        logOutOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.log_out_open);
        logOutClose = AnimationUtils.loadAnimation(getActivity(),R.anim.log_out_close);

        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.edit_profile_layout,
                        (RelativeLayout)v.findViewById(R.id.update_profile_relative_layout)
                );
                builder.setView(view);

                TextView cancelProfileUpdate = (TextView) view.findViewById(R.id.update_user_profile_cancel);
                EditText editUsername = (EditText) view.findViewById(R.id.edit_username);
                EditText editEmail = (EditText) view.findViewById(R.id.edit_email_id);
                EditText editPhone = (EditText) view.findViewById(R.id.edit_phone);
                TextView confirmProfileUpdate = (TextView) view.findViewById(R.id.update_user_profile_update);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.edit_user_profile_progress_bar);
                //userUpdateProfile.setVisibility(View.VISIBLE);
                //userUpdateProfile.startAnimation(profileOpen);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String name = snapshot.child("Name").getValue().toString();
                            String mail = snapshot.child("Email").getValue().toString();
                            String phone = snapshot.child("Contact Number").getValue().toString();
                            editUsername.setText(name);
                            editEmail.setText(mail);
                            editPhone.setText(phone);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final AlertDialog alertDialog = builder.create();

                cancelProfileUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //userUpdateProfile.setVisibility(View.GONE);
                        //userUpdateProfile.startAnimation(profileClose);
                        alertDialog.dismiss();
                    }
                });
                confirmProfileUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(editUsername.getText().toString()))
                        {
                            Toast.makeText(v.getContext(), "Username is invalid", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else if (TextUtils.isEmpty(editEmail.getText().toString()))
                        {
                            Toast.makeText(v.getContext(), "E-mail is invalid", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else if (TextUtils.isEmpty(editPhone.getText().toString()))
                        {
                            Toast.makeText(v.getContext(), "Phone number is not valid...", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            HashMap hashMap = new HashMap();
                            hashMap.put("Name",editUsername.getText().toString());
                            hashMap.put("Email",editEmail.getText().toString());
                            hashMap.put("Contact Number",editPhone.getText().toString());

                            userRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(v.getContext(),"User profile updated successfully...",Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        String message = task.getException().getMessage();
                                        Toast.makeText(v.getContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

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
                 userContactNumber.setText("+91"+number);
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