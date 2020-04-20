package com.example.citycare.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.citycare.R;
import com.example.citycare.model.Complaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;


import java.util.ArrayList;
import java.util.List;


public class ComplaintFormActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    TextView select_location, upload_picture;
    View view;
    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    private ImageView left_btn;
    private Spinner spinner;
    private Button continue_btn;
    private EditText editTextDescription;
    private ViewGroup mSelectedImagesContainer;

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

        // Spinner
        spinner = findViewById(R.id.spinner);
        createSpinner();

        editTextDescription = findViewById(R.id.description);

        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);

        upload_picture = findViewById(R.id.uploadPictures);
        upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getApplicationContext(), Manifest.permission.CAMERA)) {

                        //Show permission dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Enable Camera").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {

                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.CAMERA}, 100);
                    }
                }

                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

                Config config = new Config();
                config.setCameraHeight(R.dimen.app_camera_height);
                config.setToolbarTitleRes(R.string.custom_title);
                config.setSelectionMin(1);
                config.setSelectionLimit(10);
                config.setSelectedBottomHeight(R.dimen.bottom_height);
                config.setFlashOn(true);

                getImages(config);

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

        // Saved Preferences
        restoreLastValue();

        // setOnClickListener
        continue_btn = findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForm();

            }
        });

    }


    private void getImages(Config config) {
        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(ComplaintFormActivity.this, ImagePickerActivity.class);

        if (image_uris != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris);
        }


        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        if (image_uris.size() >= 1) {
            mSelectedImagesContainer.setVisibility(View.VISIBLE);
        }

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : image_uris) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }


            }
        }
    }

    private void restoreLastValue() {
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
    }

    private void sendForm() {
        String stringDescription = editTextDescription.getText().toString();
        String stringEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String stringCategory = spinner.getSelectedItem().toString();
        String stringLocation = select_location.getText().toString();

        if (stringCategory == "Choose category" || stringCategory.isEmpty()) {
            spinner.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Choose category please!", Toast.LENGTH_SHORT).show();
            return;
        } else if (stringCategory == "" || stringDescription.isEmpty()) {
            editTextDescription.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Write something please!", Toast.LENGTH_SHORT).show();
            return;
        } else if (stringLocation.equals("Select location")) {
            select_location.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Choose a location please!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            createComplaint(stringDescription, stringEmail, stringCategory, stringLocation);
        }
    }

    private void createSpinner() {
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
