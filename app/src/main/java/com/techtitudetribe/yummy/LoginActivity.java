package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Animation bottomUp,topRightCorner,bottomDown,emailBottomDown, emailBottomUp;
    private RelativeLayout methodLayout,loginMethodEmail,loginHeader;
    private TextView sendOtp,continueWithEmail,continueWithGoogle;
    private EditText phoneNumber;
    private static final int RC_SIGN_IN =1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_up_animation);
        bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_down_animation);
        topRightCorner = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_top_corner_animation);
        emailBottomDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.email_bottom_down);
        emailBottomUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.email_bottom_up);

        methodLayout = (RelativeLayout) findViewById(R.id.login_method_relative_layout);
        loginMethodEmail = (RelativeLayout) findViewById(R.id.login_methods_email);
        loginHeader = (RelativeLayout) findViewById(R.id.login_header);
        sendOtp = (TextView) findViewById(R.id.send_otp);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        continueWithGoogle = (TextView) findViewById(R.id.continue_with_google);
        continueWithEmail = (TextView) findViewById(R.id.continue_with_email);
        mAuth = FirebaseAuth.getInstance();

        methodLayout.setAnimation(bottomUp);
        loginHeader.setAnimation(topRightCorner);

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phoneNumber.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this, "Enter contact number", Toast.LENGTH_SHORT).show();
                }
                else if (phoneNumber.getText().toString().length()<10)
                {
                    Toast.makeText(LoginActivity.this, "Invalid contact number...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(),OtpVerificationActivity.class);
                    intent.putExtra("number","+91"+phoneNumber.getText().toString());
                    startActivity(intent);
                }

            }
        });

        continueWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        continueWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodLayout.startAnimation(bottomDown);
                methodLayout.setVisibility(View.GONE);
                loginMethodEmail.setVisibility(View.VISIBLE);
                loginMethodEmail.startAnimation(emailBottomDown);
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Connection Failed...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            /*LoadingBar.show();
            LoadingBar.setContentView(R.layout.progress_bar);
            LoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LoadingBar.setCanceledOnTouchOutside(true);*/

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this,"Plz wait",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Can't connect with Google", Toast.LENGTH_SHORT).show();
                //LoadingBar.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            String currentUser = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(currentUser))
                                    {

                                        HashMap hashMap = new HashMap();
                                        hashMap.put("Name","Username");
                                        hashMap.put("Contact Number","+910000000000");
                                        //hashMap.put("Address",address);
                                        hashMap.put("Email","xyz@example.com");
                                        userRef.child(currentUser).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful())
                                                {
                                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    String mssg = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                    else
                                    {
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //sendUserToMainActivity();
                            //LoadingBar.dismiss();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            //sendUserToLoginActivity();
                            Toast.makeText(LoginActivity.this, "Not authenticated : " + message, Toast.LENGTH_SHORT).show();
                            //LoadingBar.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
        {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}