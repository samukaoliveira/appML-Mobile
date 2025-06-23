package com.example.appml.views;

import android.annotation.SuppressLint;
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

import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.musica.Musica;
import com.example.appml.models.musica.MusicaDetalhada;
import com.example.appml.models.musica.Versao;
import com.example.appml.services.ApiService;
import com.example.appml.services.HomeService;
import com.example.appml.services.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicaDetalheActivity extends AppCompatActivity {

    private TextView tvNome, tvInterprete, tvTomOriginal, tvUltimoTomTocado;
    private TextView tvBpmOriginal;
    private TextView tvUltimoBpmTocado;
    private TextView tvObservacoes;

    private RecyclerView rvVersoes;
    private TextView tvVersoesVazio;

    private VersaoAdapter versaoAdapter;

    ImageButton btnHome;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica_detalhe);

        btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);

        // Referências dos TextViews no layout (confirme os IDs no seu XML)
        tvNome = findViewById(R.id.tvNome);
        tvInterprete = findViewById(R.id.tvInterprete);
        tvTomOriginal = findViewById(R.id.tvTomOriginal);
        tvUltimoTomTocado = findViewById(R.id.tvUltimoTomTocado);
        tvBpmOriginal = findViewById(R.id.tvBpmOriginal);
        tvUltimoBpmTocado = findViewById(R.id.tvUltimoBpmTocado);
        tvObservacoes = findViewById(R.id.tvObservacoes);

        // RecyclerView e TextView para mensagem de vazio
        rvVersoes = findViewById(R.id.rvVersoes);
        tvVersoesVazio = findViewById(R.id.tvVersoesVazio);

        // Configura RecyclerView
        versaoAdapter = new VersaoAdapter(new ArrayList<>());
        rvVersoes.setLayoutManager(new LinearLayoutManager(this));
        rvVersoes.setAdapter(versaoAdapter);


        int musicaId = getIntent().getIntExtra("musica_id", -1);
        if (musicaId == -1) {
            Toast.makeText(this, "Música inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalheMusica(musicaId);
    }

    private void carregarDetalheMusica(int id) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ApiService.class);
        Call<MusicaDetalhada> call = apiService.getDetalheMusica(id);

        call.enqueue(new Callback<MusicaDetalhada>() {
            @Override
            public void onResponse(Call<MusicaDetalhada> call, Response<MusicaDetalhada> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MusicaDetalhada musica = response.body();

                    Gson gson = new Gson();
                    Log.d("TESTE_JSON", gson.toJson(response.body()));

                    // 1.  Logue o JSON bruto que chega (via interceptor)
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor(message -> Log.d("RAW_JSON", message));
                    log.setLevel(HttpLoggingInterceptor.Level.BODY);


                    tvNome.setText(musica.getNome());
                    tvInterprete.setText("Intérprete: " + displayValor(musica.getInterprete()));
                    tvTomOriginal.setText("Tom Original: " + displayValor(musica.getTomOriginal()));

                    tvUltimoTomTocado.setText("Último Tom Tocado: " + displayValor(musica.getUltimoTomTocado()));
                    tvBpmOriginal.setText("BPM Original: " + displayValor(musica.getBpmOriginal()));
                    tvUltimoBpmTocado.setText("Último BPM Tocado: " + displayValor(musica.getUltimoBpmTocado()));

                    // Atualiza lista de músicas no adapter
                    List<Versao> versoes = musica.getVersoes();

                    if (versoes == null || versoes.isEmpty()) {
                        tvVersoesVazio.setVisibility(View.VISIBLE);
                        rvVersoes.setVisibility(View.GONE);
                    } else {
                        tvVersoesVazio.setVisibility(View.GONE);
                        rvVersoes.setVisibility(View.VISIBLE);

                        versaoAdapter.setVersoes(versoes);  // Atualiza dados no adapter

                        for (Versao versao : musica.getVersoes()) {
                            Log.d("API_TESTE", "🎵 Título: " + versao.getNome() +
                                    ", Versão: " + versao.getNome() +
                                    ", Link: " + versao.getLinkYoutube());
                        }
                    }


                } else {
                    Toast.makeText(MusicaDetalheActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MusicaDetalhada> call, Throwable t) {
                Toast.makeText(MusicaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
