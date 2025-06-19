package com.example.appml.worker;// EscalaPollWorker.java
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaNotificacao;
import com.example.appml.models.escala.EscalaSimples;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;
import com.example.appml.views.EscalaDetalheActivity;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class EscalaPollWorker extends Worker {

    public EscalaPollWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    private static final String CHANNEL_ID = "escala_updates";

    private void criarCanalSeNecessario(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Atualizações de Escalas",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        ApiService api = RetrofitInstance
                .getRetrofitInstance(getApplicationContext())
                .create(ApiService.class);

        try {
            Response<List<EscalaNotificacao>> resp = api.getAtualizadas().execute();
            List<EscalaNotificacao> escalas = resp.body();      // pode vir null
            if (escalas == null || escalas.isEmpty()) {
                return Result.success();
            }

            Log.d("Worker", "Escalas recebidas: " + resp.body().get(0));


            // 1. Garante que o canal exista
            criarCanalSeNecessario(getApplicationContext());

            // 2. Envia uma notificação para cada escala
            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
            int baseId = (int) System.currentTimeMillis();   // base p/ gerar ids únicos

            for (int i = 0; i < escalas.size(); i++) {
                EscalaNotificacao escala = escalas.get(i);
                int notId = baseId + i;      // evitar colisão com outras execuções

                // Intent que abre a tela de detalhe
                Intent intent = new Intent(getApplicationContext(), EscalaDetalheActivity.class);
                intent.putExtra("escala_id", escala.getId());  // ajuste se pá

                PendingIntent pending = PendingIntent.getActivity(
                        getApplicationContext(),
                        notId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder nb =
                        new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.notificacao)  // crie esse ícone em mipmap
                                .setContentTitle("Escala atualizada")
                                .setContentText("Toque para ver a escala do dia " + escala.getData())
                                .setContentIntent(pending)
                                .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Permissão não concedida → não mostra notificação
                        return Result.success();
                    }
                }

                nm.notify(notId, nb.build());
            }

            return Result.success();
        } catch (Exception fatal) {
            return Result.failure();
        }
    }
}
