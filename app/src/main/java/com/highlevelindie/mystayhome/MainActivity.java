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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private int permisos;
    private Button signIn;
    private TextView tvPolitics;
    private boolean error = false;
    private int APP_VERSION;
    private CheckBox checkBox;
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
        tvPolitics.setVisibility(View.INVISIBLE);

        checkBox = findViewById(R.id.checkBox);
        checkBox.setVisibility(View.INVISIBLE);

        signIn = findViewById(R.id.askPermission);
        signIn.setVisibility(View.INVISIBLE);
        checkVersion();
    }

    private void checkVersion() {
        APP_VERSION = BuildConfig.VERSION_CODE;
        Log.d("TAG", "checkVersion: " + APP_VERSION);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String version = dataSnapshot.getValue().toString();
                if (APP_VERSION != Integer.parseInt(version)) {
                    showVersionPopUp();
                } else {
                    startApp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showVersionPopUp() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("NUEVA VERSIÓN DISPONIBLE")
                .setMessage("EXISTE UNA NUEVA VERSIÓN DE LA APLICACIÓN QUE INCORPORA MEJORAS PARA LA " +
                        "APP, PARA SEGUIR USÁNDOLA ACTUALIZA POR FAVOR")
                .setCancelable(false)
                .setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.highlevelindie.mystayhome"); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    }

    private void startApp() {
        // SESSION
        if (this.getIntent().getBooleanExtra("isNull", false) == false) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(this, MainMenu.class));
                finish();
            } else {
                signIn.setOnClickListener(MainActivity.this);
                signIn.setVisibility(View.VISIBLE);
                tvPolitics.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);

            }
        } else {
            error = true;
            signIn.setOnClickListener(MainActivity.this);
            signIn.setVisibility(View.VISIBLE);
            tvPolitics.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
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

        if (checkBox.isChecked()) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                authInFirebase();
            } else {
                Intent intent = new Intent(getApplicationContext(), Gender_selection.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Debes aceptar las póliticas de privacidad", Toast.LENGTH_LONG).show();
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
