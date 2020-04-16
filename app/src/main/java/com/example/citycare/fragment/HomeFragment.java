package com.example.citycare.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.citycare.R;
import com.example.citycare.activity.ComplaintFormActivity;
import com.example.citycare.activity.SignupSigninActivity;
import com.example.citycare.activity.SuggestionFormActivity;
import com.example.citycare.activity.Welcome2Activity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView complaint = view.findViewById(R.id.complaint);
        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ComplaintFormActivity.class);
                startActivity(intent);
            }

        });

        ImageView suggestion = view.findViewById(R.id.suggestion);
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SuggestionFormActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
