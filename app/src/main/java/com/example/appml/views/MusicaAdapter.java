package com.example.appml.views;

import android.view.LayoutInflater;
import com.example.appml.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.models.musica.Musica;

import java.util.ArrayList;
import java.util.List;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {
    private List<Musica> musicas;

    public MusicaAdapter(List<Musica> musicas) {
        if (musicas != null) {
            this.musicas = musicas;
        } else {
            this.musicas = new ArrayList<>();
        }
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
        notifyDataSetChanged();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvLink, tvVersao;

        public MusicaViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvNome);
            tvLink = itemView.findViewById(R.id.tvMusicaLink);
            tvVersao = itemView.findViewById(R.id.tvMusicaVersao);
        }
    }

    @Override
    public MusicaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_musica, parent, false);
        return new MusicaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicaViewHolder holder, int position) {
        Musica musica = musicas.get(position);
        holder.tvTitulo.setText((position + 1) + "ª " + musica.getNome());
        holder.tvLink.setText(musica.getLinkYoutube());
        holder.tvVersao.setText(musica.getNomeVersao());
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }
}
