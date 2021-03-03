package com.techtitudetribe.yummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class AboutUsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference appRef;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_us_fragment, container, false);

        appRef = FirebaseDatabase.getInstance().getReference().child("App Icon");
        progressBar = (ProgressBar) v.findViewById(R.id.about_progress_bar);

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
}