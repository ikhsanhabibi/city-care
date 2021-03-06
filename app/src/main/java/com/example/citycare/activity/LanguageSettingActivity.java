package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.citycare.R;
import com.example.citycare.activity.FAQ.HowToRetrieveComplaintActivity;

import java.util.Locale;

public class LanguageSettingActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton, enRadioButton, inRadioButton, jvRadioButton, suRadioButton;
    Button button_apply;
    private ImageView left_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_language_setting);

        left_btn = findViewById(R.id.left_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(LanguageSettingActivity.this, MoreActivity.class);
                startActivity(s);
                finish();
            }
        });


        radioGroup = findViewById(R.id.radioGroup);
        button_apply = findViewById(R.id.apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(radioId);

                Toast.makeText(LanguageSettingActivity.this, getResources().getString(R.string.selected_language) + " " + radioButton.getText(),
                        Toast.LENGTH_SHORT).show();

                chooseLanguage(radioButton.getText().toString());

                Intent s = new Intent(getApplicationContext(), MoreActivity.class);
                startActivity(s);
                finish();
            }
        });
    }

    void chooseLanguage(String language) {

        if (language.equals("English")) {
            setLocale("en");
        } else if (language.equals("Indonesia")) {
            setLocale("in");
        } else if (language.equals("Javanese")) {
            setLocale("jv");
        } else if (language.equals("Sundanese")) {
            setLocale("su");
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
