package com.example.appml.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.List;

public class MusicService extends Service {

    private ExoPlayer player;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = new ExoPlayer.Builder(this).build();

        // ❌ REMOVIDO foreground
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

    public void stopMusic() {
        player.stop();
    }

    public void next() {
        player.seekToNext();
    }

    public void previous() {
        player.seekToPrevious();
    }

    public void setRepeat(int mode) {
        player.setRepeatMode(mode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}