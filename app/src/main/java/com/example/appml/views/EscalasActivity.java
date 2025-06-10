package com.example.appml.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private EscalasAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escalas);

        recyclerView = findViewById(R.id.recycler_escalas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carregarEscalas();
    }

    private void carregarEscalas() {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ApiService.class);
        Call<List<EscalaSimples>> call = apiService.getListaEscalas();

        call.enqueue(new Callback<List<EscalaSimples>>() {
            @Override
            public void onResponse(Call<List<EscalaSimples>> call, Response<List<EscalaSimples>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EscalaSimples> escalas = response.body();
                    adapter = new EscalasAdapter(escalas, EscalasActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(EscalasActivity.this, "Falha ao carregar escalas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EscalaSimples>> call, Throwable t) {
                Toast.makeText(EscalasActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEscalaClick(int escalaId) {
        // Abrir detalhe passando o id da escala clicada
        Intent intent = new Intent(this, EscalaDetalheActivity.class);
        intent.putExtra("escala_id", escalaId);
        startActivity(intent);
    }
}
