package com.example.appml.services;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerNotificationManager;

import com.example.appml.R;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;

@OptIn(markerClass = UnstableApi.class)
public class NotificationHelper {

    public static Notification createNotification(Context context, ExoPlayer player) {

        PlayerNotificationManager manager =
                new PlayerNotificationManager.Builder(
                        context,
                        1,
                        "music_channel"
                ).build();

        manager.setPlayer(player);

        return new NotificationCompat.Builder(context, "music_channel")
                .setContentTitle("Tocando música")
                .setSmallIcon(R.drawable.ic_music)
                .build();
    }
}