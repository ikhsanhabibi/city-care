package com.example.citycare.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.citycare.R;
import com.example.citycare.activity.SignupSigninActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    public static final String TAG = "TAG";
    View view;

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button continue_btn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private ProgressBar progress_bar;

    public SignupFragment() {

    }

    //Checking valid email pattern
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextName = view.findViewById(R.id.name);
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        continue_btn = view.findViewById(R.id.continue_btn);
        progress_bar = view.findViewById(R.id.progressBar);
        progress_bar.setVisibility(View.INVISIBLE);

        continue_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString();
                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();

                if (name.isEmpty()) {
                    editTextName.setError(getResources().getString(R.string.name_required));
                    editTextName.requestFocus();
                    return;
                } else if (email.isEmpty()) {
                    editTextEmail.setError(getResources().getString(R.string.email_required));
                    editTextEmail.requestFocus();
                    return;

                } else if (!isEmailValid(email)) {
                    editTextEmail.setError(getResources().getString(R.string.enter_valid_email));
                    editTextEmail.requestFocus();
                    return;
                } else if (password.isEmpty()) {
                    editTextPassword.setError(getResources().getString(R.string.enter_password));
                    editTextEmail.requestFocus();
                    return;
                } else if (password.length() < 6) {
                    editTextPassword.setError(getResources().getString(R.string.min_password));
                    editTextPassword.requestFocus();
                    return;
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    DocumentReference docRef = firestore.collection("users").document(email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "User already exists.");
                                    Toast.makeText(getActivity(), getResources().getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "Creating new user");
                                    createAccount(email, password, name);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // create account with email and password
    public void createAccount(final String email, final String password, final String name) {
        progress_bar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.register_succesful), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), SignupSigninActivity.class));

                    DocumentReference documentReference = firestore.collection("users").document(email);

                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);


                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: User is saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error! User document not saved!", e);
                        }
                    });

                } else {
                    startActivity(new Intent(getActivity(), SignupSigninActivity.class));
                }
            }
        });
    }


}
