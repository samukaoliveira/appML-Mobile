package com.example.appml.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.musica.Musica;
import com.example.appml.models.musica.Versao;

import java.util.ArrayList;
import java.util.List;

public class VersaoAdapter extends RecyclerView.Adapter<VersaoAdapter.VersaoViewHolder> {
    private List<Versao> versoes;

    public VersaoAdapter(List<Versao> versoes) {
        if (versoes != null) {
            this.versoes = versoes;
        } else {
            this.versoes = new ArrayList<>();
        }
    }

    public void setVersoes(List<Versao> versoes) {
        this.versoes = versoes;
        notifyDataSetChanged();
    }

    public static class VersaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvLink;

        public VersaoViewHolder(View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvLink = itemView.findViewById(R.id.tvMusicaLink);
        }
    }

    @Override
    public VersaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_versao, parent, false);
        return new VersaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VersaoViewHolder holder, int position) {
        Versao versao = versoes.get(position);
        holder.tvNome.setText((position + 1) + "ª " + versao.getNome());
        holder.tvLink.setText(versao.getLinkYoutube());
    }

    @Override
    public int getItemCount() {
        return versoes.size();
    }
}
