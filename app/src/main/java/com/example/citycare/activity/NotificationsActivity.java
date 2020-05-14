package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.citycare.adapter.NotificationsAdapter;
import com.example.citycare.R;
import com.example.citycare.model.Complaint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    CollectionReference notifications;
    ArrayList<Complaint> notificationsArrayList = new ArrayList<Complaint>();
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String email;
    RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Firebase
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        email = firebaseUser.getEmail();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifications = FirebaseFirestore.getInstance().collection("notifications");
        fetchAllNotifications();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent2 = new Intent(NotificationsActivity.this, HomeActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.notifications:
                        break;
                    case R.id.faq:
                        Intent intent3 = new Intent(NotificationsActivity.this, FAQActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.profile:
                        Intent intent4 = new Intent(NotificationsActivity.this, ProfileActivity.class);
                        startActivity(intent4);
                        finish();
                        break;
                }
                return true;
            }

        });

    }

    private void fetchAllNotifications() {

        //notificationsArrayList.clear();
        notifications.whereEqualTo("user", email).orderBy("timestamp", Query.Direction.DESCENDING).limit(8).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Complaint data = new Complaint();
                        String location = doc.getData().get("location").toString();
                        String category = doc.getData().get("category").toString();
                        String status = doc.getData().get("status").toString();

                        String str = doc.getData().get("timestamp").toString();
                        String result = str.substring(str.indexOf("=") + 1, str.indexOf(","));
                        long seconds = Long.parseLong(result, 10);
                        long millis = seconds * 1000;
                        Date date = new Date(millis);

                        data.setLocation(location);
                        data.setCategory(category);
                        data.setStatus(status);
                        data.setTimestamp(date);
                        notificationsArrayList.add(data);
                    }

                    NotificationsAdapter adapter = new NotificationsAdapter(NotificationsActivity.this, notificationsArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
