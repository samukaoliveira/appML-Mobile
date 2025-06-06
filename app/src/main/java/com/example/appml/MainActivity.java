package com.example.appml;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appml.views.EscalasActivity;

public class MainActivity extends AppCompatActivity {

    CardView cardEscalas;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardEscalas = findViewById(R.id.card_escalas);

        cardEscalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EscalasActivity.class);
                startActivity(intent);
            }
        });

    }




}