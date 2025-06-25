package edu.northeastern.group2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private SharedPreferences preferences;
    private DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        usernameInput = findViewById(R.id.usernameInput);
        preferences = getSharedPreferences("StickerAppPrefs", MODE_PRIVATE);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            if (!username.isEmpty()) {
                saveUsernameLocally(username);
                registerUserInFirebase(username);
                goToMainActivity();
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUsernameLocally(String username) {
        preferences.edit().putString("currentUser", username).apply();
    }

    private void registerUserInFirebase(String username) {
        usersRef.child(username).get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                usersRef.child(username).setValue(true);
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, Afterlogin.class);
        startActivity(intent);
        finish();
    }
}
