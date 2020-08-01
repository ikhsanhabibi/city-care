package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.citycare.R;
import com.example.citycare.model.Form;

public class SuggestionSentActivity extends AppCompatActivity {

    Button my_suggestions;
    TextView suggestion_number;
    Form suggestion = new Form();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SuggestionSentActivity.this, SuggestionFormActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_sent);

        suggestion.setId(getIntent().getStringExtra("id"));
        suggestion_number = findViewById(R.id.suggestion_number);
        suggestion_number.setText(suggestion.getId());

        // setOnClickListener
        my_suggestions = findViewById(R.id.my_suggestions);
        my_suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), ReportsActivity.class);
                startActivity(s);
                finish();
            }
        });
    }
}
