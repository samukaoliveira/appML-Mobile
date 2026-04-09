package com.example.appml.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.musica.Musica;

import java.util.ArrayList;
import java.util.List;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {

    private List<Musica> musicas;
    private OnMusicaPlayListener listener;

    // Interface simplificada (só play agora)
    public interface OnMusicaPlayListener {
        void onPlayClicked(Musica musica);
    }

    public MusicaAdapter(List<Musica> musicas, OnMusicaPlayListener listener) {
        this.musicas = (musicas != null) ? musicas : new ArrayList<>();
        this.listener = listener;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = (musicas != null) ? musicas : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvVersao, tvLink;

        public MusicaViewHolder(View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvVersao = itemView.findViewById(R.id.tvMusicaVersao);
            tvLink = itemView.findViewById(R.id.tvMusicaLink);
        }
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_musica, parent, false);
        return new MusicaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = musicas.get(position);

        holder.tvNome.setText((position + 1) + "ª " + musica.getNome());
        holder.tvVersao.setText(musica.getNomeVersao());

        String audioUrl = musica.getArquivoAudio();
        String youtubeLink = musica.getLinkYoutube();

        // 🔥 SEMPRE limpa estado antes (evita bug de reciclagem)
        holder.tvLink.setVisibility(View.GONE);
        holder.itemView.setEnabled(true);

        // 🎧 Se tiver áudio → clicável
        if (audioUrl != null && !audioUrl.trim().isEmpty()) {

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayClicked(musica);
                }
            });

        }
        // 📺 Se não tiver áudio mas tiver YouTube
        else if (youtubeLink != null && !youtubeLink.trim().isEmpty()) {

            holder.tvLink.setVisibility(View.VISIBLE);
            holder.tvLink.setText(youtubeLink);

            // opcional: clicar abre também (ou você pode remover)
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayClicked(musica);
                }
            });

        }
        // 🚫 Nada disponível
        else {

            holder.tvLink.setVisibility(View.VISIBLE);
            holder.tvLink.setText("Sem áudio disponível");

            holder.itemView.setOnClickListener(null);
            holder.itemView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }

    public List<Musica> getMusicas() {
        return musicas != null ? musicas : new ArrayList<>();
    }
}