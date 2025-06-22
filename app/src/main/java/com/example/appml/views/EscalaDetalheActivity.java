package com.example.appml.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.MainActivity;
import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.musica.Musica;
import com.example.appml.services.ApiService;
import com.example.appml.services.HomeService;
import com.example.appml.services.RetrofitInstance;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscalaDetalheActivity extends AppCompatActivity {

    private TextView tvNome, tvData, tvHora;
    private TextView tvBaterista, tvBaixista, tvTecladista;
    private TextView tvVocalistas, tvViolonista, tvGuitarrista;
    private TextView tvObservacoes;

    private RecyclerView rvMusicas;
    private TextView tvMusicasVazia;

    private MusicaAdapter musicaAdapter;

    ImageButton btnHome;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);

        // Referências dos TextViews no layout (confirme os IDs no seu XML)
        tvNome = findViewById(R.id.tvNome);
        tvData = findViewById(R.id.tvData);
        tvHora = findViewById(R.id.tvHora);

        tvBaterista = findViewById(R.id.tvBaterista);
        tvBaixista = findViewById(R.id.tvBaixista);
        tvTecladista = findViewById(R.id.tvTecladista);

        tvVocalistas = findViewById(R.id.tvVocalistas);
        tvViolonista = findViewById(R.id.tvViolonista);
        tvGuitarrista = findViewById(R.id.tvGuitarrista);

        tvObservacoes = findViewById(R.id.tvObservacoes);

        // RecyclerView e TextView para mensagem de vazio
        rvMusicas = findViewById(R.id.rvMusicas);
        tvMusicasVazia = findViewById(R.id.tvMusicasVazia);

        // Configura RecyclerView
        musicaAdapter = new MusicaAdapter(new ArrayList<>());
        rvMusicas.setLayoutManager(new LinearLayoutManager(this));
        rvMusicas.setAdapter(musicaAdapter);


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
        Call<EscalaDetalhada> call = apiService.getDetalheEscala(id);

        call.enqueue(new Callback<EscalaDetalhada>() {
            @Override
            public void onResponse(Call<EscalaDetalhada> call, Response<EscalaDetalhada> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EscalaDetalhada escala = response.body();

                    Gson gson = new Gson();
                    Log.d("TESTE_JSON", gson.toJson(response.body()));

                    // 1.  Logue o JSON bruto que chega (via interceptor)
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor(message -> Log.d("RAW_JSON", message));
                    log.setLevel(HttpLoggingInterceptor.Level.BODY);


                    tvNome.setText(escala.getNome());
                    tvData.setText(escala.getData());
                    tvHora.setText(escala.getHora());

                    tvBaterista.setText("Baterista: " + displayValor(escala.getBaterista()));
                    tvBaixista.setText("Baixista: " + displayValor(escala.getBaixista()));
                    tvTecladista.setText("Tecladista: " + displayValor(escala.getTecladista()));

                    List<String> vocalistas = escala.getVocalistas();
                    if (vocalistas != null && !vocalistas.isEmpty() && !vocalistas.equals("-")) {
                        String nomes = TextUtils.join(", ", vocalistas);
                        tvVocalistas.setText("Vocalistas: " + nomes);
                    } else {
                        tvVocalistas.setText("Vocalistas: ---");
                    }

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));

                    // Atualiza lista de músicas no adapter
                    List<Musica> musicas = escala.getMusicas();

                    if (musicas == null || musicas.isEmpty()) {
                        tvMusicasVazia.setVisibility(View.VISIBLE);
                        rvMusicas.setVisibility(View.GONE);
                    } else {
                        tvMusicasVazia.setVisibility(View.GONE);
                        rvMusicas.setVisibility(View.VISIBLE);

                        musicaAdapter.setMusicas(musicas);  // Atualiza dados no adapter

                        for (Musica musica : escala.getMusicas()) {
                            Log.d("API_TESTE", "🎵 Título: " + musica.getTitulo() +
                                    ", Versão: " + musica.getNomeVersao() +
                                    ", Link: " + musica.getLinkYoutube());
                        }
                    }

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null && !escala.getObs().equals("-") ? escala.getObs() : "-"));

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
        if (valor == null || valor.equals("-") || valor.trim().isEmpty()) {
            return "---";
        }
        return valor;
    }
}
