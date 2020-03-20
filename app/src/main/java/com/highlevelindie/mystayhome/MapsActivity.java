package com.highlevelindie.mystayhome;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highlevelindie.mystayhome.model.Location;
import com.highlevelindie.mystayhome.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = new User();
                        try {
                            user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        }catch(Exception e){
                            user = new User();
                            user.setCity("Madrid");
                        }finally {
                            try {
                                usrChords = new LatLng(user.getLatitude(), user.getLongitude());
                            }catch (NullPointerException e){
                                Toast.makeText(getApplicationContext(),
                                        "Se ha producido un error" +
                                        "al recuperar la localización archivada, mandando a Madrid",
                                        Toast.LENGTH_LONG).show();
                                usrChords = new LatLng(40.4165001,-3.7025599);
                            }finally {
                                getMarkers();
                                setCamera();
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
