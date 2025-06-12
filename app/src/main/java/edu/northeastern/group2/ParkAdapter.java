package edu.northeastern.group2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {

    private List<Park> parkList;

    public ParkAdapter(List<Park> parks) {
        this.parkList = parks;
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
    }

    @Override
    public int getItemCount() {
        return parkList == null ? 0 : parkList.size();
    }

    static class ParkViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, coordinatesText;

        public ParkViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.park_name);
            descriptionText = itemView.findViewById(R.id.park_description);
            coordinatesText = itemView.findViewById(R.id.park_coordinates);
        }
    }
}
