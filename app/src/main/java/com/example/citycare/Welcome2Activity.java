package com.example.citycare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Welcome2Activity extends AppCompatActivity {

    TextView welcometo;
    TextView ccs;
    TextView report_it;
    Button sign_up;
    Button sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_2);
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

        sign_up = findViewById(R.id.sign_up);
        sign_up.setTypeface(bold);

        sign_in = findViewById(R.id.sign_in);
        sign_in.setTypeface(bold);


        // setOnClickListener
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent s = new Intent(getApplicationContext(), SignupSigninActivity.class);
                s.putExtra("fragment","signUp");
                startActivity(s);
                finish();


            }
        });



        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), SignupSigninActivity.class);
                startActivity(s);
                finish();
            }});


    }




}
