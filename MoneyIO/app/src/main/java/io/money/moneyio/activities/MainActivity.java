package io.money.moneyio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.money.moneyio.R;

public class MainActivity extends AppCompatActivity {

    private Button loginMail, registerMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginMail = (Button) findViewById(R.id.main_loginmail_btn);
        registerMail = (Button) findViewById(R.id.main_registermail_btn);
        loginMailBtnListener();
        registerMailBtnListener();
    }

    public void loginMailBtnListener() {
        loginMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginMailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void registerMailBtnListener() {
        registerMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterMailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
