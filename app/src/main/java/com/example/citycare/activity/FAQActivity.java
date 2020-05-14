package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FAQActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent2 = new Intent(FAQActivity.this, HomeActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent intent3 = new Intent(FAQActivity.this, NotificationsActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.faq:
                         break;
                    case R.id.profile:
                        Intent intent4 = new Intent(FAQActivity.this, ProfileActivity.class);
                        startActivity(intent4);
                        finish();
                        break;
                }
                return true;
            }

        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
