package com.highlevelindie.mystayhome;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.highlevelindie.mystayhome.model.Clap;

import java.util.ArrayList;

public class ClapWall extends AppCompatActivity implements View.OnClickListener {
    ArrayList<Clap> claps = new ArrayList<>();
    private ListView commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clap_wall);
        commentList = findViewById(R.id.commentList);
        ImageButton back = findViewById(R.id.backbutton);
        back.setOnClickListener(this);
        listUsers();
    }


    /**
     * Fill the ArrayList of User according to the uids stored in uids ArrayList
     */
    private void listUsers() {
        FirebaseDatabase.
                getInstance().
                getReference().
                child("Claps").
                limitToLast(50).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (!data.child("message").getValue().toString().isEmpty()) {
                                Clap clap = data.getValue(Clap.class);
                                claps.add(clap);
                            }
                        }
                        commentList.setAdapter(new matchAdapter(ClapWall.this, R.layout.comment_view, claps));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class matchAdapter extends ArrayAdapter<Clap> {
        private ArrayList<Clap> claps;

        public matchAdapter(@NonNull Context context, int resource, ArrayList<Clap> claps) {
            super(context, resource, claps);
            this.claps = claps;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertview, @NonNull ViewGroup parent) {
            View view = convertview;
            final Clap clap = claps.get(position);

            if (view == null) {
                view = LayoutInflater.from(ClapWall.this).inflate(R.layout.comment_view, parent, false);
            }

            // Cogemos las referencias del layout que le hemos puesto para los items en objetos
            // del tipo TextView
            TextView userName = view.findViewById(R.id.commentName);
            TextView userCity = view.findViewById(R.id.commentCity);
            TextView userComment = view.findViewById(R.id.commentBody);

            if (userName.length() >= 10) {
                userName.setText(userName.getText().toString().split("", 10)[0]);
            }
            // Asignamos su valor mediante setText
            userName.setText(clap.getUser());
            userCity.setText(clap.getCity());
            userComment.setText(clap.getMessage());

            return view;
        }
    }

}
