package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendClapScreen extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference mReference;

    private TextView clapCount;
    private TextView wall;
    private Button sendClap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_clap_screen);
        mReference = FirebaseDatabase.getInstance().getReference();

        clapCount = findViewById(R.id.countClap);
        wall = findViewById(R.id.viewWall);
        wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClapWall.class);
                startActivity(intent);
                finish();
            }
        });

        sendClap = findViewById(R.id.sendClap);
        sendClap.setOnClickListener(this);
        ImageButton back = findViewById(R.id.backbutton);
        back.setOnClickListener(this);
        getCount();
        changeButtonName();
    }

    private void changeButtonName() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final TextView alreadyClapped = findViewById(R.id.alreadyClapped);
        mReference.child("Claps").
                child(mAuth.getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() || getIntent().getBooleanExtra("Clapped",false)){
                    sendClap.setText(R.string.viewClapButton);
                    alreadyClapped.setText(R.string.alreadyClapped);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCount() {
        mReference.child("Claps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clapCount.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendClap:
                Intent intent = new Intent(getApplicationContext(), create_clap.class);
                startActivity(intent);
                break;
            case R.id.backbutton:
                finish();
                break;
        }
    }



}
