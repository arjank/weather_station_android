package com.arjankleene.weather_station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LocationManager locationManager;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location.hasAccuracy() && location.getAccuracy() < 100) {
                locationManager.removeUpdates(locationListener);
                Toast.makeText(getBaseContext(), "Found accurate location", Toast.LENGTH_LONG).show();
            }
            String msg = "New Latitude: "+location.getLatitude()+"New Longitude: "+location.getLongitude();
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            updateLocationCounter();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Required for the interface implementation.
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private Integer seenLocations = -1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateLocationCounter();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager = null;
    }

    private void updateLocationCounter() {
        TextView counter = (TextView) findViewById(R.id.counter);
        seenLocations++;
        counter.setText(seenLocations.toString());
    }
}
