package edu.northeastern.group2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ParkSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_park_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.park_search_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText stateCodeInput = findViewById(R.id.state_code_input);
        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String stateCode = stateCodeInput.getText().toString().trim().toUpperCase();
            if (stateCode.isEmpty()) {
                Toast.makeText(this, "Please enter a state code.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Searching parks in " + stateCode, Toast.LENGTH_SHORT).show();
        });

    }
}