package com.ani.anicab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    ListView requestListview;
    ArrayList<String> requests = new ArrayList<>();
    ArrayList<Double> requestLats = new ArrayList<>();
    ArrayList<Double> requestLngs = new ArrayList<>();
    ArrayList<String> requestUsernames = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    LocationManager locationManager;
    LocationListener locationListener;

    public void updateListview(Location location)
    {
        if(location!=null)
        {
            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

                final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                query.whereNear("location", geoPointLocation);
                query.whereDoesNotExist("driverUsername");
                query.setLimit(10);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {

                            requests.clear();
                            requestLngs.clear();
                            requestLats.clear();

                            if (objects.size() > 0) {

                                for (ParseObject object : objects) {

                                    ParseGeoPoint reqLocation = (ParseGeoPoint) object.get("location");

                                    if(reqLocation!=null){
                                        Double distance = geoPointLocation.distanceInKilometersTo(reqLocation);

                                        distance = (double) Math.round(distance * 10) / 10;

                                        requests.add(distance.toString() + " Km");
                                        requestLats.add(reqLocation.getLatitude());
                                        requestLngs.add(reqLocation.getLongitude());
                                        requestUsernames.add(object.getString("username"));
                                    }

                                }
                            } else {

                                requests.add("Sorry! No requests at this time");
                            }

                            arrayAdapter.notifyDataSetChanged();


                        }
                    }
                });
            }catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


    }


    public void logOut(final View view) {

        ParseUser.getCurrentUser().put("RiderOrDriver", "");
        ParseUser.getCurrentUser().saveInBackground();

        ParseUser.logOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null)
                        updateListview(lastKnownLocation);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        requestListview = findViewById(R.id.requestListview);
        requests.add("Getting Nearby Location");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requests);
        requestListview.setAdapter(arrayAdapter);

        requestListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ViewRequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (requestLats.size() > position && requestLngs.size() > position && requestUsernames.size() > position && lastKnownLocation != null) {
                        Intent intent = new Intent(getApplicationContext(), DriverLocationActivity.class);
                        intent.putExtra("reqLat", requestLats.get(position));
                        intent.putExtra("reqLng", requestLngs.get(position));
                        intent.putExtra("driverLat", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLng", lastKnownLocation.getLongitude());
                        intent.putExtra("username", requestUsernames.get(position));

                        startActivity(intent);

                    }
                }

             }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateListview(location);

                ParseUser.getCurrentUser().put("driverLocation",
                        new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

                ParseUser.getCurrentUser().saveInBackground();

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


        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0,locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation!= null)
                updateListview(lastKnownLocation);

        }
    }
}
