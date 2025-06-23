package com.example.appml.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaSimples;
import com.example.appml.models.musica.Musica;

import java.util.List;

public class MusicasAdapter extends RecyclerView.Adapter<MusicasAdapter.MusicaViewHolder> {

    private List<Musica> musicas;
    private OnMusicaClickListener listener;

    public interface OnMusicaClickListener {
        void onMusicaClick(int musicaId);
    }

    public MusicasAdapter(List<Musica> musicas, OnMusicaClickListener listener) {
        this.musicas = musicas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica_simples, parent, false);
        return new MusicaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = musicas.get(position);

        holder.nome.setText(musica.getNome());
        holder.interprete.setText(musica.getInterprete());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMusicaClick(musica.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView nome, interprete;

        public MusicaViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.tvNome);
            interprete = itemView.findViewById(R.id.tvInterprete);
        }
    }
}
