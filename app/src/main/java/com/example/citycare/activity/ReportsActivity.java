package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.citycare.model.Form;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.citycare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ReportsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    private BottomNavigationView bottomNavigationView;
    private EditText complaintNumber;
    private Button find;
    private Form report = new Form();
    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        complaintNumber = findViewById(R.id.complaint_number);

        find = findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findComplaint();


            }
        });


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


    void findComplaint() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .whereEqualTo("id", complaintNumber.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { isEmpty = task.getResult().isEmpty();

                            if (isEmpty) {
                                Log.d(TAG, "No complaint is found");
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    report.setId(document.getData().get("id").toString());
                                    report.setType(document.getData().get("type").toString());
                                    report.setCategory(document.getData().get("category").toString());
                                    report.setLocation(document.getData().get("location").toString());
                                    report.setDescription(document.getData().get("description").toString());

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    long timeInSeconds = Long.parseLong(document.getData().get("timestamp").toString().split("seconds=")[1].replace(", nano", ""));
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    report.setTimestamp(new Date(timeInSeconds * 1000));

                                    report.setStatus(document.getData().get("status").toString());
                                    report.setFeedback(document.getData().get("feedback").toString());

                                    Intent s = new Intent(getApplicationContext(), ReportDetailsActivity.class);
                                    s.putExtra("id", report.getId());
                                    s.putExtra("type", report.getType());
                                    s.putExtra("category", report.getCategory());
                                    s.putExtra("location", report.getLocation());
                                    s.putExtra("description", report.getDescription());
                                    s.putExtra("timestamp", dateFormat.format(new Date(timeInSeconds * 1000)));
                                    s.putExtra("status", report.getStatus());
                                    s.putExtra("feedback", report.getFeedback());
                                    startActivity(s);
                                    finish();


                                }
                            }

                        } else {

                            Log.d(TAG, "No complaint is found");
                        }
                    }
                });

        db.collection("suggestions")
                .whereEqualTo("id", complaintNumber.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            isEmpty = task.getResult().isEmpty();

                            if (isEmpty) {
                                Log.d(TAG, "No suggestion is found");
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    report.setId(document.getData().get("id").toString());
                                    report.setType(document.getData().get("type").toString());
                                    report.setCategory(document.getData().get("category").toString());
                                    report.setLocation(document.getData().get("location").toString());
                                    report.setDescription(document.getData().get("description").toString());

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    long timeInSeconds = Long.parseLong(document.getData().get("timestamp").toString().split("seconds=")[1].replace(", nano", ""));
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    report.setTimestamp(new Date(timeInSeconds * 1000));

                                    report.setStatus(document.getData().get("status").toString());
                                    report.setFeedback(document.getData().get("feedback").toString());

                                    Intent s = new Intent(getApplicationContext(), ReportDetailsActivity.class);
                                    s.putExtra("id", report.getId());
                                    s.putExtra("type", report.getType());
                                    s.putExtra("category", report.getCategory());
                                    s.putExtra("location", report.getLocation());
                                    s.putExtra("description", report.getDescription());
                                    s.putExtra("timestamp", dateFormat.format(new Date(timeInSeconds * 1000)));
                                    s.putExtra("status", report.getStatus());
                                    s.putExtra("feedback", report.getFeedback());
                                    startActivity(s);
                                    finish();


                                }
                            }

                        } else {

                            Log.d(TAG, "No suggestion is found");
                        }
                    }
                });
    }

}
