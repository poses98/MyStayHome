package com.highlevelindie.mystayhome;

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
                        user = task.getResult().toObject(User.class);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        log.setText("Comenzando la actualización de datos");
        updateProfile();
    }

    private void updateProfile() {
        user.setNickname(usrTxt.getText().toString());
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
}
