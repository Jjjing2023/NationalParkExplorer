package edu.northeastern.group2;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StickerHistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private StickerHistoryAdapter adapter;
    private List<StickerMessage> receivedStickers;
    private String currentUser;
    private DatabaseReference receivedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sticker_history);

        SharedPreferences prefs = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "Guest");

        if ("Guest".equals(currentUser)) {
            finish();
            return;
        }

        historyRecyclerView = findViewById(R.id.rv_received_history);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receivedStickers = new ArrayList<>();
        adapter = new StickerHistoryAdapter(this, receivedStickers);
        historyRecyclerView.setAdapter(adapter);

        receivedRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser)
                .child("received");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        listenForReceivedStickers();


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void listenForReceivedStickers() {
        receivedRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "Children count = " + snapshot.getChildrenCount());
                receivedStickers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    StickerMessage msg = ds.getValue(StickerMessage.class);
                    if (msg != null) {
                        Log.d("DEBUG", "Parsed: " + msg.getSender() + " → " + msg.getStickerId());
                        receivedStickers.add(msg);
                        StickerNotificationHelper.sendNotification(StickerHistoryActivity.this, msg);
                    }else{
                        Log.e("DEBUG", "Failed to parse msg from snapshot: " + ds.toString());
                    }
                }
                // 倒序排列：最新的贴纸在上方
                Collections.reverse(receivedStickers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StickerHistory", "Database error: " + error.getMessage());
            }
        });
    }
}