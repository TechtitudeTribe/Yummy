package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EmailSignupActivity extends AppCompatActivity {

    private EditText email,password,confirmPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String currentUser;
    private TextView signUp;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Admin");

        LoadingBar = new ProgressDialog(this);
        email = (EditText) findViewById(R.id.sign_up_email);
        password = (EditText) findViewById(R.id.sign_up_password);
        confirmPassword = (EditText) findViewById(R.id.sign_up_confirm_password);
        signUp = (TextView) findViewById(R.id.create_account_sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

    }

    private void checkValidations() {
        if (TextUtils.isEmpty(email.getText().toString()))
        {
            Toast.makeText(this, "Invalid e-mail...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password.getText().toString()))
            {
                Toast.makeText(this, "Password is missing...", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(confirmPassword.getText().toString()))
                {
                    Toast.makeText(this, "Confirm your password...", Toast.LENGTH_SHORT).show();
                }else if (!password.getText().toString().equals(confirmPassword.getText().toString()))
                    {
                        Toast.makeText(this, "Password don't match...", Toast.LENGTH_SHORT).show();
                    }else
                        {
                            LoadingBar.show();
                            LoadingBar.setContentView(R.layout.progress_bar);
                            LoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LoadingBar.setCanceledOnTouchOutside(true);
                            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful())
                                            {

                                                String currentUser = firebaseAuth.getCurrentUser().getUid();
                                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                                userRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.hasChild(currentUser))
                                                        {
                                                            HashMap hashMap = new HashMap();
                                                            hashMap.put("Name","Username");
                                                            hashMap.put("Contact Number","0000000000");
                                                            //hashMap.put("Address",address);
                                                            hashMap.put("Email",email.getText().toString());

                                                            userRef.child(currentUser).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        sendUserToMainActivity();
                                                                        Toast.makeText(EmailSignupActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                                                                        LoadingBar.dismiss();
                                                                    }
                                                                    else
                                                                    {
                                                                        String mssg = task.getException().getMessage();
                                                                        Toast.makeText(EmailSignupActivity.this, "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                                                        LoadingBar.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else
                                                        {
                                                            sendUserToMainActivity();
                                                            LoadingBar.dismiss();
                                                            Toast.makeText(EmailSignupActivity.this, "You are login successfully...", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(EmailSignupActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                                LoadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(EmailSignupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}