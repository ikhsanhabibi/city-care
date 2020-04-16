package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.citycare.fragment.HelpFragment;
import com.example.citycare.fragment.HomeFragment;
import com.example.citycare.fragment.NotificationsFragment;
import com.example.citycare.fragment.ProfileFragment;
import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;

            switch (menuItem.getItemId()){
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.notifications:
                    fragment = new NotificationsFragment();
                    break;
                case R.id.help:
                    fragment = new HelpFragment();
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            return true;
        }
    };
}
