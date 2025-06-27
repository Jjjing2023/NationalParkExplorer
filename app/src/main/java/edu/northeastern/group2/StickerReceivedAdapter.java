package edu.northeastern.group2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StickerReceivedAdapter extends RecyclerView.Adapter<StickerReceivedAdapter.ViewHolder> {

    private final Context context;
    private final List<StickerMessage> stickers;

    public StickerReceivedAdapter(Context context, List<StickerMessage> stickers) {
        this.context = context;
        this.stickers = stickers;
    }

    @NonNull
    @Override
    public StickerReceivedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_received_sticker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerReceivedAdapter.ViewHolder holder, int position) {
        StickerMessage msg = stickers.get(position);

        holder.senderText.setText("From: " + msg.getSender());
        holder.timeText.setText(formatTimestamp(msg.getTimestamp()));

        int resId = context.getResources().getIdentifier(
                "sticker_" + msg.getStickerId(),
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
        return stickers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderText, timeText;
        ImageView stickerImage;

        ViewHolder(View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.tv_sender);
            timeText = itemView.findViewById(R.id.tv_time);
            stickerImage = itemView.findViewById(R.id.iv_sticker);
        }
    }

    private String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }
}
