package com.highlevelindie.mystayhome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.highlevelindie.mystayhome.model.Clap;
import com.highlevelindie.mystayhome.model.Location;
import com.highlevelindie.mystayhome.model.User;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    private User user;
    private EditText usrTxt;
    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = new User();
        log = findViewById(R.id.logprof);
        usrTxt = findViewById(R.id.nicknametxt);
        Button savebtn = findViewById(R.id.saveProfile);
        savebtn.setOnClickListener(this);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        Button politics = findViewById(R.id.politica);
        politics.setOnClickListener(this);
        findViewById(R.id.backbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
    }

    private void getUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            user = task.getResult().toObject(User.class);
                            usrTxt.setText(user.getNickname());
                        }catch (NullPointerException e){
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("isNull",true);
                            startActivity(intent);
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveProfile:
                log.setText("Comenzando la actualización de datos");
                updateProfile();
                break;
            case R.id.politica:
                Uri uri = Uri.parse("http://www.highlevelindie.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.logout:
                showPopup(Profile.class);
                break;
        }

    }

    private void updateProfile() {
        String newName = usrTxt.getText().toString();
        if(newName.isEmpty()){
            newName = "Anon";
            usrTxt.setText(newName);
        }
        user.setNickname(newName);

        log.setText("Conectando con la base de datos");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").
                document(mAuth.getCurrentUser().getUid()).
                set(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        log.setText("Base de usuario establecida");

                        updateRealTime();
                    }
                });


    }

    private void updateRealTime() {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        log.setText("Actualizando nombre en el mapa");

        mRef.child("GeneralLocations").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Location location = dataSnapshot.getValue(Location.class);
                location.setNick(user.getNickname());
                log.setText("Actualizando nombre en aplauso");

                mRef.child("GeneralLocations").
                        child(mAuth.getCurrentUser().
                                getUid()).
                        setValue(location).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                try {
                                    updateClap();
                                } catch (NullPointerException e) {
                                    log.setText("Perfil actualizado con éxito");
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateClap() {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mRef.child("Claps").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Clap clap = dataSnapshot.getValue(Clap.class);
                try {
                    clap.setUser(user.getNickname());
                    mRef.child("Claps").
                            child(mAuth.getCurrentUser().getUid()).
                            setValue(clap).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    log.setText("Perfil actualizado con éxito");
                                }
                            });
                } catch (NullPointerException e) {
                    log.setText("Perfil actualizado con éxito");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPopup(final Class target) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("CERRAR SESIÓN")
                .setMessage("¿ESTÁ SEGURO DE QUE QUIERE CERRAR LA SESIÓN ACTIVA? ESTO ELIMINARÁ TODOS" +
                        " SUS DATOS RELACIONADOS CON LA APLICACIÓN")
                .setCancelable(true)
                .setPositiveButton("LO ENTIENDO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cerrarSesion();

                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void cerrarSesion() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").
                document(mAuth.getCurrentUser().getUid()).
                delete().
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        log.setText("Datos de usuario borrados");

                        closeRealtTime();
                    }
                });


    }

    private void closeRealtTime() {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        log.setText("Borrando nombre del mapa");

        mRef.child("GeneralLocations").child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteClap();
            }
        });

    }

    private void deleteClap() {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mRef.child("Claps").
                child(mAuth.getCurrentUser().getUid()).
                removeValue().
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeSession();
            }
        });
    }

    private void closeSession() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
