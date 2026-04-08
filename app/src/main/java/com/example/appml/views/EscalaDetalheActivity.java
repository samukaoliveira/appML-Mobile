package com.example.appml.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.BaseActivity;
import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.musica.Musica;
import com.example.appml.services.ApiService;
import com.example.appml.services.HomeService;
import com.example.appml.services.MusicService;
import com.example.appml.services.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@UnstableApi
public class EscalaDetalheActivity extends BaseActivity implements MusicaAdapter.OnMusicaPlayListener {

    private TextView tvNome, tvData, tvHora;
    private TextView tvBaterista, tvBaixista, tvTecladista;
    private TextView tvVocalista1, tvVocalista2, tvVocalista3, tvVocalista4;
    private TextView tvViolonista, tvGuitarrista, tvSaxofonista;
    private TextView tvObservacoes;

    private RecyclerView rvMusicas;
    private TextView tvMusicasVazia;
    private MusicaAdapter musicaAdapter;

    private SeekBar seekBar;
    private Handler handler = new Handler();
    private TextView tvCurrentTime, tvTotalTime;
    private ProgressBar progressLoading;

    private ImageButton btnHome;

    private List<Musica> listaMusicas = new ArrayList<>();

    // 🎧 SERVICE
    private MusicService musicService;
    private boolean isBound = false;

    private boolean playerReady = false;

    private Player.Listener playerListener;

    // 🔁 Atualiza barra
    private Runnable updateSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                Player player = musicService.getPlayer();

                if (player != null && player.isPlaying()) {
                    long pos = player.getCurrentPosition();
                    long dur = player.getDuration();

                    if (dur > 0) {
                        int progress = (int) ((pos * 100) / dur);
                        seekBar.setProgress(progress);
                        tvCurrentTime.setText(formatTime((int) pos));
                    }
                }
            }
            handler.postDelayed(this, 500);
        }
    };

    // 🔌 conexão
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            playerReady = true;

            configurarPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        inicializarComponentes();
        configurarRecyclerView();
        configurarHomeButton();

        int escalaId = getIntent().getIntExtra("escala_id", -1);
        if (escalaId == -1) {
            Toast.makeText(this, "Escala inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalheEscala(escalaId);
    }

    private void inicializarComponentes() {
        tvNome = findViewById(R.id.tvNome);
        tvData = findViewById(R.id.tvData);
        tvHora = findViewById(R.id.tvHora);

        tvBaterista = findViewById(R.id.tvBaterista);
        tvBaixista = findViewById(R.id.tvBaixista);
        tvTecladista = findViewById(R.id.tvTecladista);

        tvVocalista1 = findViewById(R.id.tvVocalista1);
        tvVocalista2 = findViewById(R.id.tvVocalista2);
        tvVocalista3 = findViewById(R.id.tvVocalista3);
        tvVocalista4 = findViewById(R.id.tvVocalista4);

        tvViolonista = findViewById(R.id.tvViolonista);
        tvGuitarrista = findViewById(R.id.tvGuitarrista);
        tvSaxofonista = findViewById(R.id.tvSaxofonista);

        tvObservacoes = findViewById(R.id.tvObservacoes);

        rvMusicas = findViewById(R.id.rvMusicas);
        tvMusicasVazia = findViewById(R.id.tvMusicasVazia);

        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);

        progressLoading = findViewById(R.id.progressLoading);
    }

    private void configurarRecyclerView() {
        musicaAdapter = new MusicaAdapter(new ArrayList<>(), this);
        rvMusicas.setLayoutManager(new LinearLayoutManager(this));
        rvMusicas.setAdapter(musicaAdapter);
    }

    private void configurarHomeButton() {
        btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);
    }

    private void configurarPlayer() {
        playerListener = new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        progressLoading.setVisibility(View.VISIBLE);
                        break;

                    case Player.STATE_READY:
                        progressLoading.setVisibility(View.GONE);
                        long dur = musicService.getPlayer().getDuration();
                        tvTotalTime.setText(formatTime((int) dur));
                        break;

                    case Player.STATE_ENDED:
                        seekBar.setProgress(0);
                        tvCurrentTime.setText("00:00");
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    handler.post(updateSeekRunnable);
                } else {
                    handler.removeCallbacks(updateSeekRunnable);
                }
            }
        };

        musicService.getPlayer().addListener(playerListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long dur = musicService.getPlayer().getDuration();
                    long newPos = (dur * progress) / 100;
                    musicService.getPlayer().seekTo(newPos);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void carregarDetalheEscala(int id) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ApiService.class);

        apiService.getDetalheEscala(id).enqueue(new Callback<EscalaDetalhada>() {
            @Override
            public void onResponse(Call<EscalaDetalhada> call, Response<EscalaDetalhada> response) {
                if (response.isSuccessful() && response.body() != null) {

                    EscalaDetalhada escala = response.body();

                    tvNome.setText(escala.getNome());
                    tvData.setText(escala.getData());
                    tvHora.setText(escala.getHora());

                    tvBaterista.setText("Baterista: " + displayValor(escala.getBaterista()));
                    tvBaixista.setText("Baixista: " + displayValor(escala.getBaixista()));
                    tvTecladista.setText("Tecladista: " + displayValor(escala.getTecladista()));

                    List<String> vocalistas = escala.getVocalistas();

                    tvVocalista1.setText(getVocal(vocalistas, 0));
                    tvVocalista2.setText(getVocal(vocalistas, 1));
                    tvVocalista3.setText(getVocal(vocalistas, 2));
                    tvVocalista4.setText(getVocal(vocalistas, 3));

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));
                    tvSaxofonista.setText("Saxofonista: " + displayValor(escala.getSaxofonista()));

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null ? escala.getObs() : "-"));

                    listaMusicas = escala.getMusicas();

                    if (listaMusicas == null || listaMusicas.isEmpty()) {
                        tvMusicasVazia.setVisibility(View.VISIBLE);
                        rvMusicas.setVisibility(View.GONE);
                    } else {
                        tvMusicasVazia.setVisibility(View.GONE);
                        rvMusicas.setVisibility(View.VISIBLE);
                        musicaAdapter.setMusicas(listaMusicas);
                    }

                } else {
                    Toast.makeText(EscalaDetalheActivity.this, "Erro ao carregar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EscalaDetalhada> call, Throwable t) {
                Toast.makeText(EscalaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getVocal(List<String> lista, int index) {
        if (lista != null && lista.size() > index) {
            String nome = lista.get(index);
            if (nome != null && !nome.trim().isEmpty()) return nome;
        }
        return "---";
    }

    private String displayValor(String valor) {
        return (valor == null || valor.equals("-") || valor.trim().isEmpty()) ? "---" : valor;
    }

    @Override
    public void onPlayClicked(Musica musica) {
        if (!playerReady) {
            Toast.makeText(this, "Aguarde o player iniciar...", Toast.LENGTH_SHORT).show();
            return;
        }

        List<MediaItem> items = new ArrayList<>();

        for (Musica m : listaMusicas) {
            items.add(MediaItem.fromUri(m.getArquivoAudio()));
        }

        int index = listaMusicas.indexOf(musica);

        musicService.playPlaylist(items, index);
    }

    @Override
    public void onPauseClicked() {
        if (musicService != null) {
            musicService.pause();
        }
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}