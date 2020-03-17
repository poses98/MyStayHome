package com.highlevelindie.mystayhome;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highlevelindie.mystayhome.model.Clap;
import com.highlevelindie.mystayhome.model.User;

public class create_clap extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    User user = new User();
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clap);

        message = findViewById(R.id.clapMessage);

        Button sendClap = findViewById(R.id.sendClap);
        sendClap.setOnClickListener(this);
        ImageButton back = findViewById(R.id.backbutton);
        back.setOnClickListener(this);
        getUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendClap:
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                Clap clap = new Clap();
                clap.setCity(user.getCity());
                clap.setMessage(message.getText().toString());
                clap.setUser(user.getNickname());
                mRef.child("Claps").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(clap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Aplauso enviado con éxito!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            case R.id.backbutton:
                finish();
                break;
        }

    }

    private void getUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        user = task.getResult().toObject(User.class);

                    }
                });
    }
}
