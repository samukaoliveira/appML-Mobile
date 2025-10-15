package com.example.appml.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    // Interface para controlar play/pause na Activity
    public interface OnMusicaPlayListener {
        void onPlayClicked(Musica musica);
        void onPauseClicked();
    }

    public MusicaAdapter(List<Musica> musicas, OnMusicaPlayListener listener) {
        if (musicas != null) {
            this.musicas = musicas;
        } else {
            this.musicas = new ArrayList<>();
        }
        this.listener = listener;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
        notifyDataSetChanged();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvVersao, tvLink;
        LinearLayout layoutPlayer;
        ImageButton btnPlay, btnPause;

        public MusicaViewHolder(View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvVersao = itemView.findViewById(R.id.tvMusicaVersao);
            tvLink = itemView.findViewById(R.id.tvMusicaLink);
            layoutPlayer = itemView.findViewById(R.id.layoutPlayer);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnPause = itemView.findViewById(R.id.btnPause);
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

        // Caso tenha áudio
        if (audioUrl != null && !audioUrl.trim().isEmpty()) {
            holder.layoutPlayer.setVisibility(View.VISIBLE);
            holder.tvLink.setVisibility(View.GONE);

            holder.btnPlay.setOnClickListener(v -> listener.onPlayClicked(musica));
            holder.btnPause.setOnClickListener(v -> listener.onPauseClicked());
        }
        // Caso não tenha áudio mas tenha YouTube
        else if (youtubeLink != null && !youtubeLink.trim().isEmpty()) {
            holder.layoutPlayer.setVisibility(View.GONE);
            holder.tvLink.setVisibility(View.VISIBLE);
            holder.tvLink.setText(youtubeLink);
        }
        // Nenhum dos dois disponível
        else {
            holder.layoutPlayer.setVisibility(View.GONE);
            holder.tvLink.setVisibility(View.VISIBLE);
            holder.tvLink.setText("Sem áudio disponível");
        }
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }
}
