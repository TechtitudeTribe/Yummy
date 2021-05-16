package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AboutUsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference appRef;
    private ProgressBar progressBar;
    private ImageView facebook, gmail, phone;
    private static final int REQUEST_CALL = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_us_fragment, container, false);

        appRef = FirebaseDatabase.getInstance().getReference().child("App Icon");
        progressBar = (ProgressBar) v.findViewById(R.id.about_progress_bar);

        facebook = (ImageView) v.findViewById(R.id.about_facebook);
        gmail = (ImageView) v.findViewById(R.id.about_gmail);
        phone = (ImageView) v.findViewById(R.id.about_phone);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(getActivity());
                startActivity(facebookIntent);
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = openGmail(getActivity());
                startActivity(emailIntent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.app_icon_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressBar.setVisibility(View.VISIBLE);
        displayAppIcons();

        return v;
    }

    private void displayAppIcons() {
        Query sortRef = appRef.orderByChild("count");

        com.firebase.ui.database.FirebaseRecyclerAdapter<AboutUsAdapter,AboutViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AboutUsAdapter, AboutViewHolder>(
                        AboutUsAdapter.class,
                        R.layout.app_icon_layout,
                        AboutViewHolder.class,
                        sortRef
                ) {
                    @Override
                    protected void populateViewHolder(AboutViewHolder aboutViewHolder, AboutUsAdapter aboutUsAdapter, int i) {
                        aboutViewHolder.setAppIcon(getActivity(),aboutUsAdapter.getAppIcon());
                        progressBar.setVisibility(View.GONE);
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AboutViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AboutViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAppIcon(Context ctx, String appIcon)
        {
            ImageView imageView = (ImageView) mView.findViewById(R.id.about_app_icon);
            Picasso.with(ctx).load(appIcon).into(imageView);
        }
    }

    public static Intent openFacebook(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/101911428711055"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Ple" +
                    "+ase install facebook...", Toast.LENGTH_SHORT).show();
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100027918345946"));
        }

    }

    public static Intent openGmail(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.google.android.gm", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:techtitudetribe@gmail.com"));
        } catch (Exception ex) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/mail/u/2/#inbox?compose=new"));
        }
    }

    private void makePhoneCall() {
        String number = "7217281579";
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getActivity(),"Permission Denied...",Toast.LENGTH_SHORT).show();
            }
        }
    }

}