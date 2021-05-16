package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OrderConfirmationActivity extends AppCompatActivity  {

    private TextView actualPrice,totalPrice;
    private TextView itemNames;
    private TextView proceedToPay;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef,userRef, orderRef,adminRef,preOrderRef;
    private String currentUser;
    private long orderItems=0, adminItems=0,preOrderItems=0;
    private TextView orderConfirmationDate,orderConfirmationDescription;
    private RadioGroup paymentMethod;
    private String paymentMethodString="Pay via UPI";
    private TextView placeOrder,sellerId,shopId,shopName;
    private String TAG ="main";
    private final int UPI_PAYMENT = 0;
    private ImageView itemImage;
    private String address,key;
    private TextView userName, userContact;
    private ImageView close;
    private RelativeLayout updateProfileLayout;
    private ProgressBar placeProgressBar;
    private TextView deliveryCharge,sendToVerification;
    private LinearLayout paymentMethodLayout;
    private String shopContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        sellerId = (TextView) findViewById(R.id.order_seller_id);
        shopId = (TextView) findViewById(R.id.order_shop_id);
        shopName = (TextView) findViewById(R.id.order_shop_name);
        userName = (TextView) findViewById(R.id.order_user_name);
        userContact = (TextView) findViewById(R.id.order_user_number);
        close = (ImageView) findViewById(R.id.update_profile_order_card_close);
        updateProfileLayout = (RelativeLayout) findViewById(R.id.update_profile_order_relative);
        totalPrice = (TextView) findViewById(R.id.order_total_price);
        placeProgressBar = (ProgressBar) findViewById(R.id.place_order_progress_bar);
        sendToVerification = (TextView) findViewById(R.id.proceed_to_verification);
        paymentMethodLayout = (LinearLayout) findViewById(R.id.payment_method_layout);

        sellerId.setText(getIntent().getStringExtra("sellerId"));
        shopId.setText(getIntent().getStringExtra("shopId"));
        shopName.setText(getIntent().getStringExtra("shopName"));
        shopContact = getIntent().getStringExtra("shopContact");
        key = getIntent().getStringExtra("key");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Orders");
        preOrderRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("PreOrders");
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Orders");

        actualPrice = (TextView) findViewById(R.id.order_confirmation_price);
        itemNames = (TextView) findViewById(R.id.order_confirmation_item_names);
        proceedToPay = (TextView) findViewById(R.id.order_confirmation_proceed_to_pay);
        orderConfirmationDate = (TextView) findViewById(R.id.order_confirmation_date);
        orderConfirmationDescription = (TextView) findViewById(R.id.order_confirmation_item_description);
        placeOrder = (TextView) findViewById(R.id.order_confirmation_place_order);
        itemImage = (ImageView) findViewById(R.id.order_confirmation_item_image);
        deliveryCharge = (TextView) findViewById(R.id.delivery_charge);

        paymentMethod = (RadioGroup) findViewById(R.id.payment_method);

        paymentMethod.clearCheck();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                String contact = snapshot.child("Contact Number").getValue().toString();

                userName.setText(name);
                userContact.setText(contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        actualPrice.setText(getIntent().getExtras().getString("totalPrice"));
        itemNames.setText(getIntent().getExtras().getString("itemName"));
        orderConfirmationDescription.setText(getIntent().getExtras().getString("itemDescription"));
        address = getIntent().getStringExtra("address");

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = simpleDateFormat.format(calendar.getTime());

        cartRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("deliveryCharge"))
                {
                    String dc = snapshot.child("deliveryCharge").getValue().toString();
                    deliveryCharge.setText(dc);
                    totalPrice.setText(String.valueOf(Integer.parseInt(actualPrice.getText().toString())+Integer.parseInt(deliveryCharge.getText().toString())));
                }
                else
                {
                    deliveryCharge.setText("00");
                    totalPrice.setText(String.valueOf(Integer.parseInt(actualPrice.getText().toString())+Integer.parseInt(deliveryCharge.getText().toString())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        paymentMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                paymentMethodString = radioButton.getText().toString();
                if (paymentMethodString.equals("Pay via UPI"))
                {
                    proceedToPay.setVisibility(View.VISIBLE);
                    placeOrder.setVisibility(View.GONE);
                }
                else
                {
                    proceedToPay.setVisibility(View.GONE);
                    placeOrder.setVisibility(View.VISIBLE);
                }
            }
        });

        orderConfirmationDate.setText("Order Will Be Placed On\n"+currentDate);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    orderItems = snapshot.getChildrenCount();
                }
                else
                {
                    orderItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileLayout.setVisibility(View.GONE);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());

        adminRef.child(currentDateAdmin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    adminItems = snapshot.getChildrenCount();
                }
                else
                {
                    adminItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        preOrderRef.child(currentDateAdmin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    preOrderItems = snapshot.getChildrenCount();
                }
                else
                {
                    preOrderItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("itemStatus"))
                {
                    String is = snapshot.child("itemStatus").getValue().toString();
                    if (is.equals("Pending"))
                    {
                        sendToVerification.setVisibility(View.VISIBLE);
                        sendToVerification.setText("Under Verification...");
                        proceedToPay.setVisibility(View.GONE);
                        paymentMethodLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        sendToVerification.setVisibility(View.GONE);
                        proceedToPay.setVisibility(View.VISIBLE);
                        paymentMethodLayout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    sendToVerification.setVisibility(View.VISIBLE);
                    sendToVerification.setText("Send To Verification");
                    paymentMethodLayout.setVisibility(View.GONE);
                    sendToVerification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendDetailsForVerifications(currentDateAdmin,currentDate,currentTime);
                        }
                    });
                    proceedToPay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkValidations(currentDate,currentTime);
            }
        });

        proceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(paymentMethodString))
                {
                    Toast.makeText(getApplicationContext(), "Select any Payment Method...", Toast.LENGTH_SHORT).show();
                    updateProfileLayout.setVisibility(View.GONE);
                }
                else if (userName.getText().toString().equals("Username"))
                {
                    updateProfileLayout.setVisibility(View.VISIBLE);
                }
                else if (userContact.getText().toString().equals("+910000000000"))
                {
                    updateProfileLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    //rahul.kumar7499@axisbank
                    placeProgressBar.setVisibility(View.VISIBLE);
                    proceedToPay.setVisibility(View.GONE);
                    updateProfileLayout.setVisibility(View.GONE);
                    payUsingUpi("Yummy", "7071632728@axl",
                            "Food Items", totalPrice.getText().toString());

                }
            }
        });
    }

    private void sendDetailsForVerifications(String currentDateAdmin, String currentDate, String currentTime) {

        String itemName = itemNames.getText().toString();
        String itemDescription = orderConfirmationDescription.getText().toString();
        String shopNameString = shopName.getText().toString();
        String itemPlacedDate = currentDate +" at "+currentTime;
        String actualPriceString = actualPrice.getText().toString();
        String deliveryChargeString  =  deliveryCharge.getText().toString();

        if (userName.getText().toString().equals("Username"))
        {
            updateProfileLayout.setVisibility(View.VISIBLE);
        }
        else if (userContact.getText().toString().equals("+910000000000"))
        {
            updateProfileLayout.setVisibility(View.VISIBLE);
        }
        else if (TextUtils.isEmpty(paymentMethodString))
        {
            Toast.makeText(this, "Select any Payment Method...", Toast.LENGTH_SHORT).show();
            updateProfileLayout.setVisibility(View.GONE);
        }
        else {
            sendToVerification.setVisibility(View.GONE);
            placeProgressBar.setVisibility(View.VISIBLE);
            HashMap hashMap1 = new HashMap();
            hashMap1.put("itemStatus", "Pending");
            cartRef.child(key).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task2) {
                    if (task2.isSuccessful()) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("count", preOrderItems + 1);
                        hashMap.put("price",actualPriceString);
                        hashMap.put("deliveryCharge",deliveryChargeString);
                        hashMap.put("userId", currentUser);
                        hashMap.put("key", key);
                        hashMap.put("address", address);
                        hashMap.put("itemName",itemName);
                        hashMap.put("itemDescription",itemDescription);
                        hashMap.put("shopName", shopNameString);
                        hashMap.put("itemStatus", "Pending");
                        hashMap.put("itemPlacedDate",itemPlacedDate);
                        hashMap.put("contactNumber",userContact.getText().toString());
                        hashMap.put("name",userName.getText().toString());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());

                        preOrderRef.child(currentDateAdmin).child(currentUser+currentDateandTime).updateChildren(hashMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task1) {

                                        if (task1.isSuccessful())
                                        {
                                            placeProgressBar.setVisibility(View.GONE);
                                            sendToVerification.setVisibility(View.VISIBLE);
                                            Toast.makeText(OrderConfirmationActivity.this, "Your order is under verification", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                            {
                                                placeProgressBar.setVisibility(View.GONE);
                                                sendToVerification.setVisibility(View.VISIBLE);
                                            String message = task1.getException().getMessage();
                                            Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                        {
                            placeProgressBar.setVisibility(View.GONE);
                            sendToVerification.setVisibility(View.VISIBLE);
                        String message = task2.getException().getMessage();
                        Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void payUsingUpi(String name, String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(OrderConfirmationActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkValidations(String currentDate, String currentTime) {
        if (userName.getText().toString().equals("Username"))
        {
            updateProfileLayout.setVisibility(View.VISIBLE);
        }
        else if (userContact.getText().toString().equals("+910000000000"))
            {
                updateProfileLayout.setVisibility(View.VISIBLE);
            }
            else if (TextUtils.isEmpty(paymentMethodString))
        {
            Toast.makeText(this, "Select any Payment Method...", Toast.LENGTH_SHORT).show();
            updateProfileLayout.setVisibility(View.GONE);
        }
        else
        {
            placeProgressBar.setVisibility(View.VISIBLE);
            updateProfileLayout.setVisibility(View.GONE);
            placeOrder.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            HashMap hashMap = new HashMap();
            hashMap.put("itemNames",itemNames.getText().toString());
            hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
            hashMap.put("count",orderItems+1);
            hashMap.put("itemTotalAmount",totalPrice.getText().toString());
            hashMap.put("itemPlacedDate",currentDate +" at "+currentTime);
            hashMap.put("itemStatus","Ordered");
            hashMap.put("userId",currentUser);
            hashMap.put("payment method",paymentMethodString);
            hashMap.put("sellerId",sellerId.getText().toString());
            hashMap.put("shopId",shopId.getText().toString());
            hashMap.put("shopName",shopName.getText().toString());
            hashMap.put("customerName",userName.getText().toString());
            hashMap.put("customerNumber",userContact.getText().toString());
            hashMap.put("titleName","MyOrder"+currentDateandTime);

            orderRef.child("MyOrder"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        sendUserDetailsToAdmin(currentDate,currentDateandTime,currentTime);
                    }
                    else
                    {
                        String message = task.getException().getMessage().toString();
                        Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(OrderConfirmationActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(OrderConfirmationActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = simpleDateFormat.format(calendar.getTime());

                HashMap hashMap = new HashMap();
                hashMap.put("itemNames",itemNames.getText().toString());
                hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
                hashMap.put("count",orderItems+1);
                hashMap.put("itemTotalAmount",totalPrice.getText().toString());
                hashMap.put("itemPlacedDate",currentDate+" at "+currentTime);
                hashMap.put("itemStatus","Ordered");
                hashMap.put("userId",currentUser);
                hashMap.put("payment method",paymentMethodString);
                hashMap.put("sellerId",sellerId.getText().toString());
                hashMap.put("shopId",shopId.getText().toString());
                hashMap.put("shopName",shopName.getText().toString());
                hashMap.put("customerName",userName.getText().toString());
                hashMap.put("customerNumber",userContact.getText().toString());
                hashMap.put("orderStatus","ConfirmByAdmin");
                hashMap.put("titleName","MyOrder"+currentDateandTime);

                orderRef.child("MyOrder"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            sendUserDetailsToAdminPay(currentDate,currentDateandTime, currentTime);
                            
                        }
                        else
                        {
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(OrderConfirmationActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(OrderConfirmationActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(OrderConfirmationActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendUserDetailsToAdmin(String currentDate, String currentDateandTime, String currentTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());


        HashMap hashMap = new HashMap();
        hashMap.put("itemNames",itemNames.getText().toString());
        hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
        hashMap.put("count",adminItems+1);
        hashMap.put("itemTotalAmount",totalPrice.getText().toString());
        hashMap.put("itemPlacedDate",currentDate+" at "+currentTime);
        hashMap.put("itemStatus","Ordered");
        hashMap.put("userId",currentUser);
        hashMap.put("payment method",paymentMethodString);
        hashMap.put("address",address);
        hashMap.put("deliveryCharge",deliveryCharge.getText().toString());
        hashMap.put("titleName","MyOrder"+currentDateandTime);
        hashMap.put("sellerId",sellerId.getText().toString());
        hashMap.put("shopId",shopId.getText().toString());
        hashMap.put("shopName",shopName.getText().toString());
        hashMap.put("customerName",userName.getText().toString());
        hashMap.put("customerNumber",userContact.getText().toString());

        adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task1) {
                if (task1.isSuccessful())
                {
                    cartRef.child(key).child("itemStatus").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful())
                            {
                                Toast.makeText(OrderConfirmationActivity.this, "Your Order is placed successfully...", Toast.LENGTH_SHORT).show();
                                proceedToPay.setVisibility(View.GONE);
                                placeOrder.setVisibility(View.GONE);
                                placeProgressBar.setVisibility(View.GONE);
                                sendNotification();
                            }
                            else
                            {
                                String message = task2.getException().getMessage().toString();
                                Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                placeProgressBar.setVisibility(View.GONE);
                                placeOrder.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else
                {
                    String message = task1.getException().getMessage().toString();
                    Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                    placeProgressBar.setVisibility(View.GONE);
                    placeOrder.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendUserDetailsToAdminPay(String currentDate, String currentDateandTime, String currentTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());


        HashMap hashMap = new HashMap();
        hashMap.put("itemNames",itemNames.getText().toString());
        hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
        hashMap.put("count",adminItems+1);
        hashMap.put("itemTotalAmount",totalPrice.getText().toString());
        hashMap.put("itemPlacedDate",currentDate+" at "+currentTime);
        hashMap.put("itemStatus","Ordered");
        hashMap.put("userId",currentUser);
        hashMap.put("payment method",paymentMethodString);
        hashMap.put("address",address);
        hashMap.put("deliveryCharge",deliveryCharge.getText().toString());
        hashMap.put("titleName","MyOrder"+currentDateandTime);
        hashMap.put("sellerId",sellerId.getText().toString());
        hashMap.put("shopId",shopId.getText().toString());
        hashMap.put("shopName",shopName.getText().toString());
        hashMap.put("customerName",userName.getText().toString());
        hashMap.put("customerNumber",userContact.getText().toString());

        adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task1) {
                if (task1.isSuccessful())
                {
                    cartRef.child(key).child("itemStatus").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful())
                            {
                                Toast.makeText(OrderConfirmationActivity.this, "Your Order is placed successfully...", Toast.LENGTH_SHORT).show();
                                proceedToPay.setVisibility(View.GONE);
                                placeOrder.setVisibility(View.GONE);
                                placeProgressBar.setVisibility(View.GONE);
                                sendNotification();
                            }
                            else
                            {
                                String message = task2.getException().getMessage().toString();
                                Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                placeProgressBar.setVisibility(View.GONE);
                                proceedToPay.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
                else
                {
                    String message = task1.getException().getMessage().toString();
                    Toast.makeText(OrderConfirmationActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                    placeProgressBar.setVisibility(View.GONE);
                    proceedToPay.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel =
                    new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentTitle("Notfication")
                .setSmallIcon(R.drawable.icon_notification)
                .setAutoCancel(true)
                .setContentText("Your order is placed...");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    public static boolean isConnectionAvailable(OrderConfirmationActivity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}