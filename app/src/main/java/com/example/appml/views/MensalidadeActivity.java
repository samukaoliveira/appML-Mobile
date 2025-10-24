package com.example.appml.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.mensalidade.Mensalidade;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensalidadeActivity extends AppCompatActivity {

    private RecyclerView rvMensalidades;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensalidades);

        rvMensalidades = findViewById(R.id.rvMensalidades);
        progressBar = new ProgressBar(this);
        rvMensalidades.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitInstance.getRetrofitInstance(this).create(ApiService.class);

        carregarMensalidades();
    }

    private void carregarMensalidades() {
        progressBar.setVisibility(View.VISIBLE);
        rvMensalidades.setVisibility(View.GONE);

        apiService.getMensalidades().enqueue(new Callback<List<Mensalidade>>() {
            @Override
            public void onResponse(Call<List<Mensalidade>> call, Response<List<Mensalidade>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Mensalidade> lista = response.body();

                    Log.d("API_MENSALIDADE", "Sucesso! Quantidade de mensalidades recebidas: " + lista.size());

                    if (lista.isEmpty()) {
                        Toast.makeText(MensalidadeActivity.this, "Nenhuma mensalidade encontrada", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (rvMensalidades.getAdapter() == null) {
                        MensalidadeAdapter adapter = new MensalidadeAdapter(MensalidadeActivity.this, lista);
                        rvMensalidades.setAdapter(adapter);
                    } else {
                        MensalidadeAdapter adapter = (MensalidadeAdapter) rvMensalidades.getAdapter();
                        adapter.updateData(lista);
                    }

                    rvMensalidades.setVisibility(View.VISIBLE);
                } else {
                    Log.e("API_MENSALIDADE", "Erro HTTP: " + response.code() + " / " + response.message());
                    Toast.makeText(MensalidadeActivity.this, "Erro ao carregar mensalidades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mensalidade>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                rvMensalidades.setVisibility(View.VISIBLE);

                Log.e("API_MENSALIDADE", "Falha de rede ou conversão: " + t.getMessage(), t);
                Toast.makeText(MensalidadeActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

