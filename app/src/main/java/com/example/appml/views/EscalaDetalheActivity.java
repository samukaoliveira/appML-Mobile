package com.example.appml.views;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscalaDetalheActivity extends AppCompatActivity {

    private TextView tvNome, tvData, tvHora;
    private TextView tvBaterista, tvBaixista, tvTecladista;
    private TextView tvVocalistas, tvViolonista, tvGuitarrista;
    private TextView tvObservacoes, tvFuncaoUsuario, tvMusica;

    // Exemplo: id do usuário logado (deve pegar do SharedPreferences ou outra forma real)
    private int userId = 35;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        // Referência aos TextViews no layout (confirme os IDs no seu XML)
        tvNome = findViewById(R.id.tv_nome);
        tvData = findViewById(R.id.tv_data);
        tvHora = findViewById(R.id.tv_hora);

        tvBaterista = findViewById(R.id.tv_baterista);
        tvBaixista = findViewById(R.id.tv_baixista);
        tvTecladista = findViewById(R.id.tv_tecladista);

        tvVocalistas = findViewById(R.id.tv_vocalistas);
        tvViolonista = findViewById(R.id.tv_violonista);
        tvGuitarrista = findViewById(R.id.tv_guitarrista);

        tvObservacoes = findViewById(R.id.tv_observacoes);
        tvFuncaoUsuario = findViewById(R.id.tv_funcao_usuario);
        tvMusica = findViewById(R.id.tv_musica);

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

                    tvNome.setText(escala.getNome());
                    tvData.setText(escala.getData());
                    tvHora.setText(escala.getHora());

                    tvBaterista.setText("Baterista: " + displayValor(escala.getBaterista()));
                    tvBaixista.setText("Baixista: " + displayValor(escala.getBaixista()));
                    tvTecladista.setText("Tecladista: " + displayValor(escala.getTecladista()));

                    String vocalistas = escala.getVocalista();
                    if (vocalistas != null && !vocalistas.isEmpty() && !vocalistas.equals("-")) {
                        tvVocalistas.setText("Vocalistas: " + vocalistas);
                    } else {
                        tvVocalistas.setText("Vocalistas: ---");
                    }

                    tvViolonista.setText("Violonista: " + displayValor(escala.getViolonista()));
                    tvGuitarrista.setText("Guitarrista: " + displayValor(escala.getGuitarrista()));

                    // Como não há campo música no JSON, deixamos fixo
                    tvMusica.setText("Nenhuma música associada a esta escala.");

                    tvObservacoes.setText("Obs: " + (escala.getObs() != null && !escala.getObs().equals("-") ? escala.getObs() : "-"));

                    String funcaoUsuario = descobrirFuncaoUsuario(escala, userId);
                    tvFuncaoUsuario.setText("Sua função nesta escala: " + funcaoUsuario);

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

    private String descobrirFuncaoUsuario(EscalaDetalhada escala, int userId) {
        String userIdStr = String.valueOf(userId);

        if (userIdStr.equals(escala.getBaterista())) return "Baterista";
        if (userIdStr.equals(escala.getBaixista())) return "Baixista";
        if (userIdStr.equals(escala.getTecladista())) return "Tecladista";
        if (userIdStr.equals(escala.getVocalista())) return "Vocalista";
        if (userIdStr.equals(escala.getViolonista())) return "Violonista";
        if (userIdStr.equals(escala.getGuitarrista())) return "Guitarrista";

        return "Nenhuma função atribuída";
    }
}
