package com.example.ambrosiaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker currentLocationMarker;
    double latitude = 0;
    double longitude = 0;
    LatLng userLocation;
    DatabaseReference dbref1;
    //int proximityRadius = 10000;


    public void saveLocation(View view) {

        //showing Alert before saving
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_save)
                .setTitle("Are you sure?")
                .setMessage("Your current location will be marked as Ambrosia area.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //IF USER CLICKS "YES|
                        if (latitude!=0 && longitude!=0) {
                            //getting address information based on lat/long
                            String address = getAddress(latitude,longitude);

                            Map<String, String> values = new HashMap<>();
                            values.put("latitude",Double.toString(latitude));
                            values.put("longitude",Double.toString(longitude));
                            values.put("address",address);

                            //saving values in Firebase
                            dbref1.push().child("Locations").setValue(values, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError==null) {
                                        Toast.makeText(MapsActivity.this,"Saved successfully!",Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(MapsActivity.this,"Saving failed!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(MapsActivity.this,"No location data",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //if "no", then do nothing
                .setNegativeButton("No",null)
                .show();
    }

    //when user clicks on "My location" button, center camera on his/her location
    public void centerCameraOnLocation(View view){
        if(userLocation!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }



    public String getAddress(double latitude, double longitude){
        ///getting address details for the lat/long
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String addressInfo = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if (addressList!=null && addressList.size()>0) {
                if(addressList.get(0).getThoroughfare()!=null) {
                    addressInfo += addressList.get(0).getThoroughfare() + " ";
                }
                if(addressList.get(0).getSubThoroughfare()!=null){
                    addressInfo += addressList.get(0).getSubThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressInfo=="") {
            addressInfo = "Unknown address";
        }

        return addressInfo;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.survey:
                startActivity(new Intent(this, SurveyActivity.class));
                return true;
            case R.id.forecast:
                startActivity(new Intent(this, AlergyForecastActivity.class));
                return true;
            default:
                return false;
        }

    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //connecting to Firebase
        dbref1 = FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //get user new location if it is changed
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                currentLocationMarker.remove();
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        //requesting location access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //if app can't read last known location, use (0,0) as default
            LatLng userLocation = new LatLng(0,0);
            if (lastKnownLocation!=null)
                userLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));

            //adding fixed markers of known hospitals
            mMap.addMarker(new MarkerOptions().position(new LatLng(44.7983064,20.4590730)).
                    title("H: Klinički centar")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            latitude = userLocation.latitude;
            longitude = userLocation.longitude;
        }



        //reading data from firebase and adding markers on Map
        dbref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {
                //reading locations from firebase
                boolean toastShowed = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationInformation li = snapshot.getValue(LocationInformation.class);
                    LatLng knownLocation = new LatLng(li.getLatitude(), li.getLongitude());

                    String addressInfo = li.getAddress();

                    //adding marker on Map
                    mMap.addMarker(new MarkerOptions()
                            .position(knownLocation).title(addressInfo)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                     if (!toastShowed && distance(latitude,longitude,li.getLatitude(),li.getLongitude())<50) {
                        Toast.makeText(MapsActivity.this, "Ambrosia near your location!", Toast.LENGTH_SHORT).show();
                        toastShowed = true;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /** calculates the distance between two locations in KILOMETERS */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

    //if I can use Google Places API, I will use this fragment of code
    /*public void showHospitals(View v) {
        String hospital = "hospital";
        Object transferData [] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        String url = getUrl(latitude,longitude,"hospital");
        transferData[0] = mMap;
        transferData[1] = url;

        getNearbyPlaces.execute(transferData);
    }

    private String getUrl(double latitude, double longitude, String placeType){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + ","+longitude);
        googleURL.append("&radius="+proximityRadius);
        googleURL.append("&type="+placeType);
        googleURL.append("&sensor=true");
        googleURL.append("&key="+"AIzaSyBN52cNWUOUnL-itroDBp_2mXLw47VOVnM");
        Log.d("GoogleMapsActivity","url = "+googleURL.toString());
        return googleURL.toString();
    }*/
}
