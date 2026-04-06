package com.example.appml.services;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.example.appml.LoginActivity;
import com.example.appml.MainActivity;
import com.example.appml.R;
import com.example.appml.views.EscalaDetalheActivity;

public class HomeService {

    public static void VoltaPraHome(ImageButton btnHome, final Activity activity) {

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }
        });

    }

    public static void configurarLogout(ImageButton btnLogout, final Activity activity) {

        btnLogout.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(activity)
                    .setTitle("Sair")
                    .setMessage("Deseja realmente sair?")
                    .setPositiveButton("Sim", (dialog, which) -> {

                        // limpa token
                        activity.getSharedPreferences("AppPrefs", Activity.MODE_PRIVATE)
                                .edit()
                                .remove("auth_token")
                                .apply();

                        // vai pro login
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);

                        activity.finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}
