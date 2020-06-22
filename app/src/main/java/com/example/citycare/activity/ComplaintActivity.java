package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ComplaintActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView complaint, suggestion;


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        complaint = findViewById(R.id.complaint);
        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(ComplaintActivity.this, ComplaintFormActivity.class);
                startActivity(s);
                finish();
            }
        });

        suggestion = findViewById(R.id.suggestion);
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(ComplaintActivity.this, SuggestionFormActivity.class);
                startActivity(s);
                finish();
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.complaint:
                        break;
                    case R.id.reports:
                        Intent intent2 = new Intent(ComplaintActivity.this, ReportsActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.faq:
                        Intent intent3 = new Intent(ComplaintActivity.this, FAQActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.more:
                        Intent intent4 = new Intent(ComplaintActivity.this, MoreActivity.class);
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
