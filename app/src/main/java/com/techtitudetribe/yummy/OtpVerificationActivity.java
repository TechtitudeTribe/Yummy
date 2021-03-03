package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private String phoneNumber,address;
    private String otpId;
    private FirebaseAuth mAuth;
    private TextView otpVerification;
    private EditText otpText;
    private TextView otpTimer,otpResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        phoneNumber = getIntent().getStringExtra("number").toString();
        mAuth=FirebaseAuth.getInstance();


        otpVerification = (TextView) findViewById(R.id.otp_verification);
        otpText = (EditText) findViewById(R.id.otp_text);
        otpTimer = (TextView) findViewById(R.id.otp_timer);
        otpResend = (TextView) findViewById(R.id.otp_resend);

        initiateOtp();
        reverseTimer(60,otpTimer,otpResend);


        otpVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        otpResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateOtp();
                reverseTimer(60,otpTimer, otpResend);
                otpResend.setVisibility(View.GONE);
            }
        });
    }

    private void checkValidations() {
        if (otpText.getText().toString().isEmpty())
        {
            Toast.makeText(this,"OTP is missing...",Toast.LENGTH_LONG).show();
        } else if(otpText.getText().toString().length()!=6)
        {
            Toast.makeText(this, "Invalid OTP...", Toast.LENGTH_SHORT).show();
        } else
        {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,otpText.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void initiateOtp() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpId=s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            String currentUser = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(currentUser))
                                    {
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("Name","Username");
                                        hashMap.put("Contact Number",phoneNumber);
                                        //hashMap.put("Address",address);
                                        hashMap.put("Email","xyz@example.com");

                                        userRef.child(currentUser).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful())
                                                {
                                                    Intent intent = new Intent(OtpVerificationActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    String mssg = task.getException().getMessage();
                                                    Toast.makeText(OtpVerificationActivity.this, "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(OtpVerificationActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else
                        {
                            String mssg = task.getException().getMessage();
                            Toast.makeText(OtpVerificationActivity.this, "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void reverseTimer(int seconds, final TextView tv, TextView otpResend)
    {
        new CountDownTimer(seconds*1000 + 1000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished/1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds));
            }

            @Override
            public void onFinish() {
                tv.setText("00:00:00");
                otpResend.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}