package com.example.appml.views;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.musica.Musica;
import com.example.appml.services.ApiService;
import com.example.appml.services.HomeService;
import com.example.appml.services.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.media.MediaPlayer;

public class EscalaDetalheActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaPlayListener {

    private TextView tvNome, tvData, tvHora;
    private TextView tvBaterista, tvBaixista, tvTecladista;
    private TextView tvVocalistas, tvViolonista, tvGuitarrista, tvSaxofonista;
    private TextView tvObservacoes;
    private RecyclerView rvMusicas;
    private TextView tvMusicasVazia;
    private MusicaAdapter musicaAdapter;

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private TextView tvCurrentTime, tvTotalTime;
    private ProgressBar progressLoading;

    ImageButton btnHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        // TextViews
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

        // RecyclerView
        rvMusicas = findViewById(R.id.rvMusicas);
        tvMusicasVazia = findViewById(R.id.tvMusicasVazia);
        musicaAdapter = new MusicaAdapter(new ArrayList<>(), this);
        rvMusicas.setLayoutManager(new LinearLayoutManager(this));
        rvMusicas.setAdapter(musicaAdapter);

        // SeekBar e contadores
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    int newPosition = (mediaPlayer.getDuration() * progress) / 100;
                    mediaPlayer.seekTo(newPosition);
                    tvCurrentTime.setText(formatTime(newPosition));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // ProgressBar de carregamento
        progressLoading = findViewById(R.id.progressLoading);
        progressLoading.setVisibility(View.GONE);

        // Botão Home
        btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);

        // Carrega escala
        int escalaId = getIntent().getIntExtra("escala_id", -1);
        if (escalaId == -1) {
            Toast.makeText(this, "Escala inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        carregarDetalheEscala(escalaId);
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
                    if (vocalistas != null && !vocalistas.isEmpty()) {
                        tvVocalistas.setText("Vocalistas: " + TextUtils.join(", ", vocalistas));
                    } else {
                        tvVocalistas.setText("Vocalistas: ---");
                    }

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));
                    tvSaxofonista.setText("Saxofonista: " + displayValor(escala.getSaxofonista()));

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null && !escala.getObs().equals("-") ? escala.getObs() : "-"));

                    // Atualiza lista de músicas
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

    // -------------------------------
    // MediaPlayer / SeekBar
    // -------------------------------

    @Override
    public void onPlayClicked(Musica musica) {
        try {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            else mediaPlayer.reset();

            progressLoading.setVisibility(View.VISIBLE);

            mediaPlayer.setDataSource(musica.getArquivoAudio());

            // Prepare assíncrono para UI não travar
            mediaPlayer.setOnPreparedListener(mp -> {
                progressLoading.setVisibility(View.GONE);
                mediaPlayer.start();
                tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
                startSeekBarUpdate();
            });

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                tvCurrentTime.setText("00:00");
                handler.removeCallbacks(updateSeekBar);
            });

        } catch (Exception e) {
            e.printStackTrace();
            progressLoading.setVisibility(View.GONE);
            Toast.makeText(this, "Erro ao reproduzir música", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPauseClicked() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    private void startSeekBarUpdate() {
        if (updateSeekBar != null) handler.removeCallbacks(updateSeekBar);

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int position = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    if (duration > 0) {
                        int progress = (int) (((float) position / duration) * 100);
                        seekBar.setProgress(progress);
                        tvCurrentTime.setText(formatTime(position));
                    }

                    if (mediaPlayer.isPlaying()) {
                        handler.postDelayed(this, 500);
                    }
                }
            }
        };
        handler.post(updateSeekBar);
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
