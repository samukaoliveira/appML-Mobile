package com.example.appml;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.appml.services.UpdateChecker;
import com.example.appml.views.EscalasActivity;
import com.example.appml.views.MensalidadeActivity;
import com.example.appml.views.MusicasActivity;
import com.example.appml.worker.WorkScheduler;

public class MainActivity extends AppCompatActivity {

    CardView cardEscalas, cardMusicas, cardMensalidades;

    ImageView btnHome;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateChecker checker = new UpdateChecker(this);
        checker.checkForUpdate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }

        WorkScheduler.schedule(this);

        btnHome = findViewById(R.id.btnHome);

        if (btnHome != null) {
            btnHome.setVisibility(View.GONE);
        }

        cardEscalas = findViewById(R.id.card_escalas);

        cardEscalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EscalasActivity.class);
                startActivity(intent);
            }
        });

        cardMusicas = findViewById(R.id.card_musicas);

        cardMusicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicasActivity.class);
                startActivity(intent);
            }
        });

        cardMensalidades = findViewById(R.id.card_mensalidades);

        cardMensalidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MensalidadeActivity.class);
                startActivity(intent);
            }
        });

    }




}