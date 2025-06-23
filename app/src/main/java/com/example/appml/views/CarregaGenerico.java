package com.example.appml.views;

import android.app.Activity;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarregaGenerico {

    public interface AdapterFactory<T> {
        RecyclerView.Adapter<?> create(List<T> items);
    }

    public <T> void carregar(
            Activity context,
            RecyclerView recyclerView,
            Call<List<T>> chamada,
            AdapterFactory<T> factory
    ) {
        chamada.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<T> lista = response.body();
                    RecyclerView.Adapter<?> adapter = factory.create(lista);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(context, "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                Toast.makeText(context, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
