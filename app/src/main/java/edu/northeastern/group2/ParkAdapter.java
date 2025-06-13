package edu.northeastern.group2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {
    private List<Park> parkList;
    private Handler handler;
    private Runnable loadingRunnable;
    private int dotCount = 0;

    public ParkAdapter(List<Park> parks) {
        this.parkList = parks;
        this.handler = new Handler(Looper.getMainLooper());
        startLoadingAnimation();
    }

    private void startLoadingAnimation() {
        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4;
                String dots = ".".repeat(dotCount);
                notifyDataSetChanged();
                handler.postDelayed(this, 500);
            }
        };
        handler.post(loadingRunnable);
    }

    public void updateParks(List<Park> newList) {
        this.parkList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_park, parent, false);
        return new ParkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {
        Park park = parkList.get(position);
        int number = position + 1;

        // Add number prefix to the name
        holder.nameText.setText(number + ". " + park.getName());
        holder.descriptionText.setText(park.getDescription());
        holder.coordinatesText.setText(park.getLatitude() + ", " + park.getLongitude());
        
        // Set weather information
        if (park.isLoading()) {
            String dots = ".".repeat(dotCount);
            holder.weatherText.setText("Loading" + dots);
        } else {
            String weatherText = park.getWeatherIconCode().isEmpty() ? 
                "Loading..." : 
                park.getWeatherIconCode() + " " + park.getTemperature();
            holder.weatherText.setText(weatherText);
        }
    }

    @Override
    public int getItemCount() {
        return parkList == null ? 0 : parkList.size();
    }

    static class ParkViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, coordinatesText, weatherText;

        public ParkViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.park_name);
            descriptionText = itemView.findViewById(R.id.park_description);
            coordinatesText = itemView.findViewById(R.id.park_coordinates);
            weatherText = itemView.findViewById(R.id.weather_info);
        }
    }
}
