package com.example.citycare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ComplaintFormActivity extends AppCompatActivity {

    private ImageView left_btn, add_location;
    private Spinner spinner;
    public static final String TAG = "TAG";
    private Button continue_btn;
    private EditText editTextTitle, editTextDescription;
    View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_form);

        // setOnClickListener
        left_btn = findViewById(R.id.left_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), NavigationActivity.class);
                s.putExtra("fragment", "signUp");
                startActivity(s);
                finish();


            }
        });

        add_location = findViewById(R.id.add_location);
        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(s);
                finish();


            }
        });


        editTextTitle = (EditText) findViewById(R.id.title);
        editTextDescription = (EditText) findViewById(R.id.description);


        // Spinner
        spinner = findViewById(R.id.spinner);
        List<String> categories = new ArrayList<>();
        categories.add(0, "Choose category");
        categories.add("Road");
        categories.add("Park");
        categories.add("Bridge");
        categories.add("Side walk");
        categories.add("Other");

        // Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);

        // Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                if (parent.getItemAtPosition(position).equals("Choose category")) {

                } else {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // setOnClickListener
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String stringTitle = editTextTitle.getText().toString();
                String stringDescription = editTextDescription.getText().toString();
                String stringEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String stringCategory = spinner.getSelectedItem().toString();

                createComplaint(stringTitle, stringDescription, stringEmail, stringCategory);

            }
        });


    }

    public void createComplaint(final String title, final String description, String email, String category) {

        Complaint complaint;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newComplaintRef = db.collection("complaints").document();

        //DocumentReference userRef = db.collection("users").document(email);
        //String name = userRef.get().getResult().getString("name");

        complaint = new Complaint();
        complaint.setStatus("SENT");
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setEmail(db.collection("users").document(email).getId());
        complaint.setCategory(category);
        //complaint.setName(name);

        newComplaintRef.set(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Complaint sent.");
                startActivity(new Intent(getApplicationContext(), ComplaintSentActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });

    }



}
