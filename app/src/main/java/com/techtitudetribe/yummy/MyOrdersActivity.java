package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyOrdersActivity extends AppCompatActivity {

    private DatabaseReference orderRef;
    private RecyclerView ordersListView;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private ProgressBar progressBar;
    private RelativeLayout noOrdersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Orders");

        progressBar = (ProgressBar) findViewById(R.id.my_orders_progress_bar);
        noOrdersLayout = (RelativeLayout) findViewById(R.id.no_orders_layout);

        ordersListView = (RecyclerView) findViewById(R.id.my_orders_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ordersListView.setLayoutManager(linearLayoutManager);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noOrdersLayout.setVisibility(View.GONE);
                    showMyOrders();
                }
                else
                {
                    noOrdersLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showMyOrders();
    }

    private void showMyOrders() {
        Query sort = orderRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyOrdersAdapter,MyOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MyOrdersAdapter, MyOrderViewHolder>(
                        MyOrdersAdapter.class,
                        R.layout.my_orders_item_layout,
                        MyOrderViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(MyOrderViewHolder myOrderViewHolder, MyOrdersAdapter myOrderAdapter, int i) {

                        RelativeLayout relativeLayout = (RelativeLayout) myOrderViewHolder.mView.findViewById(R.id.my_order_item_relative_layout);
                        TextView textView = (TextView) myOrderViewHolder.mView.findViewById(R.id.my_order_item_order_number);
                        String key = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        myOrderViewHolder.setItemNames(myOrderAdapter.getItemNames());
                        myOrderViewHolder.setItemDescription(myOrderAdapter.getItemDescription());
                        myOrderViewHolder.setItemTotalAmount(myOrderAdapter.getItemTotalAmount());
                        myOrderViewHolder.setItemPlacedDate(myOrderAdapter.getItemPlacedDate());
                        myOrderViewHolder.setCount(myOrderAdapter.getCount());
                        progressBar.setVisibility(View.GONE);

                        myOrderViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MyOrdersActivity.this,MyOrderDetailActivity.class);
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                        });
                    }
                };
        ordersListView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyOrderViewHolder extends RecyclerView.ViewHolder{

        View mView ;
        public MyOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setItemNames(String itemNames)
        {
            TextView name = (TextView) mView.findViewById(R.id.my_order_item_name);
            name.setText(itemNames);
        }

        public void setItemDescription(String itemDescription)
        {
            TextView number = (TextView) mView.findViewById(R.id.my_order_item_description);
            number.setText(itemDescription);
        }

        public void setItemPlacedDate(String itemPlacedDate)
        {
            TextView date = (TextView) mView.findViewById(R.id.my_order_item_placed_date);
            date.setText("Order Placed on "+itemPlacedDate);
        }

        public void setItemTotalAmount(String itemTotalAmount)
        {
            TextView amount = (TextView) mView.findViewById(R.id.my_order_item_total_amount);
            amount.setText("for Amount Rs."+itemTotalAmount);
        }

        public void setCount(String count)
        {
            TextView orderNumber = (TextView) mView.findViewById(R.id.my_order_item_order_number);
            orderNumber.setText(count);
        }
    }
}