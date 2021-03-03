package com.techtitudetribe.yummy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity {

    private MeowBottomNavigation meo;
    private final static int ID_PROFILE=1;
    private final static int ID_CONTACT=2;
    private final static int ID_HOME=3;
    private final static int ID_CART=4;
    private final static int ID_ABOUT=5;
    private long backPressedTime;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meo=(MeowBottomNavigation) findViewById(R.id.bottom_nav);
        meo.add(new MeowBottomNavigation.Model(ID_PROFILE, R.drawable.icon_profile));
        meo.add(new MeowBottomNavigation.Model(ID_CONTACT, R.drawable.icon_contact));
        meo.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.icon_home));
        meo.add(new MeowBottomNavigation.Model(ID_CART, R.drawable.icon_cart));
        meo.add(new MeowBottomNavigation.Model(ID_ABOUT, R.drawable.icon_about));

        //mAuth = FirebaseAuth.getInstance();

        if(savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }

        meo.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {
                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_CONTACT : select_fragment = new ContactUsFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });
        meo.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {
                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_CONTACT : select_fragment = new ContactUsFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });
        meo.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {

                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_CONTACT : select_fragment = new ContactUsFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });
        meo.show(ID_HOME,true);

    }

    @Override
    public void onBackPressed() {
        meo.show(ID_HOME,true);
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit...", Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    /*@Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
    }*/

    /*(private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LocationActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }*/
}