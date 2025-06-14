package edu.northeastern.group2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;

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
    private ProgressBar loadingSpinner;
    private List<Park> parkList = new ArrayList<>();
    private ExecutorService executorService;
    private static final String PARK_LIST_KEY = "PARK_LIST";
    private Handler handler;
    private Runnable loadingRunnable;
    private TextView loadingText;
    private int dotCount = 0;

    private static final String[] VALID_STATE_CODES = {
        "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
        "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
        "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
        "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
        "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
        "DC" 
    };

    private boolean isValidStateCode(String stateCode) {
        for (String validCode : VALID_STATE_CODES) {
            if (validCode.equals(stateCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting ParkSearchActivity");
        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_park_search);
            executorService = Executors.newFixedThreadPool(5);
            handler = new Handler(Looper.getMainLooper());
            Log.d(TAG, "onCreate: Basic setup completed");

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.park_search_layout), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            EditText stateCodeInput = findViewById(R.id.state_code_input);
            Button searchButton = findViewById(R.id.search_button);
            loadingSpinner = findViewById(R.id.loading_spinner);

            searchButton.setOnClickListener(v -> {
                String stateCode = stateCodeInput.getText().toString().trim().toUpperCase();
                Log.d(TAG, "Search button clicked for state code: " + stateCode);
                if (stateCode.isEmpty()) {
                    Snackbar.make(v, "Please enter a state code.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidStateCode(stateCode)) {
                    Snackbar.make(v, "Please enter a valid US state code (e.g., CA, NY, TX).", Snackbar.LENGTH_LONG).show();
                    return;
                }

                startLoadingAnimation(stateCode);
                fetchParks(stateCode);
            });

            parkRecyclerView = findViewById(R.id.park_recycler_view);
            parkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            parkAdapter = new ParkAdapter(parkList);
            parkRecyclerView.setAdapter(parkAdapter);
            Log.d(TAG, "onCreate: RecyclerView and Adapter initialized");

            // Restore park list if available
            if (savedInstanceState != null && savedInstanceState.containsKey(PARK_LIST_KEY)) {
                parkList = savedInstanceState.getParcelableArrayList(PARK_LIST_KEY);
                parkAdapter.updateParks(parkList);
                Log.d(TAG, "onCreate: Restored " + parkList.size() + " parks from saved state");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            throw e;
        }
    }

    private void startLoadingAnimation(String stateCode) {
        loadingSpinner.setVisibility(View.VISIBLE);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.park_search_layout), "", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setText("Searching parks in " + stateCode);
        snackbar.show();

        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4;
                String dots = ".".repeat(dotCount);
                textView.setText("Searching parks in " + stateCode + dots);
                handler.postDelayed(this, 500); 
            }
        };
        handler.post(loadingRunnable);
    }

    private void stopLoadingAnimation() {
        loadingSpinner.setVisibility(View.GONE);
        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }
    }

    private void fetchParks(String stateCode) {
        Log.d(TAG, "fetchParks: Starting fetch for state code: " + stateCode);
        String apiKey = BuildConfig.NPS_API_KEY;
        String urlStr = "https://developer.nps.gov/api/v1/parks?stateCode=" + stateCode + "&limit=500&api_key=" + apiKey;
        Log.d(TAG, "fetchParks: NPS API URL: " + urlStr);

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                Log.d(TAG, "fetchParks: Making NPS API request");

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
                Log.d(TAG, "fetchParks: Received " + parks.length() + " parks from NPS API");

                List<Park> tempList = new ArrayList<>();
                for (int i = 0; i < parks.length(); i++) {
                    JSONObject park = parks.getJSONObject(i);
                    String name = park.getString("fullName");
                    String description = park.getString("description");
                    String lat = park.getString("latitude");
                    String lon = park.getString("longitude");

                    Park newPark = new Park(name, description, lat, lon);
                    tempList.add(newPark);
                    Log.d(TAG, "fetchParks: Adding park: " + name);
                    fetchWeatherForPark(newPark);
                }

                runOnUiThread(() -> {
                    parkList = tempList;
                    parkAdapter.updateParks(parkList);
                    Log.d(TAG, "fetchParks: Updated UI with " + parkList.size() + " parks");
                    stopLoadingAnimation();

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                
                    Snackbar.make(findViewById(R.id.park_search_layout),
                            "Found " + parkList.size() + " parks", Snackbar.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e(TAG, "fetchParks: Error fetching parks", e);
                runOnUiThread(() -> {
                    stopLoadingAnimation();
                    Snackbar.make(findViewById(R.id.park_search_layout),
                            "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
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
                    int conditionCode = condition.getInt("code");
                    String conditionText = condition.getString("text");
                    Log.d(TAG, "Weather condition code: " + conditionCode + ", text: " + conditionText);
                    
                    double tempF = current.getDouble("temp_f");
                    String weatherEmoji = WeatherIconMapper.getEmojiByCode(conditionCode);

                    runOnUiThread(() -> {
                        park.setWeatherInfo(weatherEmoji, String.format("%.1f°F", tempF));
                        parkAdapter.notifyDataSetChanged();
                    });
                } else {
                    String errorMessage = "Error code: " + responseCode;
                    try {
                        InputStream errorStream = conn.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder errorResponse = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                errorResponse.append(line);
                            }
                            reader.close();
                            errorMessage += ", Response: " + errorResponse.toString();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error stream", e);
                    }
                    
                    Log.e(TAG, "Weather API error for " + park.getName() + ": " + errorMessage);
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
        stopLoadingAnimation();
        executorService.shutdown();
    }
}
