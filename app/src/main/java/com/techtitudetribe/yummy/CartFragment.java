package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cart_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");

        progressBar = (ProgressBar) v.findViewById(R.id.my_cart_progress_bar);
        recyclerView = (RecyclerView) v.findViewById(R.id.my_cart_list_view);
        noCartLayout = (RelativeLayout) v.findViewById(R.id.no_cart_layout);

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
                        cartItemViewHolder.setItemImage(cartItemAdapter.getItemImage(),getActivity());
                        cartItemViewHolder.setItemName(cartItemAdapter.getItemName());
                        cartItemViewHolder.setItemQuantity(cartItemAdapter.getItemQuantity());
                        cartItemViewHolder.setItemCustomizedPrice(cartItemAdapter.getItemCustomizedPrice());
                        progressBar.setVisibility(View.GONE);

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
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                price.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                /*priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));*/
                                delete.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                price.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                /*priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));*/
                                delete.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                price.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                //priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));*/
                                delete.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                name.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                price.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                //priceTag.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));*/
                                delete.setColorFilter(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                                break;
                        }

                        cartItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),CartItemDescriptionActivity.class);
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                        });
                        deleteLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cartRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getActivity(), "Item removed successfully...", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            String mssg = task.getException().toString();
                                            Toast.makeText(getActivity(), "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
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
}