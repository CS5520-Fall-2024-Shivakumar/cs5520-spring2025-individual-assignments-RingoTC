package com.example.hanliao_cs5520_android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutMeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        
        TextView aboutMeText = findViewById(R.id.about_me_text);
        aboutMeText.setText("Name: Han Liao\nEmail: liao.han1@northeastern.edu");
    }
} 