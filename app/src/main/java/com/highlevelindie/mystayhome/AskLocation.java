package com.highlevelindie.mystayhome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AskLocation extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;


    private int permisos;
    private Button askLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_location);


        askLocation = findViewById(R.id.askPermission);
        askLocation.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ((ContextCompat.checkSelfPermission(AskLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AskLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, permisos);
        }
    }

    @Override
    public void onClick(View v) {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showHelp(getClass());
        } else {

            // PERMISOS
            if ((ContextCompat.checkSelfPermission(AskLocation.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AskLocation.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        permisos);
            }
            Intent intent = new Intent(getApplicationContext(), CreateUser.class);

            String nickname = this.getIntent().getStringExtra("nick");
            int gender = this.getIntent().getIntExtra("gender", 2);
            int age = this.getIntent().getIntExtra("age", 18);
            intent.putExtra("nick", nickname);
            intent.putExtra("gender", gender);
            intent.putExtra("age", age);
            startActivity(intent);
            finish();
        }

    }

    private void showHelp(final Class target) {


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("NECESITAMOS QUE ACTIVES EL GPS")
                .setMessage("Para el correcto uso de la aplicación te pedimos que actives el GPS," +
                        " sólo esta vez para situar tu pin en el mapa.")
                .setCancelable(true)
                .setPositiveButton("VALE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i);
                    }
                }).setNegativeButton("NO GRACIAS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}


/*

 */