package com.arjankleene.weather_station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.Manifest;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.arjankleene.astro.SolarInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class MainActivity extends Activity {

    private LocationManager locationManager;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location.hasAccuracy() && location.getAccuracy() < 100) {
                stopLocationUpdates();
                Toast.makeText(getBaseContext(), "Found accurate location", Toast.LENGTH_SHORT).show();
                hideProgressIndicator();
                displayTimes(location);
            }
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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        clearTimes();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            showProgressIndicator();
            Toast.makeText(getBaseContext(), "Looking for current location", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 5, locationListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void displayTimes(Location location) {
        SolarInfo info = new SolarInfo(new GregorianCalendar(), location);

        TextView sunriseTime = (TextView) findViewById(R.id.sunriseTime);
        TextView sunsetTime = (TextView) findViewById(R.id.sunsetTime);

        GregorianCalendar sunrise = info.getSunRise();
        displayTimeInField(sunrise, sunriseTime);

        GregorianCalendar sunset = info.getSunSet();
        displayTimeInField(sunset, sunsetTime);
    }

    private void displayTimeInField(GregorianCalendar calendar, TextView txtView) {
        if (calendar != null) {
            txtView.setText(formatTime(calendar));
        } else {
            txtView.setText("");
        }
    }

    private void stopLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void showProgressIndicator() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void clearTimes() {
        TextView sunriseTime = (TextView) findViewById(R.id.sunriseTime);
        TextView sunsetTime = (TextView) findViewById(R.id.sunsetTime);

        sunriseTime.setText("");
        sunsetTime.setText("");
    }

    private static String formatTime(GregorianCalendar calendar){
        return formatTime(calendar, DateFormat.SHORT);
    }

    private static String formatTime(GregorianCalendar calendar, int style){
        DateFormat fmt = SimpleDateFormat.getTimeInstance(style);
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());
        return dateFormatted;
    }
}
