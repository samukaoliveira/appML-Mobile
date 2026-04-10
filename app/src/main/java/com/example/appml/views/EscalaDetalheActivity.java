package com.example.appml.views;

import android.os.Bundle;
import android.os.Handler;
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
    private final Handler handler = new Handler();
    private TextView tvCurrentTime, tvTotalTime;
    private ProgressBar progressLoading;

    private ImageButton btnStopFooter;
    private ImageButton btnPlayPause, btnNext, btnPrev;
    private SeekBar seekBarFooter;
    private ImageButton btnNextFooter, btnPrevFooter;

    private List<Musica> listaMusicas = new ArrayList<>();

    // musicService e isBound herdados do BaseActivity

    private boolean playerReady = false;
    private int currentIndex = -1;
    private boolean playlistCarregada = false;

    // ✅ Atualiza seekbar e highlight da lista — exclusivo desta Activity
    private final MusicService.PlayerCallback localCallback = new MusicService.PlayerCallback() {

        @Override
        public void onMusicChanged(String nome, int index) {
            runOnUiThread(() -> {
                currentIndex = index;
                musicaAdapter.setCurrentPlayingIndex(index);
                // tvFooterMusica e visibilidade do footer ficam com BaseActivity
            });
        }

        @Override
        public void onPlayStateChanged(boolean isPlaying) {
            runOnUiThread(() -> {
                if (btnPlayPause != null) {
                    btnPlayPause.setImageResource(
                            isPlaying
                                    ? android.R.drawable.ic_media_pause
                                    : android.R.drawable.ic_media_play
                    );
                }

                if (isPlaying) {
                    handler.post(updateSeekRunnable);
                } else {
                    handler.removeCallbacks(updateSeekRunnable);

                    // Se foi stop(), resetar estado local
                    if (musicService != null) {
                        Player player = musicService.getPlayer();
                        if (player == null || player.getMediaItemCount() == 0) {
                            playlistCarregada = false;
                            currentIndex = -1;
                            musicaAdapter.setCurrentPlayingIndex(-1);
                            seekBar.setProgress(0);
                            seekBarFooter.setProgress(0);
                            tvCurrentTime.setText("00:00");
                            tvTotalTime.setText("00:00");
                        }
                    }
                }
            });
        }
    };

    private final Runnable updateSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null && playerReady) {
                Player player = musicService.getPlayer();
                if (player != null) {
                    long pos = player.getCurrentPosition();
                    long dur = player.getDuration();
                    if (dur > 0) {
                        tvCurrentTime.setText(formatTime((int) pos));
                        int progress = (int) (pos * 100 / dur);
                        seekBar.setProgress(progress);
                        seekBarFooter.setProgress(progress);
                    }
                }
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onStop() {
        handler.removeCallbacks(updateSeekRunnable);

        if (musicService != null) {
            musicService.removeCallback(localCallback);
        }

        super.onStop(); // BaseActivity remove o playerCallback global e faz unbind
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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

        tvViolonista = findViewById(R.id.tvViolonista);
        tvGuitarrista = findViewById(R.id.tvGuitarrista);
        tvSaxofonista = findViewById(R.id.tvSaxofonista);

        tvVocalista1 = findViewById(R.id.tvVocalista1);
        tvVocalista2 = findViewById(R.id.tvVocalista2);
        tvVocalista3 = findViewById(R.id.tvVocalista3);
        tvVocalista4 = findViewById(R.id.tvVocalista4);

        tvObservacoes = findViewById(R.id.tvObservacoes);

        rvMusicas = findViewById(R.id.rvMusicas);
        tvMusicasVazia = findViewById(R.id.tvMusicasVazia);

        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        progressLoading = findViewById(R.id.progressLoading);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        // layoutFooterNormal, layoutPlayerFooter, tvFooterMusica, btnPlayPauseFooter
        // são inicializados pelo BaseActivity.setupFooterViews()

        seekBarFooter = findViewById(R.id.seekBarFooter);
        btnNextFooter = findViewById(R.id.btnNextFooter);
        btnPrevFooter = findViewById(R.id.btnPrevFooter);
        btnStopFooter = findViewById(R.id.btnStopFooter);
    }

    private void configurarRecyclerView() {
        musicaAdapter = new MusicaAdapter(new ArrayList<>(), this);
        rvMusicas.setLayoutManager(new LinearLayoutManager(this));
        rvMusicas.setAdapter(musicaAdapter);
    }

    private void configurarHomeButton() {
        ImageButton btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);
    }

    /**
     * ✅ Chamado pelo BaseActivity após o bind — registra callback local e configura controles
     */
    @Override
    protected void atualizarFooterGlobal() {
        super.atualizarFooterGlobal();

        if (musicService != null) {
            musicService.addCallback(localCallback);
            configurarControlesPlayer();
            restaurarEstadoPlayer();
        }
    }

    private void restaurarEstadoPlayer() {
        if (musicService == null || musicService.getPlayer() == null) return;

        Player player = musicService.getPlayer();

        if (player.getMediaItemCount() > 0) {
            playerReady = true;
            playlistCarregada = true;

            int index = player.getCurrentMediaItemIndex();
            if (index >= 0 && index < listaMusicas.size()) {
                currentIndex = index;
                musicaAdapter.setCurrentPlayingIndex(index);
            }

            long dur = player.getDuration();
            if (dur > 0) tvTotalTime.setText(formatTime((int) dur));

            if (player.isPlaying()) {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                handler.post(updateSeekRunnable);
            }
        }
    }

    private void configurarControlesPlayer() {
        playerReady = true;

        Player player = musicService.getPlayer();
        if (player == null) return;

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    progressLoading.setVisibility(View.VISIBLE);
                } else if (state == Player.STATE_READY) {
                    progressLoading.setVisibility(View.GONE);
                    long dur = player.getDuration();
                    if (dur > 0) tvTotalTime.setText(formatTime((int) dur));
                }
            }
        });

        btnPlayPause.setOnClickListener(v -> {
            if (!playlistCarregada && !listaMusicas.isEmpty()) {
                carregarETocarPlaylist(0);
                return;
            }
            if (player.isPlaying()) {
                musicService.pause();
            } else {
                musicService.play();
            }
        });

        btnNext.setOnClickListener(v -> musicService.next());
        btnPrev.setOnClickListener(v -> musicService.previous());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long dur = player.getDuration();
                    if (dur > 0) player.seekTo((dur * progress) / 100);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    private void carregarETocarPlaylist(int startIndex) {
        List<MediaItem> items = new ArrayList<>();
        List<String> nomes = new ArrayList<>();

        for (Musica m : listaMusicas) {
            if (m.getArquivoAudio() != null && !m.getArquivoAudio().isEmpty()) {
                items.add(MediaItem.fromUri(m.getArquivoAudio()));
                nomes.add(m.getNome());
            }
        }

        if (!items.isEmpty()) {
            musicService.playPlaylist(items, nomes, startIndex);
            playlistCarregada = true;
            currentIndex = startIndex;
            musicaAdapter.setCurrentPlayingIndex(startIndex);
        }
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

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));
                    tvSaxofonista.setText("Saxofonista: " + displayValor(escala.getSaxofonista()));

                    List<String> vocalistas = escala.getVocalistas();
                    tvVocalista1.setText(getVocal(vocalistas, 0));
                    tvVocalista2.setText(getVocal(vocalistas, 1));
                    tvVocalista3.setText(getVocal(vocalistas, 2));
                    tvVocalista4.setText(getVocal(vocalistas, 3));

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null ? escala.getObs() : "-"));

                    listaMusicas = escala.getMusicas();

                    if (listaMusicas == null || listaMusicas.isEmpty()) {
                        tvMusicasVazia.setVisibility(View.VISIBLE);
                        rvMusicas.setVisibility(View.GONE);
                    } else {
                        tvMusicasVazia.setVisibility(View.GONE);
                        rvMusicas.setVisibility(View.VISIBLE);
                        musicaAdapter.setMusicas(listaMusicas);

                        if (!existeAlgumAudio()) {
                            desativarPlayer();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EscalaDetalhada> call, Throwable t) {
                Toast.makeText(EscalaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean existeAlgumAudio() {
        if (listaMusicas == null) return false;
        for (Musica m : listaMusicas) {
            if (m.getArquivoAudio() != null && !m.getArquivoAudio().isEmpty()) return true;
        }
        return false;
    }

    private void desativarPlayer() {
        btnPlayPause.setEnabled(false);
        btnNext.setEnabled(false);
        btnPrev.setEnabled(false);
        seekBar.setEnabled(false);
        btnPlayPause.setAlpha(0.3f);
        btnNext.setAlpha(0.3f);
        btnPrev.setAlpha(0.3f);
        seekBar.setAlpha(0.3f);
    }

    @Override
    public void onPlayClicked(Musica musica, int position) {
        if (!playerReady || musicService == null) return;

        if (musica.getArquivoAudio() == null || musica.getArquivoAudio().isEmpty()) {
            Toast.makeText(this, "Essa música não possui áudio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!playlistCarregada) {
            carregarETocarPlaylist(position);
        } else {
            musicService.seekToDefaultPosition(position);
            musicService.play();
        }

        currentIndex = position;
        musicaAdapter.setCurrentPlayingIndex(position);
    }

    private String getVocal(List<String> lista, int index) {
        if (lista != null && lista.size() > index) {
            String nome = lista.get(index);
            if (nome != null && !nome.trim().isEmpty()) return nome;
        }
        return "---";
    }

    private String displayValor(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? "---" : valor;
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}