package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef;
    private String currentUser;
    private RelativeLayout noCartLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cart_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");

        progressBar = (ProgressBar) v.findViewById(R.id.my_cart_progress_bar);
        recyclerView = (RecyclerView) v.findViewById(R.id.my_cart_list_view);
        noCartLayout = (RelativeLayout) v.findViewById(R.id.no_cart_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.my_cart_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this::displayCartItems);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.creative_green),
                ContextCompat.getColor(getActivity(),R.color.creative_red),
                ContextCompat.getColor(getActivity(),R.color.creative_violet),
                ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                displayCartItems();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        cartRef.addValueEventListener(new ValueEventListener() {
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

        return v;

    }

    private void displayCartItems() {
        Query sort = cartRef.orderByChild("count");
        FirebaseRecyclerAdapter<CartShopItemAdapter,CartShopItemViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<CartShopItemAdapter, CartShopItemViewHolder>(
                        CartShopItemAdapter.class,
                        R.layout.shop_cart_item_layout,
                        CartShopItemViewHolder.class,
                        sort
        ) {
            @Override
            protected void populateViewHolder(CartShopItemViewHolder cartShopItemViewHolder, CartShopItemAdapter cartShopItemAdapter, int i) {

                String key = getRef(i).getKey();
                LinearLayout linearLayout = (LinearLayout) cartShopItemViewHolder.mView.findViewById(R.id.shop_cart_item_background);

                switch (i%4)
                {
                    case 0 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                        break;
                    case 1 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        break;
                    case 2 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                        break;
                    case 3 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                        break;
                }

                cartShopItemViewHolder.setItemNames(cartShopItemAdapter.getItemNames());
                cartShopItemViewHolder.setShopName(cartShopItemAdapter.getShopName());
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                cartShopItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),ShopCartItemActivity.class);
                        intent.putExtra("key",key);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CartShopItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CartShopItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setItemNames(String itemNames)
        {
            TextView textView = (TextView) mView.findViewById(R.id.shop_cart_items_name);
            textView.setText(itemNames);
        }

        public void setShopName(String shopName)
        {
            TextView textView = (TextView) mView.findViewById(R.id.shop_cart_item_shop_name);
            textView.setText(shopName);
        }
    }

}