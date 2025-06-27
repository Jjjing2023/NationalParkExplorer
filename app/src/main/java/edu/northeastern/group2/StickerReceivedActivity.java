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

public class StickerReceivedActivity extends AppCompatActivity {

    private RecyclerView receivedRecyclerView;
    private StickerReceivedAdapter adapter;
    private List<StickerMessage> receivedStickers;
    private String currentUser;
    private DatabaseReference receivedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sticker_received);

        SharedPreferences prefs = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "Guest");

        if ("Guest".equals(currentUser)) {
            finish();
            return;
        }

        receivedRecyclerView = findViewById(R.id.rv_received_history);
        receivedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receivedStickers = new ArrayList<>();
        adapter = new StickerReceivedAdapter(this, receivedStickers);
        receivedRecyclerView.setAdapter(adapter);

        receivedRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser)
                .child("received");

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
                        //StickerNotificationHelper.sendNotification(StickerHistoryActivity.this, msg);
                    }else{
                        Log.e("DEBUG", "Failed to parse msg from snapshot: " + ds.toString());
                    }
                }
                Collections.reverse(receivedStickers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StickerReceived", "Database error: " + error.getMessage());
            }
        });
    }
}