package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartItemDescriptionActivity extends AppCompatActivity {

    private ImageView itemImage,increase, decrease;
    private TextView itemName, itemDescription, itemPrice, itemQuantity,noAddress,itemImageUrl;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef,addressRef;
    private String currentUser, key;
    private ProgressBar progressBar,addProgressBar;
    private RelativeLayout addressLayout;
    private FrameLayout addressFrame;
    private RecyclerView cartAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_item_description);

        key = getIntent().getStringExtra("key");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart").child(key);
        addressRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Addresses");

        itemImage = (ImageView) findViewById(R.id.cart_item_description_image);
        itemName = (TextView) findViewById(R.id.cart_item_description_name);
        itemDescription = (TextView) findViewById(R.id.cart_item_description_des);
        itemPrice = (TextView) findViewById(R.id.cart_item_description_price);
        itemQuantity = (TextView) findViewById(R.id.cart_item_description_quantity);
        progressBar = (ProgressBar) findViewById(R.id.cart_item_description_progress_bar);
        increase = (ImageView) findViewById(R.id.cart_item_increase);
        decrease = (ImageView) findViewById(R.id.cart_item_decrease);
        addressLayout = (RelativeLayout) findViewById(R.id.cart_fragment_address_list_layout);
        noAddress = (TextView) findViewById(R.id.no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.cart_fragment_address_list_frame);
        addProgressBar = (ProgressBar) findViewById(R.id.cart_fragment_address_list_progress_bar);
        cartAddressList = (RecyclerView) findViewById(R.id.cart_fragment_address_list);
        itemImageUrl = (TextView) findViewById(R.id.cart_item_description_image_url);

        cartAddressList = (RecyclerView) findViewById(R.id.cart_fragment_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        cartAddressList.setLayoutManager(linearLayoutManager1);

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("itemName").getValue().toString();
                String description = snapshot.child("itemDescription").getValue().toString();
                String imageUrl = snapshot.child("itemImage").getValue().toString();
                String quantity = snapshot.child("itemQuantity").getValue().toString();
                String price = snapshot.child("itemCustomizedPrice").getValue().toString();

                itemName.setText(name);
                itemDescription.setText(description);
                itemQuantity.setText(quantity);
                itemPrice.setText(price);
                itemImageUrl.setText(imageUrl);
                Picasso.with(getApplicationContext()).load(imageUrl).placeholder(R.drawable.ic_default_food).into(itemImage);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayCartAddressList();
                }
                else
                {
                    noAddress.setVisibility(View.VISIBLE);
                    addressFrame.setVisibility(View.GONE);
                    addProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void displayCartAddressList() {
        Query sort = addressRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter,CartAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, CartAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        CartAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(CartAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

                        TextView textView = (TextView) cartAddressViewHolder.mView.findViewById(R.id.cart_address_layout_number);


                        cartAddressViewHolder.setAddress(myAddressAdapter.getAddress());
                        cartAddressViewHolder.setCount(myAddressAdapter.getCount());
                        addProgressBar.setVisibility(View.GONE);
                        cartAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                textView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                                Intent intent = new Intent(getApplicationContext(),OrderConfirmationActivity.class);
                                intent.putExtra("itemName",itemName.getText().toString());
                                intent.putExtra("totalPrice",itemPrice.getText().toString());
                                intent.putExtra("itemDescription",itemDescription.getText().toString());
                                intent.putExtra("itemQuantity",itemQuantity.getText().toString());
                                intent.putExtra("itemImageUrl",itemImageUrl.getText().toString());
                                startActivity(intent);
                                textView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                            }
                        });

                        switch (i%4)
                        {
                            case 0 : textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }
                    }
                };
        cartAddressList.setAdapter(fra);
    }

    public static class CartAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CartAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }


        public void setCount(String count) {
            TextView addressNumber = (TextView) mView.findViewById(R.id.cart_address_layout_number);
            addressNumber.setText(count);

        }

        public void setAddress(String address)
        {
            TextView addressText = (TextView) mView.findViewById(R.id.cart_address_layout_text);
            addressText.setText(address);
        }
    }
}