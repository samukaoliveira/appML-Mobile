// CarregaEscalas.java  (renomeei para não ter o "rr" duplicado)
package com.example.appml.views;

import android.app.Activity;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.models.escala.EscalaSimples;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utilitário para popular um RecyclerView com uma lista de EscalaSimples.
 *
 * Exemplo de uso:
 *     new CarregaEscalas().carregar(
 *         this,
 *         recyclerView,
 *         apiService.getListaEscalas(),   // OU getMinhasEscalas()
 *         this                           // se a Activity implementar OnEscalaClickListener
 *     );
 */
public class CarregaEscalas {

    public void carregar(
            Activity context,
            RecyclerView recyclerView,
            Call<List<EscalaSimples>> chamada,
            EscalasAdapter.OnEscalaClickListener listener // pode ser null
    ) {
        chamada.enqueue(new Callback<List<EscalaSimples>>() {
            @Override
            public void onResponse(Call<List<EscalaSimples>> call,
                                   Response<List<EscalaSimples>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<EscalaSimples> escalas = response.body();
                    EscalasAdapter adapter = new EscalasAdapter(escalas, listener);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(context,
                            "Falha ao carregar escalas",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EscalaSimples>> call, Throwable t) {
                Toast.makeText(context,
                        "Erro: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
