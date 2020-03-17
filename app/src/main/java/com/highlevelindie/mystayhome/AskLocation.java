package com.highlevelindie.mystayhome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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


/*

 */