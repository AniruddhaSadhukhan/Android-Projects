package com.ani.anicab;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent intent;

    public void acceptRequest(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        objects.get(0).put("driverUsername", ParseUser.getCurrentUser().getUsername());
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null)
                                {
                                    String url = "http://maps.google.com/maps?saddr="
                                            +intent.getDoubleExtra("driverLat",0)+","
                                            +intent.getDoubleExtra("driverLng",0)+"&daddr="
                                            +intent.getDoubleExtra("reqLat",0)+","
                                            +intent.getDoubleExtra("reqLng",0);

                                    Intent drivingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(drivingIntent);
                                }else
                                {
                                    Toast.makeText(DriverLocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(DriverLocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlocation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ConstraintLayout mapLayout = findViewById(R.id.mapLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                LatLng driverLoc = new LatLng(intent.getDoubleExtra("driverLat",0),intent.getDoubleExtra("driverLng",0));
                LatLng requestLoc = new LatLng(intent.getDoubleExtra("reqLat",0),intent.getDoubleExtra("reqLng",0));

                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(mMap.addMarker(new MarkerOptions().position(driverLoc).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                markers.add(mMap.addMarker(new MarkerOptions().position(requestLoc).title("Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(Marker marker: markers)
                {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 60;
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,padding));

            }
        });

        }
}
