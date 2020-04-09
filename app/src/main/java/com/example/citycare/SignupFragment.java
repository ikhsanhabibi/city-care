package com.example.citycare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    View view;

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button continue_btn;

    private FirebaseAuth mAuth;

    public SignupFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        editTextName = (EditText) view.findViewById(R.id.name);
        editTextEmail = (EditText) view.findViewById(R.id.email);
        editTextPassword = (EditText) view.findViewById(R.id.password);
        continue_btn = (Button) view.findViewById(R.id.continue_btn);

        continue_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();


                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                else if (password.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextEmail.requestFocus();
                    return;
                }

                else if (name.isEmpty()) {
                    editTextName.setError("Name is required");
                    editTextName.requestFocus();
                    return;
                }

                else if (!isEmailValid(email)) {
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                else if (password.length() < 6) {
                    editTextPassword.setError("Minimum length of the password should be 6");
                    editTextPassword.requestFocus();
                    return;
                }

                else if (!(email.isEmpty() && password.isEmpty())) {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "User registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), SignupSigninActivity.class));
                            }
                            else {
                                startActivity(new Intent(getActivity(), NavigationActivity.class));
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(getActivity(), "Error!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    //Checking valid email pattern
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
