package edu.northeastern.group2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class StickerNotificationHelper {

    private static final String CHANNEL_ID = "StickerChannel";

    public static void sendNotification(Context context, StickerMessage msg) {
        if (msg == null || msg.getSender() == null) return;

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(manager);

        int resId = context.getResources().getIdentifier(
                "sticker_" + msg.getStickerId(),
                "drawable",
                context.getPackageName());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("New sticker from " + msg.getSender())
                .setContentText("You just received a sticker!")
                .setSmallIcon(R.drawable.ic_arrow_forward)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), resId, options))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), resId, options))
                        .bigLargeIcon((Bitmap) null))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());

        Log.d("DEBUG", "Sending notification for sticker: " + msg.getStickerId());
    }

    private static void createChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sticker Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifies when a sticker is received.");
            manager.createNotificationChannel(channel);
        }
    }
}
