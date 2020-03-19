package com.highlevelindie.mystayhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highlevelindie.mystayhome.model.User;

import java.util.Objects;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private User user;
    private TextView general, city;
    private int permisos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAuth = FirebaseAuth.getInstance();

        ImageButton clapButton = findViewById(R.id.btnClap);
        clapButton.setOnClickListener(this);

        ImageButton mapButton = findViewById(R.id.btnMap);
        mapButton.setOnClickListener(this);

        ImageButton helpButton = findViewById(R.id.btnHelp);
        helpButton.setOnClickListener(this);

        ImageButton profileButton = findViewById(R.id.btnProf);
        profileButton.setOnClickListener(this);

        general = findViewById(R.id.contGeneral);
        city = findViewById(R.id.contCiudad);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUser();
    }

    private void getUser() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        }catch(Exception e){
                            user = new User();
                            user.setCity("Madrid");
                        }finally {
                            getCount();
                        }

                    }
                });
    }

    private void getCount() {
        mReference.child("GeneralLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                general.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String cityStr = "Madrid";
        if(user != null) {
            cityStr = user.getCity();
        }

        mReference.child(cityStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                city.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnClap:
                intent = new Intent(getApplicationContext(), SendClapScreen.class);
                break;
            case R.id.btnMap:
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                break;
            case R.id.btnHelp:
                showHelp(MainMenu.class);
                break;
            case R.id.btnProf:
                intent = new Intent(getApplicationContext(), Profile.class);
        }
        if (intent != null) {
            startActivity(intent);
        }

    }

    private void showHelp(final Class target) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("AYUDA")
                .setMessage("Utiliza el botón de aplausos para mandar ánimos al personal sanitario.\n\n" +
                        "Con el botón de mapa podrás ver un mapa en tiempo real de los usuarios que se" +
                        " encuentran en sus casas.\n\nEn el perfil puedes modificar tu nombre de usuario")
                .setCancelable(true)
                .setPositiveButton("VALE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
