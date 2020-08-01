package com.example.citycare.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;

import com.example.citycare.BuildConfig;
import com.example.citycare.R;
import com.example.citycare.model.Form;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class ComplaintFormActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int MAX_LENGTH = 10;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private Uri imgUri;
    private String downloadUrl;
    private TextView select_location, upload_picture;
    private Bitmap bitmap;
    private ImageView left_btn;
    private Spinner spinner;
    private Button continue_btn;
    private EditText editTextDescription;
    private Context context;
    private String imgPath = null;
    private float latitude;
    private float longitude;
    private ProgressBar progress_bar;

    public static String generateRandomID(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent s = new Intent(ComplaintFormActivity.this, ComplaintActivity.class);
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
                Intent s = new Intent(getApplicationContext(), ComplaintActivity.class);
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
                s.putExtra("FROM_ACTIVITY", "COMPLAINT");
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

    private void createSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add(0, getResources().getString(R.string.choose_category));
        categories.add(getResources().getString(R.string.airport));
        categories.add(getResources().getString(R.string.bike_lane));
        categories.add(getResources().getString(R.string.bridge));
        categories.add(getResources().getString(R.string.bus));
        categories.add(getResources().getString(R.string.canal));
        categories.add(getResources().getString(R.string.field));
        categories.add(getResources().getString(R.string.park));
        categories.add(getResources().getString(R.string.parking_area));
        categories.add(getResources().getString(R.string.port));
        categories.add(getResources().getString(R.string.road));
        categories.add(getResources().getString(R.string.side_walk));
        categories.add(getResources().getString(R.string.traffic_sign));
        categories.add(getResources().getString(R.string.train));
        categories.add(getResources().getString(R.string.tram));
        categories.add(getResources().getString(R.string.tunnel));
        categories.add(getResources().getString(R.string.waterway));
        categories.add(getResources().getString(R.string.other));


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
                if (parent.getItemAtPosition(position).equals(getResources().getString(R.string.choose_category))) {

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

    private void requestPermissionsCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
            alertBuilder.setMessage(getResources().getString(R.string.camera_necessary));
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
            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
            alertBuilder.setMessage(getResources().getString(R.string.storage_necessary));
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

    private void selectImage() {
        try {
            final CharSequence[] options = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallery), getResources().getString(R.string.cancel)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.select_option));
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals(getResources().getString(R.string.take_photo))) {
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(ComplaintFormActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                //If permission is granted
                                try {
                                    takePicture();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                requestPermissionsCamera();

                            }
                        } else {
                            //no need to check permissions in android versions lower then marshmallow
                            try {
                                takePicture();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (options[item].equals(getResources().getString(R.string.choose_from_gallery))) {
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

                    } else if (options[item].equals(getResources().getString(R.string.cancel))) {
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

    private void takePicture() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        imgUri = Uri.fromFile(createImageFile());

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
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
            }
        }

    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "image_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",         /* suffix */
                storageDir            /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imgPath = image.getAbsolutePath();
        return image;
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
        if (bitmap != null) {
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
                    }
                }
            });

        } else {
            Intent newIntent = new Intent(this, ComplaintFormActivity.class);
            startActivity(newIntent);
            finish();
        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE_GALLERY) {
            imgUri = data.getData();
            imgPath = getRealPathFromURI(context, imgUri);
            bitmap = null;

            String[] bits = imgPath.split("/");
            String pictureName = bits[bits.length - 1];

            // Check image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(imgUri),
                        null,
                        options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;


            if (imageHeight > 1920 || imageWidth > 1920) {
                upload_picture.setText(R.string.upload_picture);
                Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.image_too_large), Toast.LENGTH_SHORT).show();
                return;

            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                upload_picture.setText(pictureName);
            }

            ImageUpload(imgUri, bitmap);

        } else if (requestCode == PICK_IMAGE_CAMERA) {

            try {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] bits = imgPath.split("/");
            String pictureName = bits[bits.length - 1];

            upload_picture.setText(pictureName);
            ImageUpload(imgUri, bitmap);
        } else {
            Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.select_image_please), Toast.LENGTH_SHORT).show();
        }


    }

    public void createComplaint(String category, String description, String
            location, double latitude, double longitude) {

        progress_bar.setVisibility(View.VISIBLE);

        // Saved Preferences
        restoreLastValue();

        final Form complaint;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newComplaintRef = db.collection("complaints").document();

        complaint = new Form();
        complaint.setId(generateRandomID(MAX_LENGTH));
        complaint.setCategory(category);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setType(getResources().getString(R.string.complaint_type));
        complaint.setStatus("SENT");
        complaint.setImageUrl(downloadUrl);
        complaint.setFeedback("-");

        newComplaintRef.set(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress_bar.setVisibility(View.GONE);

                Intent s = new Intent(getApplicationContext(), ComplaintSentActivity.class);
                s.putExtra("id", complaint.getId());
                startActivity(s);
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

    private void sendForm() {
        String stringCategory = spinner.getSelectedItem().toString();
        String stringDescription = editTextDescription.getText().toString();
        String stringPicture = upload_picture.getText().toString();
        String stringLocation = select_location.getText().toString();

        if ((float) getIntent().getDoubleExtra("latitude", 0) != 0.0 && getIntent().getDoubleExtra("longitude", 0) != 0.0) {
            latitude = (float) getIntent().getDoubleExtra("latitude", 0);
            longitude = (float) getIntent().getDoubleExtra("longitude", 0);
        }

        if (stringCategory.equals( getResources().getString(R.string.choose_category)) || stringCategory.isEmpty()) {
            spinner.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.choose_category_please), Toast.LENGTH_SHORT).show();
            return;
        } else if (stringCategory == "" || stringDescription.isEmpty()) {
            editTextDescription.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.write_something), Toast.LENGTH_SHORT).show();
            return;
        } else if (stringPicture.equals(getResources().getString(R.string.upload_picture))) {
            upload_picture.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.pick_picture), Toast.LENGTH_SHORT).show();
            return;
        } else if (stringLocation.equals(getResources().getString(R.string.select_location))) {
            select_location.requestFocus();
            Toast.makeText(ComplaintFormActivity.this, getResources().getString(R.string.choose_location), Toast.LENGTH_SHORT).show();
            return;
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            createComplaint(stringCategory, stringDescription, stringLocation, latitude, longitude);
        }
    }

    public void clearForm() {
        select_location.setText(getResources().getString(R.string.select_location));
        editTextDescription.setText("");
        spinner.setSelection(0);
        upload_picture.setText(getResources().getString(R.string.upload_picture));
    }

    private void restoreLastValue() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String category = sharedPreferences.getString("category", "0");
        String desc = sharedPreferences.getString("desc", "");
        String picture = sharedPreferences.getString("uploaded_picture", getString(R.string.upload_picture));
        String location = sharedPreferences.getString("location", getString(R.string.select_location));
        this.downloadUrl = sharedPreferences.getString("downloadUrl", "");
        this.latitude = Float.parseFloat(sharedPreferences.getString("latitude", "0"));
        this.longitude = Float.parseFloat(sharedPreferences.getString("longitude", "0"));

        String savedAddress = getIntent().getStringExtra("address");

        if (savedAddress != null) {
            select_location.setText(savedAddress.trim());
        } else if (!location.equals("")) {
            select_location.setText(R.string.select_location);
        } else {
            select_location.setText(getString(R.string.select_location));
        }

        spinner.setSelection(Integer.parseInt(category));
        editTextDescription.setText(desc);

        if (picture.contains(".jpg") || picture.contains(".JPG") || picture.contains(".JPEG") || picture.contains(".jpeg") || picture.contains(".png") || picture.contains(".PNG"))
            upload_picture.setText(picture);
        else {
            upload_picture.setText(getString(R.string.upload_picture));
        }
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
        SavePreferences("uploaded_picture", upload_picture.getText().toString());
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

}
