package io.money.moneyio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;

public class HomeActivity extends AppCompatActivity {

    private Button logout;
    private TextView welcome;
    private FirebaseAuth firebaseAuth;
    private String firstName, secondName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        logout = (Button)findViewById(R.id.home_logout_btn);
        welcome = (TextView)findViewById(R.id.home_welcometext);
        firebaseAuth = FirebaseAuth.getInstance();
        firstName = getIntent().getStringExtra("firstName");
        secondName = getIntent().getStringExtra("secondName");
        FirebaseUser user = firebaseAuth.getCurrentUser();

//        Toast.makeText(this, user.getDisplayName().toString(), Toast.LENGTH_SHORT).show();
            welcome.setText(welcome.getText() + user.getDisplayName());
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
