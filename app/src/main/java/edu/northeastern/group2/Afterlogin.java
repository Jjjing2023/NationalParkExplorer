package edu.northeastern.group2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Afterlogin extends AppCompatActivity {
    private String currentUser;
    private DatabaseReference usersRef;
    private Spinner spinnerRecipient;
    private RecyclerView rvStickers;
    private StickerAdapter stickerAdapter;
    private StickerItem selectedSticker;
    private static final String SCREEN_NAME = "Stick It To Them";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);

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

        TextView header = findViewById(R.id.screenText);
        header.setText(SCREEN_NAME + " - Welcome " + currentUser);

        usersRef = FirebaseDatabase.getInstance()
                .getReference("users");

        spinnerRecipient = findViewById(R.id.spinner_recipient);
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
    }

    private void loadRecipients() {
        usersRef.get().addOnCompleteListener(task -> {
            List<String> names = new ArrayList<>();
            names.add(getString(R.string.select_recipient));

            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    String name = snap.getKey();
                    if (name != null && !name.equals(currentUser)) {
                        names.add(name);
                    }
                }
            }
            if (names.size() == 1) {
                names.add("No friends");
            }

            Log.d("Afterlogin", "Loaded recipients: " + names);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_hint_item,
                    names);
            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerRecipient.setAdapter(adapter);
            spinnerRecipient.setSelection(0);

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
        int pos = spinnerRecipient.getSelectedItemPosition();
        if (pos == 0) {
            Toast.makeText(this, R.string.err_no_recipient, Toast.LENGTH_SHORT).show();
            return;
        }
        String receiver = (String) spinnerRecipient.getSelectedItem();

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
        spinnerRecipient.setSelection(0);
    }
}
