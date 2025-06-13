package edu.northeastern.group2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ParkSearchActivity extends AppCompatActivity {
    private static final String TAG = "ParkSearchActivity";
    private ParkAdapter parkAdapter;
    private RecyclerView parkRecyclerView;
    private List<Park> parkList = new ArrayList<>();
    private ExecutorService executorService;
    private static final String PARK_LIST_KEY = "PARK_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_park_search);
        executorService = Executors.newFixedThreadPool(5);

        // Handle insets
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
            fetchParks(stateCode);
        });

        parkRecyclerView = findViewById(R.id.park_recycler_view);
        parkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        parkAdapter = new ParkAdapter(parkList);
        parkRecyclerView.setAdapter(parkAdapter);

        // Restore park list if available
        if (savedInstanceState != null && savedInstanceState.containsKey(PARK_LIST_KEY)) {
            parkList = savedInstanceState.getParcelableArrayList(PARK_LIST_KEY);
            parkAdapter.updateParks(parkList);
        }
    }

    private void fetchParks(String stateCode) {
        String apiKey = BuildConfig.NPS_API_KEY;
        String urlStr = "https://developer.nps.gov/api/v1/parks?stateCode=" + stateCode + "&limit=500&api_key=" + apiKey;

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

                List<Park> tempList = new ArrayList<>();
                for (int i = 0; i < parks.length(); i++) {
                    JSONObject park = parks.getJSONObject(i);
                    String name = park.getString("fullName");
                    String description = park.getString("description");
                    String lat = park.getString("latitude");
                    String lon = park.getString("longitude");

                    Park newPark = new Park(name, description, lat, lon);
                    tempList.add(newPark);
                    fetchWeatherForPark(newPark);
                }

                runOnUiThread(() -> {
                    parkList = tempList;
                    parkAdapter.updateParks(parkList);
                    Snackbar.make(findViewById(R.id.park_search_layout),
                            "Found " + parkList.size() + " parks", Snackbar.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Snackbar.make(findViewById(R.id.park_search_layout),
                                "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void fetchWeatherForPark(Park park) {
        executorService.execute(() -> {
            try {
                String apiKey = BuildConfig.WEATHER_API_KEY;
                String urlStr = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%s,%s&aqi=no",
                        apiKey, park.getLatitude(), park.getLongitude());
                
                Log.d(TAG, "Fetching weather for " + park.getName() + " with URL: " + urlStr);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Weather API response code for " + park.getName() + ": " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "Weather API response for " + park.getName() + ": " + response.toString());

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject current = jsonObject.getJSONObject("current");
                    JSONObject condition = current.getJSONObject("condition");

                    String iconCode = condition.getString("icon");
                    double temp = current.getDouble("temp_f");
                    String tempStr = String.format("%.0f", temp);

                    // Convert WeatherAPI icon code to emoji
                    String weatherEmoji = convertWeatherIconToEmoji(iconCode);

                    runOnUiThread(() -> {
                        park.setWeatherInfo(weatherEmoji, tempStr);
                        parkAdapter.notifyDataSetChanged();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.e(TAG, "API key is invalid or not activated yet for " + park.getName());
                    runOnUiThread(() -> {
                        park.setWeatherInfo("⏳", "Activating...");
                        parkAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e(TAG, "Error fetching weather for " + park.getName() + ": " + responseCode);
                    runOnUiThread(() -> {
                        park.setWeatherInfo("❌", "Failed");
                        parkAdapter.notifyDataSetChanged();
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching weather for " + park.getName(), e);
                runOnUiThread(() -> {
                    park.setWeatherInfo("❌", "Failed");
                    parkAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private String convertWeatherIconToEmoji(String iconUrl) {
        if (iconUrl.contains("sun") || iconUrl.contains("113")) return "☀️";
        if (iconUrl.contains("cloud") || iconUrl.contains("116") || iconUrl.contains("119")) return "☁️";
        if (iconUrl.contains("rain") || iconUrl.contains("296") || iconUrl.contains("308")) return "🌧️";
        if (iconUrl.contains("thunder") || iconUrl.contains("389")) return "⛈️";
        if (iconUrl.contains("snow") || iconUrl.contains("338")) return "🌨️";
        if (iconUrl.contains("mist") || iconUrl.contains("143")) return "🌫️";
        return "❓";
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (parkList != null && !parkList.isEmpty()) {
            outState.putParcelableArrayList(PARK_LIST_KEY, new ArrayList<>(parkList));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
