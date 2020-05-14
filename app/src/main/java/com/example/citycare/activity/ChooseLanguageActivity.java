package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.citycare.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button button_apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_language_setting);

        // One time activity
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            Intent intent = new Intent(this, Welcome2Activity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }


        radioGroup = findViewById(R.id.radioGroup);
        button_apply = findViewById(R.id.apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(radioId);

                Toast.makeText(ChooseLanguageActivity.this, getResources().getString(R.string.selected_language) + " " + radioButton.getText(),
                        Toast.LENGTH_SHORT).show();

                chooseLanguage(radioButton.getText().toString());

                Intent s = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(s);
                finish();
            }
        });
    }


    private void chooseLanguage(String language) {

        if (language.equals("English")) {
            setLocale("en");
        } else if (language.equals("Deutsch")) {
            setLocale("de");
        } else if (language.equals("Indonesia")) {
            setLocale("in");
        } else if (language.equals("Polish")) {
            setLocale("pl");
        }
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("my_language", lang);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("my_language", "en");
        setLocale(language);
    }
}
