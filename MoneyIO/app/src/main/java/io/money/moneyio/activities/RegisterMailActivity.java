package io.money.moneyio.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;
import io.money.moneyio.model.Utilities;

public class RegisterMailActivity extends AppCompatActivity {

    private EditText email, password, password2, firstName, secondName;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mail);
        removeActionBar();
        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.registermail_email);
        password = (EditText)findViewById(R.id.registermail_password);
        password2 = (EditText)findViewById(R.id.registermail_repeatpassword);
        firstName = (EditText)findViewById(R.id.registermail_firstname);
        secondName = (EditText)findViewById(R.id.registermail_secondname);
        register = (Button)findViewById(R.id.registermail_register_btn);
        registerBtnListener();
        keyboardHideListener();
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void keyboardHideListener(){
        layout = (LinearLayout) findViewById(R.id.register_activity);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void registerBtnListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userMail = email.getText().toString().trim();
                final String userPassword = password.getText().toString().trim();
                String userPassword2 = password2.getText().toString().trim();
                final String userFirstName = firstName.getText().toString().trim();
                final String userSecondName = secondName.getText().toString().trim();

                if(!Utilities.checkString(userMail)){
                    email.setError("Invalid symbols");
                    return;
                }
                if(!Utilities.isMail(userMail)){
                    email.setError("Invalid email");
                    return;
                }
                if(!Utilities.checkString(userPassword)){
                    password.setError("Invalid symbols");
                    return;
                }
                if(userPassword.length() < 6){
                    password.setError("Password must be 6 symbols minimum");
                    return;
                }
                if(!Utilities.checkString(userPassword2)){
                    password2.setError("Invalid symbols");
                    return;
                }
                if(!userPassword.equals(userPassword2)){
                    password2.setError("Passwords does not match");
                    return;
                }
                if(!Utilities.checkString(userFirstName)){
                    firstName.setError("Invalid symbols");
                    return;
                }
                if(!Utilities.checkString(userSecondName)){
                    secondName.setError("Invalid symbols");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterMailActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstName.toString() + " " + secondName.toString()).build();
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                                    }
                                    Intent intent = new Intent(RegisterMailActivity.this, LoginMailActivity.class);
                                    intent.putExtra("email", userMail);
                                    intent.putExtra("password", userPassword);
                                    intent.putExtra("firstName", userFirstName);
                                    intent.putExtra("secondName", userSecondName);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterMailActivity.this, "Could not register successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(RegisterMailActivity.this);
        a_builder.setMessage("Do you want to cancel registration?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(RegisterMailActivity.this, MainActivity.class);
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
        alert.setTitle("Cancel registration");
        alert.show();
    }
}
