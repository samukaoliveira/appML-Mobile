package com.example.appml.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.List;

public class MusicService extends Service {

    private static final String CHANNEL_ID = "music_channel";

    private ExoPlayer player;

    // ✅ BINDER ADICIONADO
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = new ExoPlayer.Builder(this).build();

        createNotificationChannel();
        startForegroundSafe();
    }

    private void startForegroundSafe() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reproduzindo música")
                .setContentText("AppML Player")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();

        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // ✅ ISSO É O QUE A ACTIVITY PRECISA
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void playPlaylist(List<MediaItem> items, int index) {
        player.setMediaItems(items, index, 0);
        player.prepare();
        player.play();
    }

    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void next() {
        player.seekToNext();
    }

    public void previous() {
        player.seekToPrevious();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}