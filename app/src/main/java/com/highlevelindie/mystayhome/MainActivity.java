package com.highlevelindie.mystayhome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private int permisos;
    private Button signIn;
    private TextView tvPolitics;
    private boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        tvPolitics = findViewById(R.id.politica);
        tvPolitics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.highlevelindie.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        signIn = findViewById(R.id.askPermission);
        signIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, permisos);
        }
        // SESSION
        if (this.getIntent().getBooleanExtra("isNull", false) == false) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(this, MainMenu.class));
            }
        } else {
            error = true;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (error) {
            showHelp(MainActivity.class, "HA OCURRIDO UN ERROR", R.string.nullError);
        }
    }

    @Override
    public void onClick(View v) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            authInFirebase();
        }else{
            Log.d("TAG", "onClick: continuando con el usuario creado");
            Intent intent = new Intent(getApplicationContext(), Gender_selection.class);
            startActivity(intent);
            finish();
        }
    }

    private void authInFirebase() {
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), Gender_selection.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("login", "onComplete: " + task.getResult());
                }
            }
        });
    }

    private void showHelp(final Class target, String title, int message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("HECHO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
