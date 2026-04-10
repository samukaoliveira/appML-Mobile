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
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private static final String CHANNEL_ID = "music_channel";

    private ExoPlayer player;
    private String nomeMusicaAtual;
    private Player.Listener playerListener;
    private List<String> nomesPlaylist = new ArrayList<>();

    // ✅ Lista de callbacks — suporta múltiplos observadores
    private final List<PlayerCallback> callbacks = new ArrayList<>();

    public interface PlayerCallback {
        void onMusicChanged(String nome, int index);
        void onPlayStateChanged(boolean isPlaying);
    }

    public void addCallback(PlayerCallback cb) {
        if (cb != null && !callbacks.contains(cb)) {
            callbacks.add(cb);
        }
    }

    public void removeCallback(PlayerCallback cb) {
        callbacks.remove(cb);
    }

    private void notifyMusicChanged(String nome, int index) {
        for (PlayerCallback cb : new ArrayList<>(callbacks)) {
            cb.onMusicChanged(nome, index);
        }
    }

    private void notifyPlayStateChanged(boolean isPlaying) {
        for (PlayerCallback cb : new ArrayList<>(callbacks)) {
            cb.onPlayStateChanged(isPlaying);
        }
    }

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

        playerListener = new Player.Listener() {

            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                int index = player.getCurrentMediaItemIndex();

                if (nomesPlaylist != null && index >= 0 && index < nomesPlaylist.size()) {
                    nomeMusicaAtual = nomesPlaylist.get(index);
                }

                notifyMusicChanged(nomeMusicaAtual, index);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                notifyPlayStateChanged(isPlaying);
            }
        };

        player.addListener(playerListener);

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

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void playPlaylist(List<MediaItem> items, List<String> nomes, int index) {
        nomesPlaylist.clear();
        nomesPlaylist.addAll(nomes);

        player.setMediaItems(items, index, 0);
        player.prepare();
        player.play();

        if (index >= 0 && index < nomesPlaylist.size()) {
            nomeMusicaAtual = nomesPlaylist.get(index);
        }

        notifyMusicChanged(nomeMusicaAtual, index);
        notifyPlayStateChanged(true);
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

    public void stop() {
        if (player != null) {
            player.stop();
            player.clearMediaItems(); // ✅ limpar itens sinaliza que foi stop, não pause
        }
        nomesPlaylist.clear();
        nomeMusicaAtual = null;
        notifyPlayStateChanged(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.removeListener(playerListener);
            player.release();
        }
    }

    public String getNomeMusicaAtual() {
        return nomeMusicaAtual;
    }

    public void setNomeMusicaAtual(String nome) {
        this.nomeMusicaAtual = nome;
        notifyMusicChanged(nome, player.getCurrentMediaItemIndex());
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public long getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return player != null ? player.getDuration() : 0;
    }

    public void seekTo(long pos) {
        if (player != null) player.seekTo(pos);
    }

    public void seekToDefaultPosition(int index) {
        if (player != null) {
            player.seekToDefaultPosition(index);
        }
    }

    public void toggleRepeatMode() {
        int current = player.getRepeatMode();
        int next;
        if (current == Player.REPEAT_MODE_OFF) {
            next = Player.REPEAT_MODE_ONE;
        } else if (current == Player.REPEAT_MODE_ONE) {
            next = Player.REPEAT_MODE_ALL;
        } else {
            next = Player.REPEAT_MODE_OFF;
        }
        player.setRepeatMode(next);
    }

    public int getRepeatMode() {
        return player != null ? player.getRepeatMode() : Player.REPEAT_MODE_OFF;
    }
}