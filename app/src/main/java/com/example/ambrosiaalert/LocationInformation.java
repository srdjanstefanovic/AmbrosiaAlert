package com.example.ambrosiaalert;

//this class is used for reading values from Firebase in MapsActivity (onChildAdded)
public class LocationInformation {

    String latitude;
    String longitude;
    String address;

    public double getLatitude() {
        return Double.parseDouble(latitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return Double.parseDouble(longitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocationInformation(){

    }

    public LocationInformation(String latitude, String longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
