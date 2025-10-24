package com.example.appml.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appml.R;
import com.example.appml.models.mensalidade.Mensalidade;

import java.util.List;

public class MensalidadeAdapter extends RecyclerView.Adapter<MensalidadeAdapter.ViewHolder> {

    private final List<Mensalidade> mensalidades;
    private final Context context;

    public MensalidadeAdapter(Context context, List<Mensalidade> mensalidades) {
        this.context = context;
        this.mensalidades = mensalidades;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mensalidade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensalidade m = mensalidades.get(position);

        // Mês e ano juntos
        holder.tvMesAno.setText(m.getMes() + " / " + m.getAno());

        // Valor
        holder.tvValor.setText("Valor: R$ " + String.format("%.2f", m.getValor()));

        // Status e cor
        holder.tvStatus.setText("Status: " + m.getStatus());
        if ("pago".equalsIgnoreCase(m.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        // Chave Pix (só aparece se houver)
        if (m.getPix() != null && !m.getPix().trim().isEmpty()) {
            holder.tvChavePix.setVisibility(View.VISIBLE);
            holder.tvChavePix.setText("Pix: " + m.getPix());

            // Copiar Pix ao tocar
            holder.tvChavePix.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("PIX", m.getPix());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Chave PIX copiada!", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.tvChavePix.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensalidades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMesAno, tvValor, tvStatus, tvChavePix;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMesAno = itemView.findViewById(R.id.tvMesAno);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvChavePix = itemView.findViewById(R.id.tvChavePix);
        }
    }

    public void updateData(List<Mensalidade> novaLista) {
        this.mensalidades.clear();
        this.mensalidades.addAll(novaLista);
        notifyDataSetChanged();
    }
}
