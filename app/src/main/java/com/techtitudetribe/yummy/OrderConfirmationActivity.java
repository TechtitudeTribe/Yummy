package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OrderConfirmationActivity extends AppCompatActivity {

    private TextView totalPrice;
    private TextView itemNames,itemQuantity,itemImageUrl;
    private TextView proceedToPay;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef, orderRef;
    private String currentUser;
    private long orderItems=0;
    private TextView orderConfirmationDate,orderConfirmationDescription;
    private RadioGroup paymentMethod, orderType;
    private String paymentMethodString="Pay via UPI", orderTypeString;
    private TextView placeOrder;
    private String TAG ="main";
    private final int UPI_PAYMENT = 0;
    private ImageView itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Cart");
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("My Orders");

        totalPrice = (TextView) findViewById(R.id.order_confirmation_price);
        itemQuantity = (TextView) findViewById(R.id.order_confirmation_item_quantity);
        itemNames = (TextView) findViewById(R.id.order_confirmation_item_names);
        proceedToPay = (TextView) findViewById(R.id.order_confirmation_proceed_to_pay);
        orderConfirmationDate = (TextView) findViewById(R.id.order_confirmation_date);
        orderConfirmationDescription = (TextView) findViewById(R.id.order_confirmation_item_description);
        placeOrder = (TextView) findViewById(R.id.order_confirmation_place_order);
        itemImage = (ImageView) findViewById(R.id.order_confirmation_item_image);
        itemImageUrl = (TextView) findViewById(R.id.order_confirmation_item_image_url);

        paymentMethod = (RadioGroup) findViewById(R.id.payment_method);
        orderType = (RadioGroup) findViewById(R.id.order_type);

        paymentMethod.clearCheck();
        orderType.clearCheck();

        totalPrice.setText(getIntent().getExtras().getString("totalPrice"));
        itemNames.setText(getIntent().getExtras().getString("itemName"));
        orderConfirmationDescription.setText(getIntent().getExtras().getString("itemDescription"));
        itemQuantity.setText(getIntent().getExtras().getString("itemQuantity"));
        itemImageUrl.setText(getIntent().getExtras().getString("itemImageUrl"));
        Picasso.with(getApplicationContext()).load(itemImageUrl.getText().toString()).placeholder(R.drawable.ic_default_food).into(itemImage);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

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

        orderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                orderTypeString = radioButton.getText().toString();
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

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations(currentDate);
            }
        });

        proceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(paymentMethodString))
                {
                    Toast.makeText(getApplicationContext(), "Select any Payment Method...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(orderTypeString))
                {
                    Toast.makeText(getApplicationContext(), "Select any Order Type...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //rahul.kumar7499@axisbank
                    payUsingUpi("BarbieCorn Pizza", "6397213673@ybl",
                            "Food Items", totalPrice.getText().toString());

                }
            }
        });
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

    private void checkValidations(String currentDate) {
        if (TextUtils.isEmpty(paymentMethodString))
        {
            Toast.makeText(this, "Select any Payment Method...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(orderTypeString))
        {
            Toast.makeText(this, "Select any Order Type...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            HashMap hashMap = new HashMap();
            hashMap.put("itemNames",itemNames.getText().toString());
            hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
            hashMap.put("count",String.valueOf(orderItems+1));
            hashMap.put("itemTotalAmount",totalPrice.getText().toString());
            hashMap.put("itemPlacedDate",currentDate);
            hashMap.put("itemImage",itemImageUrl.getText().toString());
            hashMap.put("itemStatus","Ordered");
            hashMap.put("userId",currentUser);
            hashMap.put("payment method",paymentMethodString);
            hashMap.put("order type",orderTypeString);
            hashMap.put("itemQuantity",itemQuantity.getText().toString());

            orderRef.child("MyOrder"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(OrderConfirmationActivity.this, "Your order is placed successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
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

                HashMap hashMap = new HashMap();
                hashMap.put("itemNames",itemNames.getText().toString());
                hashMap.put("itemDescription",orderConfirmationDescription.getText().toString());
                hashMap.put("count",String.valueOf(orderItems+1));
                hashMap.put("itemTotalAmount",totalPrice.getText().toString());
                hashMap.put("itemImage",itemImageUrl.getText().toString());
                hashMap.put("itemPlacedDate",currentDate);
                hashMap.put("itemQuantity",itemQuantity.getText().toString());
                hashMap.put("itemStatus","Ordered");
                hashMap.put("userId",currentUser);
                hashMap.put("payment method",paymentMethodString);
                hashMap.put("order type",orderTypeString);

                orderRef.child("MyOrder"+currentDateandTime).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(OrderConfirmationActivity.this, "Your order is placed successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
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