package edu.northeastern.group2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerAdapter
        extends RecyclerView.Adapter<StickerAdapter.VH> {

    public interface OnStickerClickListener {
        void onStickerClick(StickerItem sticker);
    }

    private final List<StickerItem> stickers;
    private final OnStickerClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public StickerAdapter(List<StickerItem> stickers,
                          OnStickerClickListener listener) {
        this.stickers = stickers;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        StickerItem item = stickers.get(pos);
        holder.iv.setImageResource(item.getDrawableRes());

        holder.iv.setAlpha(pos == selectedPosition ? 1f : 0.5f);

        holder.iv.setOnClickListener(v -> {
            int old = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(old);
            notifyItemChanged(selectedPosition);
            listener.onStickerClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ImageView iv;
        public VH(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.sticker_iv);
        }
    }
}
