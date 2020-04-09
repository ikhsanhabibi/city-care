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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigninFragment extends Fragment {

    View view;
    private EditText editTextEmail, editTextPassword;
    private Button continue_btn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public SigninFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) view.findViewById(R.id.email);
        editTextPassword = (EditText) view.findViewById(R.id.password);
        continue_btn = (Button) view.findViewById(R.id.continue_btn);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String pwd = editTextPassword.getText().toString();
                if (email.isEmpty()) {
                    editTextEmail.setError("Please enter your email!");
                    editTextEmail.requestFocus();
                } else if (pwd.isEmpty()) {
                    editTextPassword.setError("Please enter your password!");
                    editTextPassword.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(getContext(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                Intent intToHome = new Intent(getActivity(), NavigationActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });


        return view;
    }



}


