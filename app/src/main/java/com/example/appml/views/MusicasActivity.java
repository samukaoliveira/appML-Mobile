package com.example.appml.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.musica.Musica;
import com.example.appml.models.musica.MusicaDetalhada;
import com.example.appml.services.ApiService;
import com.example.appml.services.HomeService;
import com.example.appml.services.RetrofitInstance;

public class MusicasActivity extends AppCompatActivity implements MusicasAdapter.OnMusicaClickListener {

    private RecyclerView recyclerView;

    ImageButton btnHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicas);

        btnHome = findViewById(R.id.btnHome);
        HomeService.VoltaPraHome(btnHome, this);

        recyclerView = findViewById(R.id.recycler_musicas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiService api = RetrofitInstance.getRetrofitInstance(this)
                .create(ApiService.class);

        new CarregaGenerico().<Musica>carregar(
                this,
                recyclerView,
                api.getMusicas(),
                musicas -> new MusicasAdapter(musicas, this)
        );
    }

    @Override
    public void onMusicaClick(int musicaId) {
        // Abrir detalhe passando o id da escala clicada
        Intent intent = new Intent(this, MusicaDetalheActivity.class);
        intent.putExtra("musica_id", musicaId);
        startActivity(intent);
    }
}
