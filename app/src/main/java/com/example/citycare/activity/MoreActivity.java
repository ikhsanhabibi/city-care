package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MoreActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    TextView  language_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);





        language_setting = findViewById(R.id.languageSetting);
        language_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, LanguageSettingActivity.class);
                startActivity(intent);
            }
        });







        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.complaint:
                        Intent intent1 = new Intent(MoreActivity.this, ComplaintActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.reports:
                        Intent intent2 = new Intent(MoreActivity.this, ReportsActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.faq:
                        Intent intent3 = new Intent(MoreActivity.this, FAQActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.more:
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
