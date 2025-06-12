package edu.northeastern.group2;

public class Park {
    private String name;
    private String description;
    private String latitude;
    private String longitude;

    public Park(String name, String description, String latitude, String longitude){
        this.name = name;
        this.description = description;

        // latitude and longitude will be used to call weather api and retrieve weather icon later
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){return name;}
    public String getDescription(){return description;}
    public String getLatitude(){return latitude;}
    public String getLongitude(){return longitude;}
}
