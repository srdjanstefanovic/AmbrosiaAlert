package com.example.ambrosiaalert;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

//this class will be used, if there is access to Google Places API
public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    String googlePlaceData, url;
    GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String)objects[1];

        try {
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlaceData = downloadUrl.readTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    private void displayNearbyPlaces(List<HashMap<String,String>> nearbyPlaceList) {
        for (int i=0; i<nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearByPlace = nearbyPlaceList.get(i);
            String nameOfPlace = googleNearByPlace.get("place_name");
            String vicinity = googleNearByPlace.get("vicinity");
            double lat = Double.parseDouble(googleNearByPlace.get("lat"));
            double lng = Double.parseDouble(googleNearByPlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace+" : "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlaceList = null;
        DataParser dataParser = new DataParser();
        nearbyPlaceList = dataParser.parse(s);

        displayNearbyPlaces(nearbyPlaceList);
    }
}
