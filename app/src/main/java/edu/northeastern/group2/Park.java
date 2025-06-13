package edu.northeastern.group2;

import android.os.Parcel;
import android.os.Parcelable;

public class Park implements Parcelable {
    private String name;
    private String description;
    private String latitude;
    private String longitude;
    private String weatherIconCode;
    private String temperature;
    private boolean isLoading;

    public Park(String name, String description, String latitude, String longitude){
        this.name = name;
        this.description = description;
        // latitude and longitude will be used to call weather api and retrieve weather icon later
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherIconCode = "";
        this.temperature = "";
        this.isLoading = true;
    }

    protected Park(Parcel in) {
        name = in.readString();
        description = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        weatherIconCode = in.readString();
        temperature = in.readString();
        isLoading = in.readByte() != 0;
    }

    public static final Creator<Park> CREATOR = new Creator<Park>() {
        @Override
        public Park createFromParcel(Parcel in) {
            return new Park(in);
        }

        @Override
        public Park[] newArray(int size) {
            return new Park[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(weatherIconCode);
        parcel.writeString(temperature);
        parcel.writeByte((byte) (isLoading ? 1 : 0));
    }

    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public String getLatitude(){ return latitude; }
    public String getLongitude(){ return longitude; }
    public String getWeatherIconCode() { return weatherIconCode; }
    public String getTemperature() { return temperature; }
    public boolean isLoading() { return isLoading; }
    
    public void setWeatherInfo(String iconCode, String temp) {
        this.weatherIconCode = iconCode;
        this.temperature = temp;
        this.isLoading = false;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
