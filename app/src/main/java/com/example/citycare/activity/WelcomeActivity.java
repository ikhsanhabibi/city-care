package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.citycare.R;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcometo;
    TextView ccs;
    TextView report_it;
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // One time activity
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_welcome", false)) {
            Intent intent = new Intent(this, ComplaintActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_welcome", true);
            ed.commit();
        }


        // Change font
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Bold.ttf");
        Typeface medium = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Medium.ttf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Regular.ttf");

        welcometo = findViewById(R.id.welcometo);
        welcometo.setTypeface(bold);

        ccs = findViewById(R.id.ccs);
        ccs.setTypeface(bold);

        report_it = findViewById(R.id.report_it);
        report_it.setTypeface(medium);

        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setTypeface(bold);


        // setOnClickListener
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(WelcomeActivity.this, ComplaintActivity.class);
                startActivity(s);
                finish();
            }
        });


    }
}
