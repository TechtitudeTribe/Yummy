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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView shopDetailsList,foodItemList;
    private DatabaseReference reference,shopRef,foodRef;
    private EditText searchText;
    private TextView searchButton;
    private TextView searchShop, searchFood;
    private ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        reference = FirebaseDatabase.getInstance().getReference().child("Shops");

        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (TextView) findViewById(R.id.search_button);
        progressBar1 = (ProgressBar) findViewById(R.id.search_progress_bar);

        shopDetailsList = (RecyclerView) findViewById(R.id.shop_details_list_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        shopDetailsList.setLayoutManager(linearLayoutManager);

        //searchShop = (TextView) findViewById(R.id.search_shop);
        //searchFood = (TextView) findViewById(R.id.search_food);

        /*searchShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShop.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                searchShop.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_yellow_dark));
                searchFood.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                searchFood.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                foodItemList.setVisibility(View.GONE);
                shopDetailsList.setVisibility(View.VISIBLE);
            }
        });*/

        /*searchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShop.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                searchShop.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                searchFood.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                searchFood.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_yellow_dark));
                foodItemList.setVisibility(View.VISIBLE);
                shopDetailsList.setVisibility(View.GONE);
            }
        });*/

        foodItemList = (RecyclerView) findViewById(R.id.menu_item_list_search);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        foodItemList.setLayoutManager(linearLayoutManager1);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar1.setVisibility(View.VISIBLE);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            for (DataSnapshot ds1 : ds.getChildren())
                            {
                                String shopName = ds1.child("shopName").getValue().toString();
                                if (shopName.toUpperCase().contains(searchText.getText().toString().toUpperCase()))
                                {
                                    shopRef = ds.getRef();
                                    displayShopList();
                                    shopDetailsList.setVisibility(View.VISIBLE);
                                    foodItemList.setVisibility(View.GONE);
                                }
                                else
                                {
                                    for (DataSnapshot ds2 : ds1.child("Menu").getChildren())
                                    {
                                        for (DataSnapshot ds3 : ds2.getChildren())
                                        {
                                            String itemName = ds3.child("name").getValue().toString();

                                            if (itemName.toUpperCase().contains(searchText.getText().toString().toUpperCase()))
                                            {
                                                foodRef = ds2.getRef();
                                                showFoodItems();
                                                shopDetailsList.setVisibility(View.GONE);
                                                foodItemList.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void displayShopList() {
        Query sort = shopRef.orderByChild("shopName");
        FirebaseRecyclerAdapter<ShopDetailsAdapter,ShopDetailsViewHolder> frc = new FirebaseRecyclerAdapter<ShopDetailsAdapter, ShopDetailsViewHolder>(
                        ShopDetailsAdapter.class,
                        R.layout.shop_details_layout,
                        ShopDetailsViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(ShopDetailsViewHolder shopDetailsViewHolder, ShopDetailsAdapter shopDetailsAdapter, int i) {

                        ImageView status = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_status);
                        ProgressBar progressBar = (ProgressBar) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_progress_bar);
                        ImageView phone = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_phone);
                        ImageView message = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_mail);
                        //View view = (View) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_view);
                        RelativeLayout relativeLayout = (RelativeLayout) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_relative_layout);

                        shopDetailsViewHolder.setShopName(shopDetailsAdapter.getShopName());
                        shopDetailsViewHolder.setShopSchedule(shopDetailsAdapter.getShopSchedule());
                        shopDetailsViewHolder.setShopFrontImage(shopDetailsAdapter.getShopFrontImage(),getApplicationContext());
                        //shopProgressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        progressBar1.setVisibility(View.GONE);
                        String whatsappNumber = "+91 "+shopDetailsAdapter.getShopWhatsappNumber();
                        //contactNumber = shopDetailsAdapter.getShopContactNumber();

                        String key = getRef(i).getKey();
                        //menuRef = FirebaseDatabase.getInstance().getReference().child("Shops").child("Chandpur").child(key).child("Menu Item").child("VegPizza");
                        TextView name = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_name);
                        TextView time = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_schedule);

                        /*switch (i%4)
                        {
                            case 0 : name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                time.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                phone.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                message.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                status.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                //view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                break;
                            case 1 : name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                time.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                phone.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                message.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                status.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                //view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                break;
                            case 2 : name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                time.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                phone.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                message.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                status.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                //view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                break;
                            case 3 : name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                time.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                phone.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                message.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                status.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                //view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                break;
                        }*/
                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        /*if (shopDetailsAdapter.getShopStatus().equals("open"))
                        {
                            status.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.open));
                        }
                        else
                        {
                            status.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.close));
                        }*/

                        /*shopDetailsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                shopRef.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {
                                            String status = snapshot.child("shopStatus").getValue().toString();
                                            if (status.equals("open"))
                                            {
                                                Intent intent = new Intent(getActivity(),MenuItemActivity.class);
                                                intent.putExtra("key",key);
                                                intent.putExtra("category",category);
                                                intent.putExtra("sellerId",shopDetailsAdapter.getUserId());
                                                intent.putExtra("shopId",shopDetailsAdapter.getShopId());
                                                startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        });

                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                makePhoneCall(contactNumber);
                            }
                        });

                        message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent messageIntent = openWhatsapp(getActivity(),whatsappNumber);
                                startActivity(messageIntent);
                            }
                        });*/

                    }
                };
        shopDetailsList.setAdapter(frc);
    }

    public static class ShopDetailsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ShopDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setShopName(String shopName)
        {
            TextView name = (TextView) mView.findViewById(R.id.shop_details_name);
            name.setText(shopName);
        }

        public void setShopSchedule(String shopSchedule)
        {
            TextView schedule = (TextView) mView.findViewById(R.id.shop_details_schedule);
            schedule.setText(shopSchedule);
        }


        public void setShopFrontImage(String shopFrontImage,Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.shop_details_shop_image);
            Picasso.with(context).load(shopFrontImage).placeholder(R.drawable.ic_baseline_shop_default).into(image);
        }
    }

    private void showFoodItems() {
        Query sorting = foodRef.orderByChild("count");
          FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
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
                cartLayout.setVisibility(View.GONE);

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
                menuItemViewHolder.setImage(getApplicationContext(), menuItemAdapter.getImage());
                menuItemViewHolder.setNewPrice(menuItemAdapter.getNewPrice());
                menuItemViewHolder.setOldPrice(menuItemAdapter.getOldPrice());
                progressBar1.setVisibility(View.GONE);

                cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cart.setVisibility(View.GONE);
                        cartProgress.setVisibility(View.VISIBLE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());

                        HashMap hashMap = new HashMap();
                        //hashMap.put("count",String.valueOf(cartItems));
                        hashMap.put("itemName", menuItemAdapter.getName());
                        hashMap.put("itemPrice", menuItemAdapter.getNewPrice());
                        hashMap.put("itemCustomizedPrice", menuItemAdapter.getNewPrice());
                        hashMap.put("itemDescription", menuItemAdapter.getDescription());
                        hashMap.put("itemImage", menuItemAdapter.getImage());
                        hashMap.put("itemQuantity", "1");
                        //hashMap.put("sellerId",sellerId);
                        //hashMap.put("shopId",shopId);

                                /*cartRef.child("CartItem"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(
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
                                );*/
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