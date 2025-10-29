package com.example.appml.services;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ExoCachedPlayer
 * Player com cache simples e execução segura na main thread.
 */
@UnstableApi
public class ExoCachedPlayer {

    private static ExoCachedPlayer instance;

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ExoPlayer player;
    private SimpleCache simpleCache;
    private String currentUrl;

    // Tamanho máximo do cache: 100 MB
    private static final long MAX_CACHE_SIZE = 100L * 1024L * 1024L;

    private ExoCachedPlayer(Context context) {
        this.context = context.getApplicationContext();
        initCache();
        initPlayer();
    }

    public static synchronized ExoCachedPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new ExoCachedPlayer(context);
        }
        return instance;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initCache() {
        try {
            File cacheDir = new File(context.getCacheDir(), "media_cache");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE);
            simpleCache = new SimpleCache(cacheDir, evictor);
        } catch (Exception e) {
            e.printStackTrace();
            simpleCache = null;
        }
    }

    private void initPlayer() {
        try {
            DataSource.Factory upstreamFactory = new DefaultDataSource.Factory(context);
            DataSource.Factory cacheDataSourceFactory;

            if (simpleCache != null) {
                cacheDataSourceFactory = new CacheDataSource.Factory()
                        .setCache(simpleCache)
                        .setUpstreamDataSourceFactory(upstreamFactory)
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            } else {
                cacheDataSourceFactory = upstreamFactory;
            }

            player = new ExoPlayer.Builder(context)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheDataSourceFactory))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            player = null;
        }
    }

    /**
     * Toca uma URL (usa cache por baixo). Garantido para executar na main thread.
     */
    public void play(String url) {
        mainHandler.post(() -> {
            try {
                if (player == null) initPlayer();

                // toggle se for mesma URL
                if (player != null && player.isPlaying() && currentUrl != null && currentUrl.equals(url)) {
                    player.pause();
                    return;
                }

                currentUrl = url;
                Uri uri = Uri.parse(url);

                MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(uri)
                        .setMediaMetadata(new MediaMetadata.Builder().setTitle(uri.getLastPathSegment()).build())
                        .build();

                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Pausa a reprodução (executa em main thread).
     */
    public void pause() {
        mainHandler.post(() -> {
            try {
                if (player != null && player.isPlaying()) player.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Para o player (executa em main thread).
     */
    public void stop() {
        mainHandler.post(() -> {
            try {
                if (player != null) player.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retorna se está tocando — comportamento síncrono seguro mesmo fora da main thread.
     */
    public boolean isPlaying() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return player != null && player.isPlaying();
        } else {
            final AtomicBoolean result = new AtomicBoolean(false);
            final CountDownLatch latch = new CountDownLatch(1);
            mainHandler.post(() -> {
                try {
                    result.set(player != null && player.isPlaying());
                } finally {
                    latch.countDown();
                }
            });
            try {
                // espera até 300ms — evita travar indefinidamente
                latch.await(300, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            return result.get();
        }
    }

    /**
     * Libera player e cache (executa na main thread).
     */
    public void release() {
        mainHandler.post(() -> {
            try {
                if (player != null) {
                    player.release();
                    player = null;
                }
                if (simpleCache != null) {
                    try {
                        simpleCache.release();
                    } catch (Exception ignored) {}
                    simpleCache = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Retorna o ExoPlayer (pode ser null). Tenha cuidado — acessar o player deve ser feito na thread principal.
     */
    public ExoPlayer getPlayer() {
        return player;
    }
}
