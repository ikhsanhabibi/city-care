package com.example.citycare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.citycare.R;
import com.example.citycare.model.Complaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ComplaintFormActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    TextView select_location, select_picture;
    View view;
    String savedAddress;
    private ImageView left_btn;
    private Spinner spinner;
    private Button continue_btn;
    private EditText editTextDescription;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

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
                startActivity(s);
                finish();

            }
        });

        select_location = findViewById(R.id.selectLocation);
        select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(s);
                finish();
            }
        });

        editTextDescription = findViewById(R.id.description);


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
                    //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Saved Preferences
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String category = sharedPreferences.getString("category", "0");
        String desc = sharedPreferences.getString("desc", "");
        String location = sharedPreferences.getString("location", getString(R.string.select_location));

        String savedAddress = getIntent().getStringExtra("address");

        if (savedAddress != null) {
            select_location.setText(savedAddress.trim());
        } else if (!location.equals("")) {
            select_location.setText(location);
        } else {
            select_location.setText(getString(R.string.select_location));
        }

        spinner.setSelection(Integer.parseInt(category));
        editTextDescription.setText(desc);


        // setOnClickListener
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringDescription = editTextDescription.getText().toString();
                String stringEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String stringCategory = spinner.getSelectedItem().toString();
                String stringLocation = select_location.getText().toString();

                if (stringCategory == "Choose category" || stringCategory.isEmpty()) {
                    spinner.requestFocus();
                    Toast.makeText(ComplaintFormActivity.this, "Choose category please!.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (stringCategory == "" || stringDescription.isEmpty()) {
                    editTextDescription.requestFocus();
                    Toast.makeText(ComplaintFormActivity.this, "Write something please!.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (stringLocation.equals("Select location")) {
                    select_location.requestFocus();
                    Toast.makeText(ComplaintFormActivity.this, "Choose a location please!.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createComplaint(stringDescription, stringEmail, stringCategory, stringLocation);
                }


            }
        });

    }

    public void createComplaint(String description, String email, final String category, String location) {

        Complaint complaint;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newComplaintRef = db.collection("complaints").document();

        //DocumentReference userRef = db.collection("users").document(email);
        //String name = userRef.get().getResult().getString("name");

        complaint = new Complaint();
        complaint.setStatus("SENT");
        complaint.setDescription(description);
        complaint.setEmail(db.collection("users").document(email).getId());
        complaint.setCategory(category);
        complaint.setLocation(location);

        newComplaintRef.set(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), ComplaintSentActivity.class));
                clearForm();
                Log.d(TAG, "Complaint sent.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SavePreferences("desc", editTextDescription.getText().toString());
        SavePreferences("location", select_location.getText().toString());
        SavePreferences("category", String.valueOf(spinner.getSelectedItemPosition()));
    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void clearForm() {
        select_location.setText("");
        editTextDescription.setText("");
        spinner.setSelection(0);
    }


}
