package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReportsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private EditText complaintNumber;
    private Button find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        complaintNumber = findViewById(R.id.complaint_number);

        find = findViewById(R.id.find);





        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.complaint:
                        Intent intent1 = new Intent(ReportsActivity.this, ComplaintActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.reports:
                        break;
                    case R.id.faq:
                        Intent intent3 = new Intent(ReportsActivity.this, FAQActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.more:
                        Intent intent4 = new Intent(ReportsActivity.this, MoreActivity.class);
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
