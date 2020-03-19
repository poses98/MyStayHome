package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highlevelindie.mystayhome.model.User;

import java.io.IOException;
import java.util.ArrayList;

public class CreateUser extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView log;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        user = new User();

        log = findViewById(R.id.userLog);
        log.setText(R.string.creatingUser);
        createUser();


    }

    private void createUser() {
        log.setText(R.string.createInit);
        String nickname = this.getIntent().getStringExtra("nick");
        int gender = this.getIntent().getIntExtra("gender", 2);
        int age = this.getIntent().getIntExtra("age", 18);

        user.setNickname(nickname);
        user.setGender(gender);
        user.setAge(age);

        getLocation();
    }

    private void getLocation() {
        log.setText(R.string.createLocation);

        fusedLocationClient.getLastLocation().addOnSuccessListener(CreateUser.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    ArrayList<Address> addresses = new ArrayList<Address>();
                    Geocoder geocoder1 = new Geocoder(CreateUser.this);
                    try {
                        addresses = (ArrayList<Address>) geocoder1.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Log.d("TAG", "fallo aqui: ");

                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            user.setLatitude(address.getLatitude());
                            user.setLongitude(address.getLongitude());
                            user.setCity(address.getLocality());
                            insertIntoDatabase();
                            Log.d("TAG", "" + address.getAddressLine(0) + ", " + address.getLocality());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void insertIntoDatabase() {
        log.setText(R.string.createData);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("users").
                document(mAuth.getCurrentUser().getUid()).
                set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                insertLocation();

            }
        });
    }

    private void insertLocation() {
        log.setText(R.string.createMap);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        com.highlevelindie.mystayhome.model.Location location = new com.highlevelindie.mystayhome.model.Location();
        location.setLatitude(user.getLatitude());
        location.setLongitude(user.getLongitude());
        location.setNick(user.getNickname());
        db.collection(user.getCity() + " Location").
                document(mAuth.getCurrentUser().getUid()).
                set(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                log.setText(R.string.createCheck);
            }
        });

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();

        mData.child("GeneralLocations").
                child(mAuth.getCurrentUser().getUid()).
                setValue(location).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        log.setText(R.string.createReady);
                    }
                });
        mData.child(user.getCity()).child(mAuth.getCurrentUser().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }
}
