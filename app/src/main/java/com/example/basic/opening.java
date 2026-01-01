package com.example.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class opening extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
    }
    public void play(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void inst (View view){
        Intent i = new Intent(this, Inst.class);
        startActivity(i);
    }
}