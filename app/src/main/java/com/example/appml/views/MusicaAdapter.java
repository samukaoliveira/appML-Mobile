package com.example.appml.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
    private int currentPlayingIndex = -1;

    public interface OnMusicaPlayListener {
        void onPlayClicked(Musica musica, int position);
    }

    public MusicaAdapter(List<Musica> musicas, OnMusicaPlayListener listener) {
        this.musicas = (musicas != null) ? musicas : new ArrayList<>();
        this.listener = listener;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = (musicas != null) ? musicas : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCurrentPlayingIndex(int index) {
        int oldIndex = this.currentPlayingIndex;
        this.currentPlayingIndex = index;

        // atualiza só os itens necessários (melhor performance)
        if (oldIndex >= 0) notifyItemChanged(oldIndex);
        if (index >= 0) notifyItemChanged(index);
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

        boolean isPlaying = (position == currentPlayingIndex);

        if (isPlaying) {
            holder.btnPlay.setVisibility(View.GONE);
            holder.layoutEq.setVisibility(View.VISIBLE);

            holder.tvNome.setTextColor(0xFF4CAF50);
            holder.tvVersao.setTextColor(0xFF4CAF50);

            startEqualizerAnimation(holder);
        } else {
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.layoutEq.setVisibility(View.GONE);

            holder.tvNome.setTextColor(0xFF000000);
            holder.tvVersao.setTextColor(0xFF666666);

            stopEqualizerAnimation(holder);
        }

        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClicked(musica, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull MusicaViewHolder holder) {
        super.onViewRecycled(holder);
        stopEqualizerAnimation(holder);
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }

    // ================= VIEW HOLDER =================

    static class MusicaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNome, tvVersao;
        ImageButton btnPlay;

        LinearLayout layoutEq;
        View bar1, bar2, bar3;

        ObjectAnimator anim1, anim2, anim3;

        public MusicaViewHolder(View itemView) {
            super(itemView);

            tvNome   = itemView.findViewById(R.id.tvNome);
            tvVersao = itemView.findViewById(R.id.tvMusicaVersao);
            btnPlay  = itemView.findViewById(R.id.btnPlay);

            layoutEq = itemView.findViewById(R.id.layoutEqualizer);
            bar1     = itemView.findViewById(R.id.eqBar1);
            bar2     = itemView.findViewById(R.id.eqBar2);
            bar3     = itemView.findViewById(R.id.eqBar3);
        }
    }

    // ================= ANIMAÇÃO =================

    private void startEqualizerAnimation(MusicaViewHolder holder) {
        stopEqualizerAnimation(holder); // evita duplicação

        holder.anim1 = createAnim(holder.bar1, 300);
        holder.anim2 = createAnim(holder.bar2, 500);
        holder.anim3 = createAnim(holder.bar3, 400);

        holder.anim1.start();
        holder.anim2.start();
        holder.anim3.start();
    }

    private void stopEqualizerAnimation(MusicaViewHolder holder) {
        if (holder.anim1 != null) {
            holder.anim1.cancel();
            holder.anim1 = null;
        }

        if (holder.anim2 != null) {
            holder.anim2.cancel();
            holder.anim2 = null;
        }

        if (holder.anim3 != null) {
            holder.anim3.cancel();
            holder.anim3 = null;
        }

        // reset visual (IMPORTANTE pro RecyclerView)
        if (holder.bar1 != null) holder.bar1.setScaleY(1f);
        if (holder.bar2 != null) holder.bar2.setScaleY(1f);
        if (holder.bar3 != null) holder.bar3.setScaleY(1f);
    }

    private ObjectAnimator createAnim(View view, long duration) {
        if (view == null) return null;

        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1f);
        anim.setDuration(duration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        return anim;
    }
}