package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.citycare.R;

public class ComplaintSentActivity extends AppCompatActivity {

    Button my_complaints;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ComplaintSentActivity.this, ComplaintFormActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_sent);

        // setOnClickListener
        my_complaints = findViewById(R.id.my_complaints);
        my_complaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), ReportsActivity.class);
                startActivity(s);
                finish();
            }
        });
    }
}