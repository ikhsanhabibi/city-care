package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.citycare.R;
import com.example.citycare.model.Form;

public class ComplaintSentActivity extends AppCompatActivity {

    Button my_complaints;
    TextView complaint_number;
    Form complaint = new Form();

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

        complaint.setId(getIntent().getStringExtra("id"));
        complaint_number = findViewById(R.id.complaint_number);
        complaint_number.setText(complaint.getId());

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