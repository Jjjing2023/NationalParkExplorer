package edu.northeastern.group2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StickerSentActivity extends AppCompatActivity {

    private RecyclerView sentRecyclerView;
    private StickerSentAdapter adapter;
    private List<StickerSentItem> sentList;
    private String currentUser;
    private DatabaseReference sentRef;
    private TextView totalStickersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_sent);

        SharedPreferences prefs = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "Guest");

        if ("Guest".equals(currentUser)) {
            finish();
            return;
        }

        // Initialize views
        TextView titleText = findViewById(R.id.tv_statistics_title);
        titleText.setText("Sent Stickers");
        
        totalStickersText = findViewById(R.id.tv_total_stickers);
        
        sentRecyclerView = findViewById(R.id.rv_statistics);
        sentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sentList = new ArrayList<>();
        adapter = new StickerSentAdapter(this, sentList);
        sentRecyclerView.setAdapter(adapter);

        // Get reference to sent stickers
        sentRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser)
                .child("sent");

        loadSentStickers();
    }

    private void loadSentStickers() {
        sentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> stickerCounts = new HashMap<>();
                int totalCount = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    StickerMessage msg = ds.getValue(StickerMessage.class);
                    if (msg != null) {
                        String stickerId = msg.getStickerId();
                        stickerCounts.put(stickerId, stickerCounts.getOrDefault(stickerId, 0) + 1);
                        totalCount++;
                    }
                }

                // Update total count
                totalStickersText.setText("Total Stickers Sent: " + totalCount);

                // Convert to list for adapter
                sentList.clear();
                for (Map.Entry<String, Integer> entry : stickerCounts.entrySet()) {
                    sentList.add(new StickerSentItem(entry.getKey(), entry.getValue()));
                }

                adapter.notifyDataSetChanged();
                Log.d("SentStickers", "Loaded " + sentList.size() + " sticker types");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SentStickers", "Database error: " + error.getMessage());
            }
        });
    }
} 