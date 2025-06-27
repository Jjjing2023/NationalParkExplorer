package edu.northeastern.group2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Afterlogin extends AppCompatActivity {
    private String currentUser;
    private DatabaseReference usersRef;
//    private Spinner spinnerRecipient;
private AutoCompleteTextView dropdownRecipient;
    private RecyclerView rvStickers;
    private StickerAdapter stickerAdapter;
    private StickerItem selectedSticker;
    private static final String SCREEN_NAME = "Stick It To Them";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.afterlogin_root),
                (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                }
        );

        SharedPreferences prefs = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "Guest");
        if (currentUser == null || "Guest".equals(currentUser.trim())) {
            Toast.makeText(this, "No valid username found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView header = findViewById(R.id.screenText);
        header.setText(SCREEN_NAME + " - Welcome " + currentUser);

        usersRef = FirebaseDatabase.getInstance()
                .getReference("users");

//        spinnerRecipient = findViewById(R.id.spinner_recipient);
        dropdownRecipient = findViewById(R.id.dropdown_recipient);
        loadRecipients();

        rvStickers = findViewById(R.id.rv_stickers);
        rvStickers.setLayoutManager(new GridLayoutManager(this, 3));
        List<StickerItem> stickerList = getPredefinedStickers();
        stickerAdapter = new StickerAdapter(stickerList, sticker -> {
            selectedSticker = sticker;
        });
        rvStickers.setAdapter(stickerAdapter);

        Button btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v -> sendSticker());

        Button btnHistory = findViewById(R.id.btn_view_history);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(Afterlogin.this, StickerHistoryActivity.class);
            startActivity(intent);
        });


        DatabaseReference receivedRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser)
                .child("received");

        SharedPreferences localPrefs = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        long lastSeen = localPrefs.getLong("lastSeenTimestamp", 0);
        final long[] maxSeenTimestamp = {lastSeen};

        receivedRef.orderByChild("timestamp").startAt(lastSeen + 1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        StickerMessage msg = snapshot.getValue(StickerMessage.class);
                        if (msg != null) {
                            Log.d("Afterlogin", "New sticker received: " + msg.getStickerId());
                            StickerNotificationHelper.sendNotification(Afterlogin.this, msg);
                            maxSeenTimestamp[0] = Math.max(maxSeenTimestamp[0], msg.timestamp);
                        }
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Afterlogin", "Notification listener error: " + error.getMessage());
                    }
                });

        new android.os.Handler().postDelayed(() -> {
            localPrefs.edit().putLong("lastSeenTimestamp", maxSeenTimestamp[0]).apply();
        }, 2000);


    }

//    private void loadRecipients() {
//        usersRef.get().addOnCompleteListener(task -> {
//            List<String> names = new ArrayList<>();
//            names.add(getString(R.string.select_recipient));
//
//            if (task.isSuccessful() && task.getResult().exists()) {
//                for (DataSnapshot snap : task.getResult().getChildren()) {
//                    String name = snap.getKey();
//                    if (name != null && !name.equals(currentUser)) {
//                        names.add(name);
//                    }
//                }
//            }
//            if (names.size() == 1) {
//                names.add("No friends");
//            }
//
//            Log.d("Afterlogin", "Loaded recipients: " + names);
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                    this,
//                    R.layout.spinner_hint_item,
//                    names);
//            adapter.setDropDownViewResource(
//                    android.R.layout.simple_spinner_dropdown_item);
//            spinnerRecipient.setAdapter(adapter);
//            spinnerRecipient.setSelection(0);
//
//        });
//    }

    private void loadRecipients() {
        usersRef.get().addOnCompleteListener(task -> {
            List<String> names = new ArrayList<>();

            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    String name = snap.getKey();
                    if (name != null && !name.equals(currentUser)) {
                        names.add(name);
                    }
                }
            }

            if (names.isEmpty()) {
                names.add("No friends");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    names
            );
            dropdownRecipient.setAdapter(adapter);
        });
    }


    private List<StickerItem> getPredefinedStickers() {
        List<StickerItem> list = new ArrayList<>();
        list.add(new StickerItem("thumbsup", R.drawable.sticker_thumbsup));
        list.add(new StickerItem("sun",      R.drawable.sticker_sun));
        list.add(new StickerItem("star",     R.drawable.sticker_star));
        list.add(new StickerItem("smile",    R.drawable.sticker_smile));
        list.add(new StickerItem("heart",    R.drawable.sticker_heart));
        list.add(new StickerItem("coffee",   R.drawable.sticker_coffee));
        list.add(new StickerItem("cat",      R.drawable.sticker_cat));
        list.add(new StickerItem("car",      R.drawable.sticker_car));
        return list;
    }

    private void sendSticker() {
//        int pos = spinnerRecipient.getSelectedItemPosition();
//        if (pos == 0) {
//            Toast.makeText(this, R.string.err_no_recipient, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String receiver = (String) spinnerRecipient.getSelectedItem();

        String receiver = dropdownRecipient.getText().toString().trim();
        if (receiver.isEmpty() || receiver.equals("No friends")) {
            Toast.makeText(this, R.string.err_no_recipient, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSticker == null) {
            Toast.makeText(this, R.string.err_no_sticker, Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        StickerMessage msg = new StickerMessage(
                currentUser,
                receiver,
                selectedSticker.getId(),
                timestamp
        );

        usersRef.child(currentUser)
                .child("sent")
                .push()
                .setValue(msg);

        usersRef.child(receiver)
                .child("received")
                .push()
                .setValue(msg);

        Toast.makeText(
                this,
                "Sent “" + selectedSticker.getId() + "” to " + receiver,
                Toast.LENGTH_SHORT
        ).show();

        selectedSticker = null;
        stickerAdapter.notifyDataSetChanged();
//        spinnerRecipient.setSelection(0);
        dropdownRecipient.setText("", false);
    }
}
