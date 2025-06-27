package edu.northeastern.group2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerSentAdapter extends RecyclerView.Adapter<StickerSentAdapter.ViewHolder> {

    private final Context context;
    private final List<StickerSentItem> sentItems;

    public StickerSentAdapter(Context context, List<StickerSentItem> sentItems) {
        this.context = context;
        this.sentItems = sentItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sticker_sent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StickerSentItem item = sentItems.get(position);

        holder.stickerNameText.setText(item.getStickerId());
        holder.countText.setText("Count: " + item.getCount());

        // Set sticker image
        int resId = context.getResources().getIdentifier(
                "sticker_" + item.getStickerId(),
                "drawable",
                context.getPackageName());

        if (resId != 0) {
            holder.stickerImage.setImageResource(resId);
        } else {
            holder.stickerImage.setImageResource(R.drawable.sticker_smile); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return sentItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImage;
        TextView stickerNameText, countText;

        ViewHolder(View itemView) {
            super(itemView);
            stickerImage = itemView.findViewById(R.id.iv_sticker);
            stickerNameText = itemView.findViewById(R.id.tv_sticker_name);
            countText = itemView.findViewById(R.id.tv_count);
        }
    }
} 