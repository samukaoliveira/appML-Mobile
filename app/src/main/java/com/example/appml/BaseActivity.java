package com.example.appml;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;

import com.example.appml.services.MusicService;

public class BaseActivity extends AppCompatActivity {

    protected ImageButton btnHome;
    protected ImageButton btnLogout;

    protected View layoutFooterNormal;
    protected View layoutPlayerFooter;
    protected TextView tvFooterMusica;
    protected ImageButton btnPlayPauseFooter;

    protected MusicService musicService;
    protected boolean isBound = false;

    // Adicionar campos no BaseActivity
    protected ImageButton btnNextFooter;
    protected ImageButton btnPrevFooter;
    protected ImageButton btnStopFooter;

    private SeekBar seekBarFooter;
    private final Handler seekHandler = new Handler();

    private final MusicService.PlayerCallback playerCallback = new MusicService.PlayerCallback() {

        @Override
        public void onMusicChanged(String nome, int index) {
            runOnUiThread(() -> {
                if (tvFooterMusica != null && nome != null) {
                    tvFooterMusica.setText(nome);
                }
                // Sempre que mudar de música, garantir footer visível
                mostrarFooterPlayer();
            });
        }

        @Override
        public void onPlayStateChanged(boolean isPlaying) {
            runOnUiThread(() -> {

                if (btnPlayPauseFooter != null) {
                    btnPlayPauseFooter.setImageResource(
                            isPlaying
                                    ? android.R.drawable.ic_media_pause
                                    : android.R.drawable.ic_media_play
                    );
                }

                if (!isPlaying) {
                    // ✅ Só esconde o footer se foi um stop() de verdade
                    // (stop() limpa os itens da playlist)
                    if (musicService != null) {
                        Player player = musicService.getPlayer();
                        boolean semItens = player == null || player.getMediaItemCount() == 0;
                        if (semItens) {
                            esconderFooterPlayer();
                        }
                        // Se ainda tem itens na fila, é pause — footer permanece
                    }
                } else {
                    mostrarFooterPlayer();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aplicarStatusBarTransparente();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupHeaderButtons();
        setupFooterViews();

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        seekHandler.removeCallbacks(updateSeekFooterRunnable);

        if (musicService != null) {
            musicService.removeCallback(playerCallback);
        }

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private void aplicarStatusBarTransparente() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void setupHeaderButtons() {
        btnHome = findViewById(R.id.btnHome);
        btnLogout = findViewById(R.id.btnLogout);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    private void setupFooterViews() {
        layoutFooterNormal = findViewById(R.id.layoutFooterNormal);
        layoutPlayerFooter = findViewById(R.id.layoutPlayerFooter);
        tvFooterMusica = findViewById(R.id.tvFooterMusica);
        btnPlayPauseFooter = findViewById(R.id.btnPlayPauseFooter);

        // ✅ Adicionar estes:
        btnNextFooter = findViewById(R.id.btnNextFooter);
        btnPrevFooter = findViewById(R.id.btnPrevFooter);
        btnStopFooter = findViewById(R.id.btnStopFooter);

        if (btnPlayPauseFooter != null) {
            btnPlayPauseFooter.setOnClickListener(v -> {
                if (musicService != null) {
                    if (musicService.isPlaying()) musicService.pause();
                    else musicService.play();
                }
            });
        }

        // ✅ Listeners globais — funcionam em qualquer Activity filha
        if (btnNextFooter != null) {
            btnNextFooter.setOnClickListener(v -> {
                if (musicService != null) musicService.next();
            });
        }

        if (btnPrevFooter != null) {
            btnPrevFooter.setOnClickListener(v -> {
                if (musicService != null) musicService.previous();
            });
        }

        if (btnStopFooter != null) {
            btnStopFooter.setOnClickListener(v -> {
                if (musicService != null) musicService.stop();
            });
        }

        seekBarFooter = findViewById(R.id.seekBarFooter);
        if (seekBarFooter != null) {
            seekBarFooter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && musicService != null) {
                        long dur = musicService.getDuration();
                        if (dur > 0) musicService.seekTo((dur * progress) / 100);
                    }
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta")
                .setMessage("Tem certeza que deseja sair?")
                .setCancelable(true)
                .setPositiveButton("Sair", (dialog, which) -> {
                    getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .edit()
                            .remove("auth_token")
                            .apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;

            musicService.addCallback(playerCallback);
            runOnUiThread(() -> atualizarFooterGlobal());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            musicService = null;
        }
    };

    protected void mostrarFooterPlayer() {
        if (layoutFooterNormal != null) layoutFooterNormal.setVisibility(View.GONE);
        if (layoutPlayerFooter != null) layoutPlayerFooter.setVisibility(View.VISIBLE);
    }

    protected void esconderFooterPlayer() {
        if (layoutFooterNormal != null) layoutFooterNormal.setVisibility(View.VISIBLE);
        if (layoutPlayerFooter != null) layoutPlayerFooter.setVisibility(View.GONE);
    }

    // Sincroniza o footer ao conectar ou ao voltar para qualquer tela
    protected void atualizarFooterGlobal() {

        if (layoutFooterNormal == null || layoutPlayerFooter == null
                || tvFooterMusica == null || btnPlayPauseFooter == null) {
            return;
        }

        if (musicService == null || musicService.getPlayer() == null) return;

        Player player = musicService.getPlayer();

        if (player.getMediaItemCount() > 0) {
            mostrarFooterPlayer();

            String nome = musicService.getNomeMusicaAtual();
            if (nome != null) tvFooterMusica.setText(nome);

            btnPlayPauseFooter.setImageResource(
                    player.isPlaying()
                            ? android.R.drawable.ic_media_pause
                            : android.R.drawable.ic_media_play
            );

            if (player.isPlaying()) {
                seekHandler.removeCallbacks(updateSeekFooterRunnable);
                seekHandler.post(updateSeekFooterRunnable);
            }
        } else {
            esconderFooterPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null) {
            atualizarFooterGlobal();
        }
    }

    private final Runnable updateSeekFooterRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                long pos = musicService.getCurrentPosition();
                long dur = musicService.getDuration();
                if (dur > 0 && seekBarFooter != null) {
                    int progress = (int) (pos * 100 / dur);
                    seekBarFooter.setProgress(progress);
                }
            }
            seekHandler.postDelayed(this, 500);
        }
    };
}