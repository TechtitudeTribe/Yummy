
package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MenuItemActivity extends AppCompatActivity {

    private HorizontalScrollView horizontalMenuScrollView;
    private Animation leftAnimation,bottomUpAnim;
    private RecyclerView foodItemList;
    private DatabaseReference foodRef,cartRef;
    private String key, category;
    private LinearLayout noFoodItemFound;
    private ProgressBar progressBar;
    private long cartItems=0;
    private FirebaseAuth mAuth;
    private String currentUser;
    private LinearLayout bestseller, fastFood, veg, nonVeg, others;
    private ImageView bestsellerImage, fastFoodImage, vegImage, nonVegImage, othersImage;
    private TextView bestsellerText, fastFoodText, vegText, nonVegText, othersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        key = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");
        foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Bestseller");

        leftAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
        bottomUpAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_up_animation);

        progressBar = (ProgressBar) findViewById(R.id.menu_item_progress_bar);
        noFoodItemFound = (LinearLayout) findViewById(R.id.no_food_item_found);
        horizontalMenuScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_menu_scroll_view);
        horizontalMenuScrollView.setAnimation(leftAnimation);

        foodItemList = (RecyclerView) findViewById(R.id.menu_item_recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        foodItemList.setLayoutManager(linearLayoutManager);

        bestseller = (LinearLayout) findViewById(R.id.bestseller);
        fastFood = (LinearLayout) findViewById(R.id.fast_food);
        veg = (LinearLayout) findViewById(R.id.veg);
        nonVeg = (LinearLayout) findViewById(R.id.non_veg);
        others = (LinearLayout) findViewById(R.id.others);

        bestsellerImage = (ImageView) findViewById(R.id.bestseller_image);
        fastFoodImage = (ImageView) findViewById(R.id.fast_food_image);
        vegImage = (ImageView) findViewById(R.id.veg_image);
        nonVegImage = (ImageView) findViewById(R.id.non_veg_image);
        othersImage = (ImageView) findViewById(R.id.others_image);

        bestsellerText = (TextView) findViewById(R.id.bestseller_text);
        fastFoodText = (TextView) findViewById(R.id.fast_food_text);
        vegText = (TextView) findViewById(R.id.veg_text);
        nonVegText = (TextView) findViewById(R.id.non_veg_text);
        othersText = (TextView) findViewById(R.id.others_text);

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    showFoodItems();
                    noFoodItemFound.setVisibility(View.GONE);
                }
                else
                {
                    foodItemList.setVisibility(View.GONE);
                    noFoodItemFound.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    cartItems = snapshot.getChildrenCount();
                }
                else
                {
                    cartItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bestsellerImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                fastFoodImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                bestsellerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                fastFoodText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Bestseller");
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            foodItemList.setVisibility(View.VISIBLE);
                            showFoodItems();
                            noFoodItemFound.setVisibility(View.GONE);
                        }
                        else
                        {
                            foodItemList.setVisibility(View.GONE);
                            noFoodItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        fastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bestsellerImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                bestsellerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Fast Food");
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            foodItemList.setVisibility(View.VISIBLE);
                            showFoodItems();
                            noFoodItemFound.setVisibility(View.GONE);
                        }
                        else
                        {
                            foodItemList.setVisibility(View.GONE);
                            noFoodItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bestsellerImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                bestsellerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Veg");
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            foodItemList.setVisibility(View.VISIBLE);
                            showFoodItems();
                            noFoodItemFound.setVisibility(View.GONE);
                        }
                        else
                        {
                            foodItemList.setVisibility(View.GONE);
                            noFoodItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        nonVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bestsellerImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                bestsellerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Non Veg");
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            foodItemList.setVisibility(View.VISIBLE);
                            showFoodItems();
                            noFoodItemFound.setVisibility(View.GONE);
                        }
                        else
                        {
                            foodItemList.setVisibility(View.GONE);
                            noFoodItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bestsellerImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                bestsellerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                fastFoodText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child(category).child(key).child("Menu Item").child("Others");
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            foodItemList.setVisibility(View.VISIBLE);
                            showFoodItems();
                            noFoodItemFound.setVisibility(View.GONE);
                        }
                        else
                        {
                            foodItemList.setVisibility(View.GONE);
                            noFoodItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void showFoodItems() {
        Query sorting = foodRef.orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter,MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.menu_item_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        RelativeLayout relativeLayout = (RelativeLayout) menuItemViewHolder.mView.findViewById(R.id.menu_item_relative_layout);
                        ImageView cart = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart);
                        ProgressBar cartProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_progress_bar);
                        CardView cartLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart_layout);
                        RelativeLayout cartRelativeLayout = (RelativeLayout) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart_relative_layout);

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                        menuItemViewHolder.setName(menuItemAdapter.getName());
                        menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                        menuItemViewHolder.setPrice(menuItemAdapter.getPrice());
                        progressBar.setVisibility(View.GONE);

                        cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cart.setVisibility(View.GONE);
                                cartProgress.setVisibility(View.VISIBLE);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                String currentDateandTime = sdf.format(new Date());

                                HashMap hashMap = new HashMap();
                                hashMap.put("count",String.valueOf(cartItems));
                                hashMap.put("itemName",menuItemAdapter.getName());
                                hashMap.put("itemPrice",menuItemAdapter.getPrice());
                                hashMap.put("itemCustomizedPrice",menuItemAdapter.getPrice());
                                hashMap.put("itemDescription",menuItemAdapter.getDescription());
                                hashMap.put("itemImage",menuItemAdapter.getImage());
                                hashMap.put("itemQuantity","1");

                                cartRef.child("CartItem"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(
                                        new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                                    cart.setVisibility(View.GONE);
                                                    cartProgress.setVisibility(View.GONE);
                                                    cartLayout.setVisibility(View.GONE);
                                                }
                                                else
                                                {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                                    cartProgress.setVisibility(View.GONE);
                                                    cart.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                );
                            }
                        });
                    }
                };
        foodItemList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name)
        {
            TextView menuName = (TextView) mView.findViewById(R.id.menu_item_name);
            menuName.setText(name);
        }

        public void setDescription(String description)
        {
            TextView menuName = (TextView) mView.findViewById(R.id.menu_item_description);
            menuName.setText(description);
        }

        public void setImage(Context ctx, String image)
        {
            ImageView menuImage = (ImageView) mView.findViewById(R.id.menu_item_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.ic_default_food).into(menuImage);
        }

        public void setPrice(String price)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.menu_item_price);
            menuPrice.setText(price);
        }
    }
}