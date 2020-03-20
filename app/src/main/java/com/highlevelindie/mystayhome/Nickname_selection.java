package com.highlevelindie.mystayhome;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Nickname_selection extends AppCompatActivity implements View.OnClickListener {
    private EditText nickTxt;
    private TextView charCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_selection);

        charCount = findViewById(R.id.charCount);

        nickTxt = findViewById(R.id.nicknameTxt);
        nickTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCount.setText(count + "/15");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
        if(nickname.length() < 16) {
            Intent intent = new Intent(getApplicationContext(), AskLocation.class);
            intent.putExtra("nick", nickname);
            intent.putExtra("gender", gender);
            intent.putExtra("age", age);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),
                    "La longitud del texto no debe exceder 15 caracteres",
                    Toast.LENGTH_LONG).show();
        }
    }
}
