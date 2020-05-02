package com.example.citycare.activity;

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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.citycare.R;
import com.example.citycare.model.Complaint;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ComplaintFormActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    public Uri imgUri;
    public String downloadUrl;
    TextView select_location, upload_picture;
    private ImageView left_btn;
    private Spinner spinner;
    private Button continue_btn;
    private EditText editTextDescription;
    private Context context;
    private String imgPath = null;
    private float latitude;
    private float longitude;
    private ProgressBar progress_bar;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent s = new Intent(ComplaintFormActivity.this, NavigationActivity.class);
        startActivity(s);
        finish();
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
            public void onClick(View v) {
                selectImage();
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

        progress_bar = findViewById(R.id.progressBar);
        progress_bar.setVisibility(View.INVISIBLE);

    }

    private void takePicture() {
    }

    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_GALLERY);
    }

    public void ImageUpload(Uri imgUri, Bitmap bitmap) {
        Date myDate = new Date();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(new SimpleDateFormat("dd-MM-yyyy").format(myDate));
        final StorageReference mRef = storageReference.child("image_" + System.currentTimeMillis() + ".jpg");

        // Image Compression
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        final byte[] data = baos.toByteArray();
        UploadTask uploadTask = mRef.putFile(imgUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                mRef.putBytes(data);
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String uri = task.getResult().toString();
                    setDownloadUrl(uri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }

    public void setDownloadUrl(String url) {
        this.downloadUrl = url;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
                            if (ActivityCompat.checkSelfPermission(ComplaintFormActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                //If permission is granted
                                takePicture();
                            } else {
                                requestPermissionsCamera();

                            }
                        } else {
                            //no need to check permissions in android versions lower then marshmallow
                            takePicture();
                        }
                        takePicture();
                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(ComplaintFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                //If permission is granted
                                chooseFromGallery();
                            } else {
                                requestPermissionsStorage();
                            }
                        } else {
                            //no need to check permissions in android versions lower then marshmallow
                            chooseFromGallery();
                        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE_GALLERY) {
            imgUri = data.getData();
            imgPath = getRealPathFromURI(context, imgUri);
            Bitmap bitmap = null;

            String[] bits = imgPath.split("/");
            String pictureName = bits[bits.length - 1];

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            upload_picture.setText(pictureName);
            ImageUpload(imgUri, bitmap);
        } else if (requestCode == PICK_IMAGE_GALLERY) {

        } else {
            Toast.makeText(ComplaintFormActivity.this, "Select an image please!", Toast.LENGTH_SHORT).show();
        }


    }

    private void requestPermissionsCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("CAMERA is necessary");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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

    private void requestPermissionsStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Storage permission is necessary");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void restoreLastValue() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String category = sharedPreferences.getString("category", "0");
        String desc = sharedPreferences.getString("desc", "");
        String picture = sharedPreferences.getString("picture", getString(R.string.upload_picture));
        String location = sharedPreferences.getString("location", getString(R.string.select_location));
        this.downloadUrl = sharedPreferences.getString("downloadUrl", "");
        this.latitude = Float.parseFloat(sharedPreferences.getString("latitude", "0"));
        this.longitude = Float.parseFloat(sharedPreferences.getString("longitude", "0"));

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
        upload_picture.setText(picture);
    }

    private void sendForm() {
        String stringDescription = editTextDescription.getText().toString();
        String stringEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String stringCategory = spinner.getSelectedItem().toString();
        String stringLocation = select_location.getText().toString();
        String stringPicture = upload_picture.getText().toString();

        if ((float) getIntent().getDoubleExtra("latitude", 0) != 0.0 && getIntent().getDoubleExtra("longitude", 0) != 0.0) {
            latitude = (float) getIntent().getDoubleExtra("latitude", 0);
            longitude = (float) getIntent().getDoubleExtra("longitude", 0);
        }

        if (stringCategory == "Choose category" || stringCategory.isEmpty()) {
            spinner.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Choose category please!", Toast.LENGTH_SHORT).show();
            return;
        } else if (stringCategory == "" || stringDescription.isEmpty()) {
            editTextDescription.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Write something please!", Toast.LENGTH_SHORT).show();
            return;
        } else if (stringPicture.equals("Upload picture")) {
            upload_picture.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Pick a picture please!", Toast.LENGTH_SHORT).show();
            return;
        } else if (stringLocation.equals("Select location")) {
            select_location.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, "Choose a location please!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            createComplaint(stringDescription, stringEmail, stringCategory, stringLocation, latitude, longitude);
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

    public void createComplaint(String description, String email, String category, String
            location, double latitude, double longitude) {

        progress_bar.setVisibility(View.VISIBLE);

        // Saved Preferences
        restoreLastValue();

        Complaint complaint;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newComplaintRef = db.collection("complaints").document();

        complaint = new Complaint();
        complaint.setStatus("SENT");
        complaint.setDescription(description);
        complaint.setEmail(db.collection("users").document(email).getId());
        complaint.setCategory(category);
        complaint.setLocation(location);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImageUrl(downloadUrl);

        newComplaintRef.set(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress_bar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), ComplaintSentActivity.class));
                finish();
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
        SavePreferences("picture", upload_picture.getText().toString());
        SavePreferences("category", String.valueOf(spinner.getSelectedItemPosition()));
        SavePreferences("downloadUrl", this.downloadUrl);
        SavePreferences("latitude", String.valueOf(this.latitude));
        SavePreferences("longitude", String.valueOf(this.longitude));
    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void clearForm() {
        select_location.setText(getString(R.string.select_location));
        editTextDescription.setText("");
        spinner.setSelection(0);
        upload_picture.setText(getString(R.string.upload_picture));
    }


}
