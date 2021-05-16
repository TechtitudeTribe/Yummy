package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;

public class ShopCartItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef,addressRef;
    private String currentUser,key1;
    private RelativeLayout noCartLayout;
    private RelativeLayout addressLayout;
    private ProgressBar addProgressBar;
    private FrameLayout addressFrame;
    private RecyclerView cartAddressList;
    private TextView proceedToCheckout,noAddress;
    private int position = -1;
    private String address;
    private Boolean isOpen = false;
    private TextView totalAmount,cartItemNames,cartItemDescription;
    private TextView sellerId,shopId,shopName,shopContact,oldItemNames;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart_item);

        key1 = getIntent().getStringExtra("key");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart").child(key1);
        addressRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Addresses");

        progressBar = (ProgressBar) findViewById(R.id.my_shop_cart_progress_bar);
        noCartLayout = (RelativeLayout) findViewById(R.id.no_shop_cart_layout);
        addressLayout = (RelativeLayout) findViewById(R.id.cart_fragment_address_list_layout);
        addressFrame = (FrameLayout) findViewById(R.id.cart_fragment_address_list_frame);
        addProgressBar = (ProgressBar) findViewById(R.id.cart_fragment_address_list_progress_bar);
        proceedToCheckout = (TextView) findViewById(R.id.proceed_to_checkout);
        noAddress = (TextView) findViewById(R.id.no_address_found);
        totalAmount = (TextView) findViewById(R.id.shop_cart_total_amount);
        cartItemNames = (TextView) findViewById(R.id.shop_cart_item_names);
        cartItemDescription = (TextView) findViewById(R.id.shop_cart_item_description);
        oldItemNames = (TextView) findViewById(R.id.old_item_names);

        recyclerView = (RecyclerView) findViewById(R.id.my_shop_cart_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        cartAddressList = (RecyclerView) findViewById(R.id.cart_fragment_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        cartAddressList.setLayoutManager(linearLayoutManager1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.cart_shop_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this::displayCartItems);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this,R.color.creative_green),
                ContextCompat.getColor(this,R.color.creative_red),
                ContextCompat.getColor(this,R.color.creative_violet),
                ContextCompat.getColor(this,R.color.creative_sky_blue));

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                displayCartItems();
            }
        });

        sellerId = (TextView) findViewById(R.id.seller_id);
        shopId = (TextView) findViewById(R.id.shop_id);
        shopName = (TextView) findViewById(R.id.shop_name);
        shopContact = (TextView) findViewById(R.id.shop_contact);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String sellerID = snapshot.child("sellerId").getValue().toString();
                    String shopID = snapshot.child("shopId").getValue().toString();
                    String shopNAME = snapshot.child("shopName").getValue().toString();
                    String shopCONTACT = snapshot.child("shopContact").getValue().toString();
                    String otn = snapshot.child("itemNames").getValue().toString();

                    sellerId.setText(sellerID);
                    shopId.setText(shopID);
                    shopName.setText(shopNAME);
                    shopContact.setText(shopCONTACT);
                    oldItemNames.setText(otn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartRef.child("Cart Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    displayCartItems();
                    noCartLayout.setVisibility(View.GONE);
                }
                else
                {
                    noCartLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
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

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        proceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proceedToCheckout.getText().toString().equals("Select an Address"))
                {
                    if(isOpen)
                    {
                        addressLayout.startAnimation(close);
                        addressLayout.setVisibility(View.GONE);
                        isOpen=false;
                    }
                    else
                    {
                        addressLayout.startAnimation(open);
                        addressLayout.setVisibility(View.VISIBLE);
                        isOpen=true;
                    }
                }
                else
                {
                    if (TextUtils.isEmpty(address))
                    {
                        Toast.makeText(ShopCartItemActivity.this, "Please choose an address", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(),OrderConfirmationActivity.class);
                        intent.putExtra("itemName",cartItemNames.getText().toString());
                        intent.putExtra("totalPrice",totalAmount.getText().toString());
                        intent.putExtra("itemDescription",cartItemDescription.getText().toString());
                        intent.putExtra("address",address);
                        intent.putExtra("sellerId",sellerId.getText().toString());
                        intent.putExtra("shopId",shopId.getText().toString());
                        intent.putExtra("shopName",shopName.getText().toString());
                        intent.putExtra("shopContact",shopContact.getText().toString());
                        intent.putExtra("key",key1);
                        startActivity(intent);
                        addressLayout.startAnimation(close);
                        addressLayout.setVisibility(View.GONE);
                        proceedToCheckout.setText("Select an Address");
                        position = -1;
                        isOpen=false;
                    }
                }

            }
        });

        cartRef.child("Cart Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                String description ="";
                int count = 0;
                for(DataSnapshot totalCost : snapshot.getChildren())
                {
                    int foodCalorie = Integer.parseInt(totalCost.child("itemCustomizedPrice").getValue(String.class));
                    String foodName = totalCost.child("itemName").getValue(String.class);
                    String foodDescription = totalCost.child("itemName").getValue(String.class)+"  - Qty."+totalCost.child("itemQuantity").getValue(String.class)+"\n";
                    if (name.equals(""))
                    {
                        name = name + foodName;
                    }
                    else
                    {
                        name = name+", "+foodName;
                    }
                    count = count + foodCalorie;
                    description = description + foodDescription;
                    totalAmount.setText(String.valueOf(count));
                    cartItemNames.setText(name);
                    cartItemDescription.setText(description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void displayCartItems() {
        Query sort = cartRef.child("Cart Items").orderByChild("count");

        FirebaseRecyclerAdapter<CartItemAdapter,CartItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CartItemAdapter, CartItemViewHolder>(
                        CartItemAdapter.class,
                        R.layout.cart_item_layout,
                        CartItemViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(CartItemViewHolder cartItemViewHolder, CartItemAdapter cartItemAdapter, int i) {
                        cartItemViewHolder.setItemDescription(cartItemAdapter.getItemDescription());
                        cartItemViewHolder.setItemImage(cartItemAdapter.getItemImage(),getApplicationContext());
                        cartItemViewHolder.setItemName(cartItemAdapter.getItemName());
                        cartItemViewHolder.setItemQuantity(cartItemAdapter.getItemQuantity());
                        cartItemViewHolder.setItemCustomizedPrice(cartItemAdapter.getItemCustomizedPrice());
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);

                        RelativeLayout relativeLayout = (RelativeLayout) cartItemViewHolder.mView.findViewById(R.id.cart_item_main_background);
                        TextView name = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_quantity);
                        TextView price = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_description);
                        TextView priceTag = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_price_tag);
                        //LinearLayout relativeLayout = (LinearLayout) cartItemViewHolder.mView.findViewById(R.id.cart_item_description_layout);
                        ImageView delete = (ImageView) cartItemViewHolder.mView.findViewById(R.id.cart_item_delete);
                        RelativeLayout deleteLayout = (RelativeLayout) cartItemViewHolder.mView.findViewById(R.id.cart_item_delete_layout);
                        String key = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                /*priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));*/
                                delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                /*priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));*/
                                delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                //priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));*/
                                delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                //priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));*/
                                delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        cartItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(),CartItemDescriptionActivity.class);
                                intent.putExtra("key",key);
                                intent.putExtra("key1",key1);
                                startActivity(intent);
                            }
                        });

                        deleteLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cartRef.child("Cart Items").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if (task1.isSuccessful())
                                        {
                                            if (oldItemNames.getText().toString().contains(cartItemAdapter.getItemName()+","))
                                            {
                                                String newItemNames = oldItemNames.getText().toString().replace(cartItemAdapter.getItemName()+",","");
                                                HashMap hashMap = new HashMap();
                                                hashMap.put("itemNames",newItemNames);

                                                cartRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task2) {
                                                        if (task2.isSuccessful())
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Item removed successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            String message = task2.getException().getMessage();
                                                            Toast.makeText(getApplicationContext(), "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else if (oldItemNames.getText().toString().contains(","+cartItemAdapter.getItemName()))
                                            {
                                                String newItemNames = oldItemNames.getText().toString().replace(","+cartItemAdapter.getItemName(),"");
                                                HashMap hashMap = new HashMap();
                                                hashMap.put("itemNames",newItemNames);

                                                cartRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task3) {
                                                        if (task3.isSuccessful())
                                                        {
                                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                            Toast.makeText(getApplicationContext(),"Item removed successfully...",Toast.LENGTH_SHORT).show();

                                                        }
                                                        else
                                                        {
                                                            String message = task3.getException().getMessage();
                                                            Toast.makeText(getApplicationContext(), "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else if (oldItemNames.getText().toString().contains(cartItemAdapter.getItemName()))
                                            {
                                                String newItemNames = oldItemNames.getText().toString().replace(cartItemAdapter.getItemName(),"");

                                                cartRef.removeValue().addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task4) {
                                                        if (task4.isSuccessful())
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Item removed successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            String message = task4.getException().getMessage();
                                                            Toast.makeText(getApplicationContext(), "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                        else
                                        {
                                            String mssg = task1.getException().toString();
                                            Toast.makeText(getApplicationContext(), "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setItemName(String itemName)
        {
            TextView name = (TextView) mView.findViewById(R.id.cart_item_name);
            name.setText(itemName);
        }

        public void setItemCustomizedPrice(String itemCustomizedPrice)
        {
            TextView price = (TextView) mView.findViewById(R.id.cart_item_price);
            price.setText(itemCustomizedPrice);
        }

        public void setItemImage(String itemImage, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.cart_item_image);
            Picasso.with(context).load(itemImage).placeholder(R.drawable.ic_default_food).into(image);
        }

        public void setItemDescription(String itemDescription)
        {
            TextView description = (TextView) mView.findViewById(R.id.cart_item_description);
            description.setText(itemDescription);
        }

        public void setItemQuantity(String itemQuantity)
        {
            TextView quantity = (TextView) mView.findViewById(R.id.cart_item_quantity);
            quantity.setText("Quantity : "+itemQuantity);
        }
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

                        TextView text = (TextView) cartAddressViewHolder.mView.findViewById(R.id.cart_address_layout_text);

                        cartAddressViewHolder.setAddress(myAddressAdapter.getAddress());

                        addProgressBar.setVisibility(View.GONE);
                        cartAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position = i;
                                notifyDataSetChanged();
                                address = myAddressAdapter.getAddress();
                                proceedToCheckout.setText("Go for Checkout");
                            }

                        });

                        if (position==i)
                        {
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                            text.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                        }
                        else
                        {
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                            text.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
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


        public void setAddress(String address)
        {
            TextView addressText = (TextView) mView.findViewById(R.id.cart_address_layout_text);
            addressText.setText(address);
        }
    }

    @Override
    public void onBackPressed() {
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);
        if(isOpen)
        {
            addressLayout.startAnimation(close);
            addressLayout.setVisibility(View.GONE);
            isOpen=false;
        }
        else
        {
            super.onBackPressed();
            isOpen=true;
        }
    }
}