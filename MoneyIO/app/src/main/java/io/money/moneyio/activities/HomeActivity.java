package io.money.moneyio.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String firstName, secondName;
    private ImageButton sandwichButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        firstName = getIntent().getStringExtra("firstName");
        secondName = getIntent().getStringExtra("secondName");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        drawerDropMenuCreator();
    }

    public void drawerDropMenuCreator() {
        sandwichButton = (ImageButton)findViewById(R.id.home_toolbar_sandwich_btn);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.dlContent);

        //drawer menu settings
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setStatusBarBackground(Color.TRANSPARENT);

        //set functionality of the button
        sandwichButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
    }
}
