package com.example.appml.views;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.musica.Musica;
import com.example.appml.services.ApiService;
import com.example.appml.services.ExoCachedPlayer;
import com.example.appml.services.HomeService;
import com.example.appml.services.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@UnstableApi
public class EscalaDetalheActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaPlayListener {

    private TextView tvNome, tvData, tvHora;
    private TextView tvBaterista, tvBaixista, tvTecladista;
    private TextView tvVocalistas, tvViolonista, tvGuitarrista, tvSaxofonista;
    private TextView tvObservacoes;
    private RecyclerView rvMusicas;
    private TextView tvMusicasVazia;
    private MusicaAdapter musicaAdapter;

    private ExoCachedPlayer exoPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private TextView tvCurrentTime, tvTotalTime;
    private ProgressBar progressLoading;
    private TextView tvPercentual;

    private Player.Listener playerListener;

    ImageButton btnHome;

    private Runnable updateSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null && exoPlayer.getPlayer() != null && exoPlayer.isPlaying()) {
                long pos = exoPlayer.getPlayer().getCurrentPosition();
                long dur = exoPlayer.getPlayer().getDuration();
                if (dur > 0) {
                    int progress = (int) ((pos * 100) / dur);
                    seekBar.setProgress(progress);
                    tvCurrentTime.setText(formatTime((int) pos));
                }
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        inicializarComponentes();
        configurarRecyclerView();
        configurarHomeButton();
        configurarExoPlayer();

        int escalaId = getIntent().getIntExtra("escala_id", -1);
        if (escalaId == -1) {
            Toast.makeText(this, "Escala inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        prepararParaNovaEscala();
        carregarDetalheEscala(escalaId);
    }

    private void inicializarComponentes() {
        tvNome = findViewById(R.id.tvNome);
        tvData = findViewById(R.id.tvData);
        tvHora = findViewById(R.id.tvHora);

        tvBaterista = findViewById(R.id.tvBaterista);
        tvBaixista = findViewById(R.id.tvBaixista);
        tvTecladista = findViewById(R.id.tvTecladista);

        tvVocalistas = findViewById(R.id.tvVocalistas);
        tvViolonista = findViewById(R.id.tvViolonista);
        tvGuitarrista = findViewById(R.id.tvGuitarrista);
        tvSaxofonista = findViewById(R.id.tvSaxofonista);

        tvObservacoes = findViewById(R.id.tvObservacoes);

        rvMusicas = findViewById(R.id.rvMusicas);
        tvMusicasVazia = findViewById(R.id.tvMusicasVazia);

        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvPercentual = findViewById(R.id.tvPercentual);

        progressLoading = findViewById(R.id.progressLoading);
        progressLoading.setVisibility(View.GONE);
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

    private void configurarExoPlayer() {
        exoPlayer = ExoCachedPlayer.getInstance(this);

        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        progressLoading.setVisibility(View.VISIBLE);
                        tvPercentual.setText("Carregando...");
                        break;
                    case Player.STATE_READY:
                        progressLoading.setVisibility(View.GONE);
                        if (exoPlayer.getPlayer() != null) {
                            long dur = exoPlayer.getPlayer().getDuration();
                            tvTotalTime.setText(formatTime((int) dur));
                        }
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

        if (exoPlayer.getPlayer() != null) {
            exoPlayer.getPlayer().addListener(playerListener);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && exoPlayer.getPlayer() != null) {
                    long dur = exoPlayer.getPlayer().getDuration();
                    long newPos = (dur * progress) / 100;
                    exoPlayer.getPlayer().seekTo(newPos);
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
                    tvVocalistas.setText("Vocalistas: " + (vocalistas != null && !vocalistas.isEmpty() ? TextUtils.join(", ", vocalistas) : "---"));

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));
                    tvSaxofonista.setText("Saxofonista: " + displayValor(escala.getSaxofonista()));

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null && !escala.getObs().equals("-") ? escala.getObs() : "-"));

                    List<Musica> musicas = escala.getMusicas();
                    if (musicas == null || musicas.isEmpty()) {
                        tvMusicasVazia.setVisibility(View.VISIBLE);
                        rvMusicas.setVisibility(View.GONE);
                    } else {
                        tvMusicasVazia.setVisibility(View.GONE);
                        rvMusicas.setVisibility(View.VISIBLE);
                        musicaAdapter.setMusicas(musicas);
                    }

                } else {
                    Toast.makeText(EscalaDetalheActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EscalaDetalhada> call, Throwable t) {
                Toast.makeText(EscalaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String displayValor(String valor) {
        return (valor == null || valor.equals("-") || valor.trim().isEmpty()) ? "---" : valor;
    }

    @Override
    public void onPlayClicked(Musica musica) {
        progressLoading.setVisibility(View.VISIBLE);
        tvPercentual.setText("Carregando...");
        exoPlayer.play(musica.getArquivoAudio());
    }

    @Override
    public void onPauseClicked() {
        exoPlayer.pause();
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekRunnable);
        if (exoPlayer != null && exoPlayer.getPlayer() != null) {
            exoPlayer.getPlayer().removeListener(playerListener);
            // opcional: não liberar cache para manter singleton ativo
        }
    }

    private void prepararParaNovaEscala() {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }

        seekBar.setProgress(0);
        tvCurrentTime.setText("00:00");
        tvTotalTime.setText("00:00");
        tvPercentual.setText("");
        progressLoading.setVisibility(View.GONE);
    }
}
