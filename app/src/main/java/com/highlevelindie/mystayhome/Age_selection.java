package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Age_selection extends AppCompatActivity implements View.OnClickListener {
    private EditText ageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_selection);

        ageText = findViewById(R.id.ageText);

        Button sendAge = findViewById(R.id.askPermission);
        sendAge.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int gender = this.getIntent().getIntExtra("gender", 2);
        if (checkAge()) {
            Intent intent = new Intent(getApplicationContext(), Nickname_selection.class);
            intent.putExtra("age", Integer.parseInt(ageText.getText().toString()));
            intent.putExtra("gender", gender);
            startActivity(intent);
        }
    }

    private boolean checkAge() {
        int age = 0;
        boolean check = false;
        if (!ageText.getText().toString().isEmpty()) {
            age = Integer.parseInt(ageText.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Introduce una edad", Toast.LENGTH_LONG);
        }
        if (age < 100 && age >= 18) {
            check = true;
        } else {
            Toast.makeText(getApplicationContext(), "La edad mínima son 18 años", Toast.LENGTH_SHORT).show();
        }

        return check;
    }
}
