package com.example.mohitmamtani.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mohitmamtani.finalproject.common.Utils;

public class MainActivity extends Activity {

    Button btnSignUp, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int userId = Utils.getLoggedInUser(this);
        if (userId != -1) {
            Intent intent = new Intent(this, ScrapBook.class);
            startActivity(intent);
        }


        btnSignUp = findViewById(R.id.btn_sign_up);
        btnLogin = findViewById(R.id.btn_login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent01 = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent01);


            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent02 = new Intent(MainActivity.this, Login.class);
                startActivity(intent02);

            }
        });
    }


}
