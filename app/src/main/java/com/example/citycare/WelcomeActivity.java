package com.example.citycare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcometo;
    TextView ccs;
    TextView get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // One time activity
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, SignupSigninActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }

        // Change font
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Bold.ttf");
        Typeface medium = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Medium.ttf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/NeusaNextStd-Regular.ttf");

        welcometo = findViewById(R.id.welcometo);
        welcometo.setTypeface(regular);

        ccs = findViewById(R.id.ccs);
        ccs.setTypeface(bold);

        get_started = findViewById(R.id.get_started);
        get_started.setTypeface(bold);


        // setOnClickListener
        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), SignupSigninActivity.class);
                startActivity(s);
                finish();
            }
        });

    }
}
