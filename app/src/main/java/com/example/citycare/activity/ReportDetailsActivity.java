package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.citycare.R;
import com.example.citycare.model.Form;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReportDetailsActivity extends AppCompatActivity {

    private Form reportDetails = new Form();
    private TextView id, type, category, location, description, timestamp, status, feedback, last_update;
    private ImageView left_btn;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent s = new Intent(ReportDetailsActivity.this, ReportsActivity.class);
        startActivity(s);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        left_btn = findViewById(R.id.left_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(ReportDetailsActivity.this, ReportsActivity.class);
                startActivity(s);
                finish();
            }
        });

        fetchData();

        id = findViewById(R.id.reportID);
        id.setText(reportDetails.getId());

        type = findViewById(R.id.type);
        type.setText(reportDetails.getType());

        category = findViewById(R.id.category);
        category.setText(reportDetails.getCategory());

        location = findViewById(R.id.location);
        location.setText(reportDetails.getLocation());

        description = findViewById(R.id.description);
        description.setText(reportDetails.getDescription());

        timestamp = findViewById(R.id.submitted);
        timestamp.setText(getIntent().getStringExtra("timestamp"));

        status = findViewById(R.id.status);
        status.setText(reportDetails.getStatus());

        feedback = findViewById(R.id.feedback);
        feedback.setText(reportDetails.getFeedback());

        last_update = findViewById(R.id.last_update);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        last_update.setText(getResources().getString(R.string.last_update) + " " + dateFormat.format(new Date()));


    }


    void fetchData() {
        reportDetails.setId(getIntent().getStringExtra("id"));
        reportDetails.setType(getIntent().getStringExtra("type"));
        reportDetails.setCategory(getIntent().getStringExtra("category"));
        reportDetails.setLocation(getIntent().getStringExtra("location"));
        reportDetails.setDescription(getIntent().getStringExtra("description"));
        reportDetails.setStatus(getIntent().getStringExtra("status"));
        reportDetails.setFeedback(getIntent().getStringExtra("feedback"));
    }
}
