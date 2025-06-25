package edu.northeastern.group2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Afterlogin extends AppCompatActivity {

    private static final String GROUP_NAME = "Group FireStickers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);

        SharedPreferences preferences = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);
        String currentUser = preferences.getString("currentUser", "Guest");

        TextView groupNameText = findViewById(R.id.groupNameText);
        groupNameText.setText(GROUP_NAME + " - Welcome " + currentUser);
    }
}
