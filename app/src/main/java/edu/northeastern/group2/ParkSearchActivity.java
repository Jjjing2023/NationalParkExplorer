package edu.northeastern.group2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
                Snackbar.make(v, "Please enter a state code.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Snackbar.make(v, "Searching parks in " + stateCode, Snackbar.LENGTH_SHORT).show();

            //call fetch method
            fetchParks(stateCode);

        });
    }

    private void fetchParks(String stateCode) {
        // Apikey hided inside local properties
        String apiKey = BuildConfig.NPS_API_KEY;

        String urlStr = "https://developer.nps.gov/api/v1/parks?stateCode=" + stateCode + "&limit=500&api_key=" + apiKey;

        // communicate with api
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray parks = jsonObject.getJSONArray("data");

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parks.length(); i++) {
                    JSONObject park = parks.getJSONObject(i);
                    String name = park.getString("fullName");
                    String description = park.getString("description");
                    String lat = park.getString("latitude");
                    String lon = park.getString("longitude");

                    result.append("Name: ").append(name).append("\n")
                            .append("Description: ").append(description).append("\n")
                            .append("Coordinates: ").append(lat).append(", ").append(lon).append("\n\n");
                }

                // Update UI on main thread, later on this will be displayed with recyclerview
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(R.id.park_search_layout), "Found " + parks.length() + " parks", Snackbar.LENGTH_SHORT).show();
                    System.out.println(result);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Snackbar.make(findViewById(R.id.park_search_layout), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show()
                );
            }
        }).start();
    }

}