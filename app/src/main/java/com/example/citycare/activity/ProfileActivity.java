package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.citycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {


    TextView logOut, reports, language_setting, email;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private ProgressBar progress_bar;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase
        String getEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email = findViewById(R.id.email);
        email.setText(getEmail);

        reports = findViewById(R.id.reports);
        reports.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });


        language_setting = findViewById(R.id.languageSetting);
        language_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LanguageSettingActivity.class);
                startActivity(intent);
            }
        });

        progress_bar = findViewById(R.id.progressBar);
        progress_bar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.log_out);
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, getResources().getString(R.string.logout_succesful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, Welcome2Activity.class);
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
                    case R.id.home:
                        Intent intent2 = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent intent3 = new Intent(ProfileActivity.this, NotificationsActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.faq:
                        Intent intent4 = new Intent(ProfileActivity.this, FAQActivity.class);
                        startActivity(intent4);
                        finish();
                        break;
                    case R.id.profile:

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
