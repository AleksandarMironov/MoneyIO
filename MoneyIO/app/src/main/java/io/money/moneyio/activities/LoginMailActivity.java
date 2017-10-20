package io.money.moneyio.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;
import io.money.moneyio.model.Utilities;

public class LoginMailActivity extends AppCompatActivity {

    private String mail, password, firstName, secondName;
    private EditText email, psw;
    private Button login;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mail);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        mail = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        firstName = getIntent().getStringExtra("firstName");
        secondName = getIntent().getStringExtra("secondName");
        email = (EditText)findViewById(R.id.loginmail_email);
        psw = (EditText)findViewById(R.id.loginmail_password);
        login = (Button)findViewById(R.id.loginmail_login_btn);

        if (mail != null && password != null && firstName != null & secondName != null) {
            email.setText(mail);
            psw.setText(password);
        }
        loginBtnListener();
    }

    public void loginBtnListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMail = email.getText().toString().trim();
                String userPasw = psw.getText().toString().trim();

                if(!Utilities.isMail(userMail)){
                    email.setError("Invalid email");
                    return;
                }

                if(!Utilities.checkString(userMail)){
                    email.setError("Invalid symbols");
                    return;
                }

                if(!Utilities.checkString(userPasw)){
                    psw.setError("Invalid symbols");
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(userMail, userPasw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user.getDisplayName() == null) {
                                        Log.e("MoneyIO", "user display name is null");
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(firstName.toString() + " " + secondName.toString()).build();

                                        user.updateProfile(profileUpdates);
                                    }
                                    Intent intent = new Intent(LoginMailActivity.this, HomeActivity.class);
                                    intent.putExtra("firstName", firstName);
                                    intent.putExtra("secondName", secondName);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginMailActivity.this, "Invalid data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(LoginMailActivity.this);
        a_builder.setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LoginMailActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Cancel logging");
        alert.show();
    }
}
