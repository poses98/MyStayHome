package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.highlevelindie.mystayhome.model.User;

public class Gender_selection extends AppCompatActivity implements View.OnClickListener {
    private Button male, female, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_selection);

        male = findViewById(R.id.nextButton);
        female = findViewById(R.id.femaleGender);
        other = findViewById(R.id.otherGender);

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        other.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        User user = new User();
        switch (v.getId()) {
            case R.id.nextButton:
                user.setGender(0);
                break;
            case R.id.femaleGender:
                user.setGender(1);
                break;
            default:
                user.setGender(2);
                break;
        }
        Intent intent = new Intent(getApplicationContext(), Age_selection.class);
        intent.putExtra("gender", user.getGender());
        startActivity(intent);
    }
}
