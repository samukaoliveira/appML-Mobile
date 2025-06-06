package com.example.appml.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.escala.EscalaSimples;

import java.util.List;

public class EscalasAdapter extends RecyclerView.Adapter<EscalasAdapter.EscalaViewHolder> {

    private List<EscalaSimples> escalas;
    private OnEscalaClickListener listener;

    public interface OnEscalaClickListener {
        void onEscalaClick(int escalaId);
    }

    public EscalasAdapter(List<EscalaSimples> escalas, OnEscalaClickListener listener) {
        this.escalas = escalas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EscalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_escala_simples, parent, false);
        return new EscalaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EscalaViewHolder holder, int position) {
        EscalaSimples escala = escalas.get(position);

        holder.nome.setText(escala.getNome());
        holder.dataMinisterio.setText(escala.getData() + " - " + escala.getMinisterio());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEscalaClick(escala.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return escalas.size();
    }

    public static class EscalaViewHolder extends RecyclerView.ViewHolder {
        TextView nome, dataMinisterio;

        public EscalaViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome_escala);
            dataMinisterio = itemView.findViewById(R.id.data_ministerio);
        }
    }
}
