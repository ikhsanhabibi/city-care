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
import com.example.citycare.activity.FAQ.HowToChangeLanguageActivity;
import com.example.citycare.activity.FAQ.HowToRetrieveComplaintActivity;
import com.example.citycare.activity.FAQ.HowToSubmitComplaintActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FAQActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView howToSubmitComplaint, howToRetrieveComplaint, howToChangeTheLanguage;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);


        howToSubmitComplaint = findViewById(R.id.how_to_submit_complaint);
        howToSubmitComplaint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FAQActivity.this, HowToSubmitComplaintActivity.class);
                startActivity(intent);
            }
        });


        howToRetrieveComplaint = findViewById(R.id.how_to_retrieve_complaint);
        howToRetrieveComplaint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FAQActivity.this, HowToRetrieveComplaintActivity.class);
                startActivity(intent);
            }
        });

        howToChangeTheLanguage = findViewById(R.id.how_to_change_the_language);
        howToChangeTheLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FAQActivity.this, HowToChangeLanguageActivity.class);
                startActivity(intent);
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.complaint:
                        Intent intent2 = new Intent(FAQActivity.this, ComplaintActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.reports:
                        Intent intent3 = new Intent(FAQActivity.this, ReportsActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.faq:
                        break;
                    case R.id.more:
                        Intent intent4 = new Intent(FAQActivity.this, MoreActivity.class);
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
