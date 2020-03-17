package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Nickname_selection extends AppCompatActivity implements View.OnClickListener {
    private EditText nickTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_selection);

        nickTxt = findViewById(R.id.nicknameTxt);
        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String nickname = nickTxt.getText().toString();
        int gender = this.getIntent().getIntExtra("gender", 2);
        int age = this.getIntent().getIntExtra("age", 18);
        if (nickname.isEmpty()) {
            nickname = "Anon";
        }
        Intent intent = new Intent(getApplicationContext(), AskLocation.class);
        intent.putExtra("nick", nickname);
        intent.putExtra("gender", gender);
        intent.putExtra("age", age);
        startActivity(intent);
    }
}
