package com.example.citycare.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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


import com.example.citycare.BuildConfig;
import com.example.citycare.R;
import com.example.citycare.model.Complaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ComplaintFormActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    TextView select_location, upload_picture;
    private ImageView left_btn;
    private Spinner spinner;
    private Button continue_btn;
    private EditText editTextDescription;
    private Context context;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String imgPath = null;
    private String filename;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_form);
        context = ComplaintFormActivity.this;

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


        upload_picture = findViewById(R.id.uploadPictures);
        upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    selectImage();
                }


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


    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(ComplaintFormActivity.this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column);
        cursor.close();
        return result;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imgPath = image.getAbsolutePath();
        return image;
    }

    private void selectImage() {
        try {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Option");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // Ensure that there's a camera activity to handle the intent
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    // Create the File where the photo should go
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File

                                    }
                                    // Continue only if the File was successfully created
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(ComplaintFormActivity.this,
                                                BuildConfig.APPLICATION_ID + ".provider",
                                                photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
                                        int index = photoFile.getName().lastIndexOf("/");
                                        takePictureIntent.putExtra("filename", photoFile.getName().substring(index + 1));
                                        filename = photoFile.getName().substring(index + 1);
                                        System.out.println(filename);
                                        upload_picture.setText(photoFile.getName().substring(index + 1));
                                    }
                                }
                            }
                        }

                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("CAMERA is necessary");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CAMERA}, 100);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
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
                //((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
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
