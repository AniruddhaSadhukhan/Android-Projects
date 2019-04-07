package com.ani.anicab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    Boolean requestActive = false;
    Boolean driverActive = false;

    Handler handler = new Handler();



    public void logOut(final View view) {

        ParseUser.getCurrentUser().put("RiderOrDriver", "");
        ParseUser.getCurrentUser().saveInBackground();

        ParseUser.logOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void callCab(final View view) {

        if (requestActive) {

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e==null)
                    {
                        if(objects.size()>0)
                        {
                            for(ParseObject object: objects)
                            {
                                object.deleteInBackground();
                            }
                            requestActive = false;
                            ((Button) findViewById(R.id.callCabButton)).setText("Call a Cab");
                        }
                    }
                    else
                    {
                        Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    ParseObject request = new ParseObject("Request");

                    request.put("username", ParseUser.getCurrentUser().getUsername());

                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    request.put("location", parseGeoPoint);

                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(RiderActivity.this, "Called a Cab ", Toast.LENGTH_SHORT).show();

                                ((Button) view).setText("Cancel Cab");

                                requestActive = true;

                                checkForDriverUpdates();

                            } else
                                Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


                } else {
                    Toast.makeText(this, "Could not get location...Please try again later ", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void checkForDriverUpdates() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.whereExists("driverUsername");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e==null)
                {

                    if(objects.size()>0)
                    {
                        driverActive = true;

                        ParseQuery<ParseUser> queryDriver = ParseUser.getQuery();
                        queryDriver.whereEqualTo("username", objects.get(0).getString("driverUsername"));
                        queryDriver.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {

                                if(e==null && objects.size()>0)
                                {
                                    final ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("driverLocation");

                                    if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        final Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                        if (lastKnownLocation!= null)
                                        {
                                            ParseGeoPoint riderLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                                            Double distance = driverLocation.distanceInKilometersTo(riderLocation);

                                            distance = (double) Math.round(distance * 10) / 10;

                                            if(distance < 0.01)
                                            {
                                                ((TextView) findViewById(R.id.driverStatusTextView)).setText("Your Driver is here");

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                                                query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                        if(e==null && objects.size()>0)
                                                        {
                                                            for(ParseObject object: objects)
                                                            {
                                                                object.deleteInBackground();
                                                            }
                                                        }
                                                    }
                                                });

                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        ((TextView) findViewById(R.id.driverStatusTextView)).setText("");


                                                        ((Button) findViewById(R.id.callCabButton)).setVisibility(View. VISIBLE);
                                                        ((Button) findViewById(R.id.callCabButton)).setText("Call a Cab");

                                                        requestActive = false;
                                                        driverActive = false;

                                                        updateMap(lastKnownLocation);


                                                    }
                                                },5000);

                                            }else
                                            {

                                                ((TextView) findViewById(R.id.driverStatusTextView)).setText("Your Driver is "+distance.toString() + " Km away");

                                                LatLng driverLoc = new LatLng(driverLocation.getLatitude(),driverLocation.getLongitude());
                                                LatLng requestLoc = new LatLng(riderLocation.getLatitude(),riderLocation.getLongitude());

                                                ArrayList<Marker> markers = new ArrayList<>();

                                                mMap.clear();

                                                markers.add(mMap.addMarker(new MarkerOptions().position(driverLoc).title("Driver Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                                                markers.add(mMap.addMarker(new MarkerOptions().position(requestLoc).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));


                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                for(Marker marker: markers)
                                                {
                                                    builder.include(marker.getPosition());
                                                }
                                                LatLngBounds bounds = builder.build();

                                                int padding = 60;
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,padding));

                                                ((Button) findViewById(R.id.callCabButton)).setVisibility(View.INVISIBLE);


                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        checkForDriverUpdates();

                                                    }
                                                },2000);
                                            }

                                        }
                                    }
                                }

                            }
                        });


                    }else
                    {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                checkForDriverUpdates();

                            }
                        },2000);
                    }


                }else
                {
                    Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void updateMap(Location location)
    {
        if(!driverActive)
        {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }

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
                        updateMap(lastKnownLocation);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        requestActive = true;
                        ((Button) findViewById(R.id.callCabButton)).setText("Cancel Cab");

                        checkForDriverUpdates();
                    }
                }
                else
                {
                    Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateMap(location);

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
                updateMap(lastKnownLocation);

        }

    }
}
