package com.example.citycare.activity.FAQ;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.citycare.R;
import com.example.citycare.activity.FAQActivity;
import com.example.citycare.activity.ReportDetailsActivity;
import com.example.citycare.activity.ReportsActivity;

public class HowToChangeLanguageActivity extends AppCompatActivity {


    private ImageView left_btn;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent s = new Intent(HowToChangeLanguageActivity.this, FAQActivity.class);
        startActivity(s);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_change_language);


        left_btn = findViewById(R.id.left_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(HowToChangeLanguageActivity.this, FAQActivity.class);
                startActivity(s);
                finish();
            }
        });
    }
}