package com.example.appml.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaSimples;
import com.example.appml.views.EscalasAdapter;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscalasActivity extends AppCompatActivity implements EscalasAdapter.OnEscalaClickListener {

    private RecyclerView recyclerView;
    private Button minhasEscalas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escalas);

        recyclerView = findViewById(R.id.recycler_escalas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        minhasEscalas = findViewById(R.id.minhas_escalas);

        minhasEscalas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EscalasActivity.this, MinhasEscalasActivity.class);
                startActivity(intent);
            }
        });

        ApiService api = RetrofitInstance.getRetrofitInstance(this)
                .create(ApiService.class);

        new CarregaEscalas().carregar(
                this,
                recyclerView,
                api.getListaEscalas(),
                this
        );
    }

    @Override
    public void onEscalaClick(int escalaId) {
        // Abrir detalhe passando o id da escala clicada
        Intent intent = new Intent(this, EscalaDetalheActivity.class);
        intent.putExtra("escala_id", escalaId);
        startActivity(intent);
    }
}
