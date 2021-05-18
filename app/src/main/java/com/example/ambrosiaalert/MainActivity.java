package com.example.ambrosiaalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView txtInformation;
    TextView txtSymptoms;
    TextView txtTreatment;


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
            case R.id.map:
                startActivity(new Intent(this, MapsActivity.class));
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


    public void showInformation(View view) {
        if (txtInformation.getVisibility()==View.GONE) {
            txtInformation.setVisibility(View.VISIBLE);
            txtSymptoms.setVisibility(View.GONE);
            txtTreatment.setVisibility(View.GONE);
        }
        else {
            txtInformation.setVisibility(View.GONE);
        }
    }

    public void showSymptoms(View view){
        if (txtSymptoms.getVisibility()==View.GONE) {
            txtSymptoms.setVisibility(View.VISIBLE);
            txtInformation.setVisibility(View.GONE);
            txtTreatment.setVisibility(View.GONE);
        }
        else {
            txtSymptoms.setVisibility(View.GONE);
        }
    }

    public void showTreatment(View view) {
        if (txtTreatment.getVisibility()==View.GONE){
            txtTreatment.setVisibility(View.VISIBLE);
            txtInformation.setVisibility(View.GONE);
            txtSymptoms.setVisibility(View.GONE);
        } else {
            txtTreatment.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        viewPager.setAdapter(imageAdapter);

        txtInformation = findViewById(R.id.txtInformation);
        txtSymptoms = findViewById(R.id.txtSymptoms);
        txtTreatment = findViewById(R.id.txtTreatment);

        //requesting location access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

    }
}
