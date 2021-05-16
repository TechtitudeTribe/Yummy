package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyOrderDetailActivity extends AppCompatActivity {

    private DatabaseReference orderRef;
    private View confirmView, placedView, deliveredView;
    private ImageView confirmImage,placedImage,deliveredImage,trackImage;
    private TextView confirmText, placedText, deliveredText;
    private String key;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private TextView trackOrder, trackName, trackDescription, trackAmount;
    private RelativeLayout trackOrderLayout;
    private boolean isOpen = false;
    private ProgressBar progressBar;
    private LinearLayout deliveryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        key = getIntent().getStringExtra("key");
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Orders").child(key);

        trackOrder = (TextView) findViewById(R.id.order_details_track_order);
        trackOrderLayout = (RelativeLayout) findViewById(R.id.order_details_track_order_relative_layout);
        progressBar = (ProgressBar) findViewById(R.id.order_item_description_progress_bar);

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        confirmView = (View) findViewById(R.id.track_order_view_confirm);
        placedView = (View) findViewById(R.id.track_order_view_placed);
        deliveredView = (View) findViewById(R.id.track_order_view_delivered);

        trackName = (TextView) findViewById(R.id.order_item_description_name);
        trackAmount = (TextView) findViewById(R.id.order_item_description_price);
        trackDescription = (TextView) findViewById(R.id.order_item_description_des);
        trackImage = (ImageView) findViewById(R.id.order_item_description_image);

        confirmImage = (ImageView) findViewById(R.id.track_order_image_confirm);
        placedImage = (ImageView) findViewById(R.id.track_order_image_placed);
        deliveredImage = (ImageView) findViewById(R.id.track_order_image_delivered);

        confirmText = (TextView) findViewById(R.id.track_order_text_confirmation);
        placedText = (TextView) findViewById(R.id.track_order_text_placed);
        deliveredText = (TextView) findViewById(R.id.track_order_text_delivered);
        deliveryLayout = (LinearLayout) findViewById(R.id.delivery_layout);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String status = snapshot.child("itemStatus").getValue().toString();
                    String amount = snapshot.child("itemTotalAmount").getValue().toString();
                    String name = snapshot.child("itemNames").getValue().toString();
                    String description = snapshot.child("itemDescription").getValue().toString();

                    trackAmount.setText(amount);
                    trackDescription.setText(description);
                    trackName.setText(name);
                    progressBar.setVisibility(View.GONE);
                    trackImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.food_image));

                    if (status.equals("Ordered"))
                    {
                        confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_white_background));
                        placedView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_white_background));
                        confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        placedImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        deliveryLayout.setVisibility(View.VISIBLE);
                        confirmText.setText("Confirmation Pending");
                    }else
                    if(status.equals("Confirmed"))
                    {
                        confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        placedView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_white_background));
                        confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        placedImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        deliveryLayout.setVisibility(View.VISIBLE);
                        confirmText.setText("Confirmed");
                    }else
                    if (status.equals("Cancelled"))
                    {
                        confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        placedView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_white_background));
                        confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        placedImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        deliveryLayout.setVisibility(View.GONE);
                        confirmText.setText("Cancelled");
                    }
                    else
                    {
                        confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        placedView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_red_background));
                        confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        placedImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        deliveryLayout.setVisibility(View.VISIBLE);
                        confirmText.setText("Confirmed");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen)
                {
                    trackOrderLayout.startAnimation(close);
                    trackOrderLayout.setVisibility(View.GONE);
                    isOpen=false;
                }
                else
                {
                    trackOrderLayout.startAnimation(open);
                    trackOrderLayout.setVisibility(View.VISIBLE);
                    isOpen=true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);
        if(isOpen)
        {
            trackOrderLayout.startAnimation(close);
            trackOrderLayout.setVisibility(View.GONE);
            isOpen=false;
        }
        else
        {
            super.onBackPressed();
            isOpen=true;
        }

    }

}