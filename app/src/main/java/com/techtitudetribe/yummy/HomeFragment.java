package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private RecyclerView shopDetailsList;
    private DatabaseReference shopRef;
    private ProgressBar shopProgressBar;
    private static final int REQUEST_CALL = 1;
    private String contactNumber;
    private ImageView restaurantImage, foodStallImage, bakeryImage, groceryImage;
    private TextView restaurantText, foodStallText, bakeryText, groceryText;
    private LinearLayout restaurant, foodStall, bakery, grocery;
    private String category = "Restaurant";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        viewPager2 = (ViewPager2) v.findViewById(R.id.home_viewpager);
        shopRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child("Restaurant");
        shopProgressBar = (ProgressBar) v.findViewById(R.id.shop_details_list_progress_bar);

        restaurant = (LinearLayout) v.findViewById(R.id.restaurant);
        foodStall = (LinearLayout) v.findViewById(R.id.food_stall);
        bakery = (LinearLayout) v.findViewById(R.id.bakery);
        grocery = (LinearLayout) v.findViewById(R.id.grocery);

        restaurantImage = (ImageView) v.findViewById(R.id.restaurant_image);
        foodStallImage = (ImageView) v.findViewById(R.id.food_stall_image);
        bakeryImage = (ImageView) v.findViewById(R.id.bakery_image);
        groceryImage = (ImageView) v.findViewById(R.id.grocery_image);

        restaurantText = (TextView) v.findViewById(R.id.restaurant_text);
        foodStallText = (TextView) v.findViewById(R.id.food_stall_text);
        bakeryText = (TextView) v.findViewById(R.id.bakery_text);
        groceryText = (TextView) v.findViewById(R.id.grocery_text);

        List<HomeSliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new HomeSliderItem(R.drawable.slider1));
        sliderItems.add(new HomeSliderItem(R.drawable.slider2));
        sliderItems.add(new HomeSliderItem(R.drawable.slider3));
        sliderItems.add(new HomeSliderItem(R.drawable.slider4));

        viewPager2.setAdapter(new HomeSliderItemAdapter(sliderItems,viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
                page.setScaleX(0.80f + r * 0.40f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable,3000);
                /*pageIndicatorView.setSelection(position);
                switch (position)
                {
                    case 0 : pageIndicatorView.setSelectedColor(ContextCompat.getColor(TestActivity.this,R.color.creative_red));
                        break;
                    case 1 : pageIndicatorView.setSelectedColor(ContextCompat.getColor(TestActivity.this,R.color.creative_green));
                        break;
                    case 2 : pageIndicatorView.setSelectedColor(ContextCompat.getColor(TestActivity.this,R.color.creative_yellow_dark));
                        break;
                    case 3 : pageIndicatorView.setSelectedColor(ContextCompat.getColor(TestActivity.this,R.color.creative_sky_blue));
                        break;
                    case 4 : pageIndicatorView.setSelectedColor(ContextCompat.getColor(TestActivity.this,R.color.creative_orange));
                        break;
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                foodStallImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                groceryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                restaurantText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                foodStallText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                groceryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                shopProgressBar.setVisibility(View.VISIBLE);
                shopRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child("Restaurant");
                category = "Restaurant";
                displayShopList();
            }
        });
        foodStall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                bakeryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                groceryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                restaurantText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                bakeryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                groceryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                shopProgressBar.setVisibility(View.VISIBLE);
                shopRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child("Food Stall");
                displayShopList();
                category = "Food Stall";
            }
        });
        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                groceryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                restaurantText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                groceryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                shopProgressBar.setVisibility(View.VISIBLE);
                shopRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child("Bakery");
                displayShopList();
                category = "Bakery";
            }
        });
        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
                groceryImage.setColorFilter(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                restaurantText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                foodStallText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                bakeryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                groceryText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                shopProgressBar.setVisibility(View.VISIBLE);
                shopRef = FirebaseDatabase.getInstance().getReference().child("Shopkeepers").child("Grocery");
                displayShopList();
                category = "Grocery";
            }
        });

        shopDetailsList = (RecyclerView) v.findViewById(R.id.shop_details_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        shopDetailsList.setLayoutManager(linearLayoutManager);

        displayShopList();
        return v;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable,3000);
    }

    private void displayShopList() {
        Query sort = shopRef.orderByChild("count");
        FirebaseRecyclerAdapter<ShopDetailsAdapter,ShopDetailsViewHolder> frc =
                new FirebaseRecyclerAdapter<ShopDetailsAdapter, ShopDetailsViewHolder>(
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
                        shopDetailsViewHolder.setShopImageUrl(shopDetailsAdapter.getShopImageUrl(),getActivity());
                        shopProgressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        String whatsappNumber = "+91 "+shopDetailsAdapter.getShopWhatsappNumber();
                        contactNumber = shopDetailsAdapter.getShopContactNumber();

                        String key = getRef(i).getKey();
                        //menuRef = FirebaseDatabase.getInstance().getReference().child("Shops").child("Chandpur").child(key).child("Menu Item").child("VegPizza");
                        TextView name = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_name);
                        TextView time = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_schedule);

                        switch (i%4)
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
                        }

                        if (shopDetailsAdapter.getShopStatus().equals("open"))
                        {
                            status.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.open));
                        }
                        else
                        {
                            status.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.close));
                        }

                        shopDetailsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
                        });

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


        public void setShopImageUrl(String shopImageUrl, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.shop_details_shop_image);
            Picasso.with(context).load(shopImageUrl).placeholder(R.drawable.ic_baseline_shop_default).into(image);
        }
    }

    private void makePhoneCall(String contactNumber) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + contactNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(contactNumber);
            } else {
                Toast.makeText(getActivity(),"Permission Denied...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Intent openWhatsapp(Context context, String whatsappNumber) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.whatsapp", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + whatsappNumber));
        } catch (Exception ex) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + whatsappNumber));
        }
    }
}