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

    private TextView tvNome, tvData, tvMinisterio, tvHora;
    private TextView tvBaterista, tvBaixista, tvSaxofonista, tvTecladista;
    private TextView tvVocalistas, tvViolonista, tvGuitarrista, tvMusica;
    private TextView tvObservacoes, tvFuncaoUsuario;

    private int userId = 123; // Exemplo: id do usuário logado, pode pegar do SharedPreferences

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_detalhe);

        tvNome = findViewById(R.id.tv_nome);
        tvData = findViewById(R.id.tv_data);
        tvMinisterio = findViewById(R.id.tv_ministerio);
        tvHora = findViewById(R.id.tv_hora);

        tvBaterista = findViewById(R.id.tv_baterista);
        tvBaixista = findViewById(R.id.tv_baxista);
        tvSaxofonista = findViewById(R.id.tv_saxofonista);
        tvTecladista = findViewById(R.id.tv_tecladista);

        tvVocalistas = findViewById(R.id.tv_vocalistas);
        tvViolonista = findViewById(R.id.tv_violonista);
        tvGuitarrista = findViewById(R.id.tv_guitarrista);
        tvMusica = findViewById(R.id.tv_musica);

        tvObservacoes = findViewById(R.id.tv_observacoes);
        tvFuncaoUsuario = findViewById(R.id.tv_funcao_usuario);

        int escalaId = getIntent().getIntExtra("escala_id", -1);
        if (escalaId == -1) {
            Toast.makeText(this, "Escala inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalheEscala(escalaId);
    }

    private void carregarDetalheEscala(int id) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        Call<EscalaDetalhada> call = apiService.getDetalheEscala(id);

        call.enqueue(new Callback<EscalaDetalhada>() {
            @Override
            public void onResponse(Call<EscalaDetalhada> call, Response<EscalaDetalhada> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EscalaDetalhada escala = response.body();

                    tvNome.setText(escala.getNome());
                    tvData.setText(escala.getData());
                    tvMinisterio.setText(escala.getMinisterio());
                    tvHora.setText(escala.getHora());

                    tvBaterista.setText("Baterista: " + escala.getBaterista());
                    tvBaixista.setText("Baixista: " + escala.getBaixista());
                    tvSaxofonista.setText("Saxofonista: " + escala.getSaxofonista());
                    tvTecladista.setText("Tecladista: " + escala.getTecladista());

                    tvVocalistas.setText("Vocalistas: " + (escala.getVocalistas() != null && !escala.getVocalistas().isEmpty() ? escala.getVocalistas() : "---"));
                    tvViolonista.setText("Violonista: " + escala.getViolonista());
                    tvGuitarrista.setText("Guitarrista: " + escala.getGuitarrista());

                    if (escala.isTemMusicaAssociada()) {
                        tvMusica.setText("Há música associada a esta escala.");
                    } else {
                        tvMusica.setText("Nenhuma música associada a esta escala.");
                    }

                    tvObservacoes.setText("Obs: " + (escala.getObservacoes() != null ? escala.getObservacoes() : "-"));

                    // Verificar função do usuário nesta escala
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

    private String descobrirFuncaoUsuario(EscalaDetalhada escala, int userId) {
        // Exemplo: verificar em cada campo se o nome do usuário está atribuído
        // Para isso, você precisa do nome do usuário logado (aqui vamos supor que seja "Samuel")
        String nomeUsuario = "Samuel"; // Pega do login real

        if (nomeUsuario.equalsIgnoreCase(escala.getBaterista())) return "Baterista";
        if (nomeUsuario.equalsIgnoreCase(escala.getBaixista())) return "Baixista";
        if (nomeUsuario.equalsIgnoreCase(escala.getSaxofonista())) return "Saxofonista";
        if (nomeUsuario.equalsIgnoreCase(escala.getTecladista())) return "Tecladista";

        // Vocalistas podem ser múltiplos (se quiser, pode tratar)
        if (escala.getVocalistas() != null && escala.getVocalistas().toLowerCase().contains(nomeUsuario.toLowerCase())) return "Vocalista";

        if (nomeUsuario.equalsIgnoreCase(escala.getViolonista())) return "Violonista";
        if (nomeUsuario.equalsIgnoreCase(escala.getGuitarrista())) return "Guitarrista";

        return "Nenhuma função atribuída";
    }
}
