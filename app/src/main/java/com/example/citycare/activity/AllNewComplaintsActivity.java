package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.citycare.R;
import com.example.citycare.adapter.ComplaintAdapter;
import com.example.citycare.model.Form;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class AllNewComplaintsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CollectionReference complaints;
    ArrayList<Form> complaintsArrayList = new ArrayList<Form>();
    private ImageView left_btn;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AllNewComplaintsActivity.this, ReportsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_new_complaints);


        left_btn = findViewById(R.id.left_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(AllNewComplaintsActivity.this, ReportsActivity.class);
                startActivity(s);
                finish();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase
        complaints = FirebaseFirestore.getInstance().collection("complaints");

        fetchAllComplaints();

    }

    private void fetchAllComplaints() {
        complaintsArrayList.clear();
        complaints.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private static final String TAG = "TAG";

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Form data = new Form();
                        String location = doc.getData().get("location").toString();
                        String category = doc.getData().get("category").toString();
                        String description = doc.getData().get("description").toString();
                        String status = doc.getData().get("status").toString();

                        String str = doc.getData().get("timestamp").toString();
                        String result = str.substring(str.indexOf("=") + 1, str.indexOf(","));
                        long seconds = Long.parseLong(result, 10);
                        long millis = seconds * 1000;
                        Date date = new Date(millis);

                        String imageUrl = doc.getData().get("imageUrl").toString();

                        data.setLocation(location);
                        data.setCategory(category);
                        data.setStatus(status);
                        data.setDescription(description);
                        data.setTimestamp(date);
                        data.setImageUrl(imageUrl);

                        complaintsArrayList.add(data);
                    }

                    // Complaint adapter
                    ComplaintAdapter adapter = new ComplaintAdapter(AllNewComplaintsActivity.this, complaintsArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
