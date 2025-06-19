package com.example.appml.worker;// WorkScheduler.java (ou direto no Application)
import android.content.Context;
import androidx.work.*;

import java.util.concurrent.TimeUnit;

public final class WorkScheduler {

    public static void schedule(Context ctx) {

        PeriodicWorkRequest poll =
                new PeriodicWorkRequest.Builder(EscalaPollWorker.class,
                        15, TimeUnit.MINUTES)          // intervalo mínimo real
                        .setConstraints(
                                new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build())
                        .addTag("escala_poll")
                        .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "escala_poll",
                ExistingPeriodicWorkPolicy.KEEP,
                poll);
    }
}
