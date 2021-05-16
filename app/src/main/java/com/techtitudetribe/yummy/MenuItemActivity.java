
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
    private long cartItems=0,shopItem=0;
    private FirebaseAuth mAuth;
    private String currentUser,sellerId,shopId,shopName,shopContact;
    private LinearLayout quickBites, sweets, drinks, pizza, veg, nonVeg, extra, dairy, cakes, desserts, combo, others;
    private ImageView quickBitesImage, sweetsImage, drinksImage, pizzaImage, vegImage, nonVegImage, extraImage, dairyImage, cakesImage, dessertsImage, comboImage, othersImage, newItemImage;
    private TextView quickBitesText, sweetsText, drinksText, pizzaText, vegText, nonVegText, extraText, dairyText, cakesText, dessertsText, comboText, othersText,addMenuItemButton;
    private TextView shopItemNamesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        key = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        sellerId = getIntent().getStringExtra("sellerId");
        shopId = getIntent().getStringExtra("shopId");
        shopName = getIntent().getStringExtra("shopName");
        shopContact = getIntent().getStringExtra("shopContact");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");
        foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Quick Bites");

        leftAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
        bottomUpAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_up_animation);
        shopItemNamesText = (TextView) findViewById(R.id.shop_Item_Names_Text);

        progressBar = (ProgressBar) findViewById(R.id.menu_item_progress_bar);
        noFoodItemFound = (LinearLayout) findViewById(R.id.no_food_item_found);
        horizontalMenuScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_menu_scroll_view);
        horizontalMenuScrollView.setAnimation(leftAnimation);

        foodItemList = (RecyclerView) findViewById(R.id.menu_item_recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        foodItemList.setLayoutManager(linearLayoutManager);

        quickBites = (LinearLayout) findViewById(R.id.quick_bites);
        sweets = (LinearLayout) findViewById(R.id.sweets);
        drinks = (LinearLayout) findViewById(R.id.drinks);
        pizza = (LinearLayout) findViewById(R.id.pizza);
        veg = (LinearLayout) findViewById(R.id.veg);
        nonVeg = (LinearLayout) findViewById(R.id.non_veg);
        extra = (LinearLayout) findViewById(R.id.extra);
        dairy = (LinearLayout) findViewById(R.id.dairy);
        cakes = (LinearLayout) findViewById(R.id.cakes);
        desserts = (LinearLayout) findViewById(R.id.desserts);
        combo = (LinearLayout) findViewById(R.id.combo);
        others = (LinearLayout) findViewById(R.id.others);

        quickBitesImage = (ImageView) findViewById(R.id.quick_bites_image);
        sweetsImage = (ImageView) findViewById(R.id.sweets_image);
        drinksImage = (ImageView) findViewById(R.id.drinks_image);
        pizzaImage = (ImageView) findViewById(R.id.pizza_image);
        vegImage = (ImageView) findViewById(R.id.veg_image);
        nonVegImage = (ImageView) findViewById(R.id.non_veg_image);
        extraImage = (ImageView) findViewById(R.id.extra_image);
        dairyImage = (ImageView) findViewById(R.id.dairy_image);
        cakesImage = (ImageView) findViewById(R.id.cakes_image);
        dessertsImage = (ImageView) findViewById(R.id.desserts_image);
        comboImage = (ImageView) findViewById(R.id.combo_image);
        othersImage = (ImageView) findViewById(R.id.others_image);

        quickBitesText = (TextView) findViewById(R.id.quick_bites_text);
        sweetsText = (TextView) findViewById(R.id.sweets_text);
        drinksText = (TextView) findViewById(R.id.drinks_text);
        pizzaText = (TextView) findViewById(R.id.pizza_text);
        vegText = (TextView) findViewById(R.id.veg_text);
        nonVegText = (TextView) findViewById(R.id.non_veg_text);
        extraText = (TextView) findViewById(R.id.extra_text);
        dairyText = (TextView) findViewById(R.id.dairy_text);
        cakesText = (TextView) findViewById(R.id.cakes_text);
        dessertsText = (TextView) findViewById(R.id.desserts_text);
        comboText = (TextView) findViewById(R.id.combo_text);
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

        cartRef.child(shopName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("itemNames"))
                {
                    String shopItemNames = snapshot.child("itemNames").getValue().toString()+",";
                    shopItemNamesText.setText(shopItemNames);
                }
                else
                {
                    shopItemNamesText.setText("");
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
                    shopItem = snapshot.getChildrenCount();
                }
                else
                {
                    shopItem = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartRef.child(shopName).child("Cart Items").addValueEventListener(new ValueEventListener() {
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

        quickBites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Quick Bites");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Quick Bites");
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
        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Sweets");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Sweets");
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
        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Drinks");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Drinks");
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
        pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Pizza");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Pizza");
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
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Veg");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Veg");
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
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Non Veg");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Non Veg");
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
        extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Extra");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Extra");
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
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Dairy");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Dairy");
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
        cakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Cakes");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Cakes");
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
        desserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Desserts");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Desserts");
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
        combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Combo");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Combo");
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
                quickBitesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                quickBitesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                sweetsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                drinksText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                extraText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dairyText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                cakesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                dessertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                comboText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                othersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                foodRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(key).child("Menu").child("Others");
                //globalShopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(category).child(globalShopId).child("Menu").child("Others");
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

                        View relativeLayout = (View) menuItemViewHolder.mView.findViewById(R.id.menu_item_view);
                        ImageView cart = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart);
                        ProgressBar cartProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_progress_bar);
                        CardView cartLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart_layout);
                        RelativeLayout cartRelativeLayout = (RelativeLayout) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart_relative_layout);
                        View view = (View) menuItemViewHolder.mView.findViewById(R.id.menu_item_layout_view1);
                        TextView name = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_name);
                        TextView price = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_new_price);

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                price.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                cartRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                        menuItemViewHolder.setName(menuItemAdapter.getName());
                        menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                        menuItemViewHolder.setNewPrice(menuItemAdapter.getNewPrice());
                        menuItemViewHolder.setOldPrice(menuItemAdapter.getOldPrice());
                        progressBar.setVisibility(View.GONE);

                        if(menuItemAdapter.getStatus().equals("Available"))
                        {
                            cartLayout.setVisibility(View.VISIBLE);
                            cart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cart.setVisibility(View.GONE);
                                    cartProgress.setVisibility(View.VISIBLE);
                                    if (shopItemNamesText.getText().toString().contains(menuItemAdapter.getName()))
                                    {
                                        Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                        cart.setVisibility(View.GONE);
                                        cartProgress.setVisibility(View.GONE);
                                        cartLayout.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        HashMap hashMap1 = new HashMap();
                                        hashMap1.put("count",shopItem+1);
                                        hashMap1.put("itemNames",shopItemNamesText.getText().toString()+menuItemAdapter.getName());
                                        hashMap1.put("shopName",shopName);
                                        hashMap1.put("sellerId",sellerId);
                                        hashMap1.put("shopId",shopId);
                                        hashMap1.put("shopContact",shopContact);

                                        cartRef.child(shopName).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful())
                                                {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                                    String currentDateandTime = sdf.format(new Date());

                                                    HashMap hashMap = new HashMap();
                                                    hashMap.put("count",cartItems+1);
                                                    hashMap.put("itemName",menuItemAdapter.getName());
                                                    hashMap.put("itemPrice",menuItemAdapter.getNewPrice());
                                                    hashMap.put("itemCustomizedPrice",menuItemAdapter.getNewPrice());
                                                    hashMap.put("itemDescription",menuItemAdapter.getDescription());
                                                    hashMap.put("itemImage",menuItemAdapter.getImage());
                                                    hashMap.put("itemQuantity","1");

                                                    cartRef.child(shopName).child("Cart Items").child("CartItem"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(
                                                            new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task1) {
                                                                    if (task1.isSuccessful())
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                                                        cart.setVisibility(View.GONE);
                                                                        cartProgress.setVisibility(View.GONE);
                                                                        cartLayout.setVisibility(View.GONE);
                                                                    }
                                                                    else
                                                                    {
                                                                        String message = task1.getException().getMessage();
                                                                        Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                                                        cartProgress.setVisibility(View.GONE);
                                                                        cart.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                                else
                                                {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                                    cartProgress.setVisibility(View.GONE);
                                                    cart.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else
                        {
                            cartLayout.setVisibility(View.GONE);
                        }
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

        public void setNewPrice(String newPrice)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.menu_item_new_price);
            menuPrice.setText("Rs. "+newPrice);
        }

        public void setOldPrice(String oldPrice)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.menu_item_old_price);
            menuPrice.setText("Rs. "+oldPrice);
        }
    }
}