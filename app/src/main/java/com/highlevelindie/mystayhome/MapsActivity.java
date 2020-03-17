package com.highlevelindie.mystayhome;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.highlevelindie.mystayhome.model.Location;

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng usrChords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ImageButton back = findViewById(R.id.backbutton);
        back.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        setLocation();
        //setDemoMarkers();
    }

    /**
     * Sets the current latitude and longitude
     */
    private void setLocation() {
        Log.d("TAG", "setLocation: ");
        fusedLocationClient.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                Log.d("TAG", "onSuccess: ");
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    ArrayList<Address> addresses = new ArrayList<Address>();
                    Geocoder geocoder1 = new Geocoder(MapsActivity.this);
                    try {
                        addresses = (ArrayList<Address>) geocoder1.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            usrChords = new LatLng(address.getLatitude(), address.getLongitude());
                            getMarkers();
                            setCamera();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * Sets the camera zoom to the user latitude and longitude
     */
    private void setCamera() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usrChords, 14));
    }


    private void getMarkers() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("GeneralLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Location location = data.getValue(Location.class);
                    mMap.addMarker(new MarkerOptions().
                            position(new LatLng(location.getLatitude(), location.getLongitude())).
                            title(location.getNick()).
                            snippet(location.getNick() + " está en casa")).
                            setIcon(BitmapDescriptorFactory.
                                    defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
