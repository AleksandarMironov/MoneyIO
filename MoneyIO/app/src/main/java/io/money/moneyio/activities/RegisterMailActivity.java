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
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.Utilities;

public class RegisterMailActivity extends AppCompatActivity {

    private EditText email, password, password2, firstName, secondName;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private View dummyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mail);
        removeActionBar();
        initialiseElements();
        registerBtnListener();
        keyboardHideListener();
    }

    private void initialiseElements(){
        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.registermail_email);
        password = (EditText)findViewById(R.id.registermail_password);
        password2 = (EditText)findViewById(R.id.registermail_repeatpassword);
        firstName = (EditText)findViewById(R.id.registermail_firstname);
        secondName = (EditText)findViewById(R.id.registermail_secondname);
        register = (Button)findViewById(R.id.registermail_register_btn);
        dummyView = findViewById(R.id.dummy_reg);
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void keyboardHideListener(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.register_activity);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyView.requestFocus();
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
                    email.setError(getString(R.string.invalid_symbols));
                    return;
                }
                if(!Utilities.isMail(userMail)){
                    email.setError(getString(R.string.invalid_email));
                    return;
                }
                if(!Utilities.checkString(userPassword)){
                    password.setError(getString(R.string.invalid_symbols));
                    return;
                }
                if(userPassword.length() < 6){
                    password.setError(getString(R.string.password_must_be_six_sym));
                    return;
                }
                if(!Utilities.checkString(userPassword2)){
                    password2.setError(getString(R.string.invalid_symbols));
                    return;
                }
                if(!userPassword.equals(userPassword2)){
                    password2.setError(getString(R.string.password_does_not_match));
                    return;
                }
                if(!Utilities.checkString(userFirstName)){
                    firstName.setError(getString(R.string.invalid_symbols));
                    return;
                }
                if(!Utilities.checkString(userSecondName)){
                    secondName.setError(getString(R.string.invalid_symbols));
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterMailActivity.this, R.string.successfully_registered, Toast.LENGTH_SHORT).show();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userFirstName + " " + userSecondName).build();
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                                    }
                                    Intent intent = new Intent(RegisterMailActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterMailActivity.this, R.string.could_not_register_successfully, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(RegisterMailActivity.this);
        a_builder.setMessage(R.string.cancel_registration)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(RegisterMailActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(getString(R.string.cancel_registration_btn_text));
        alert.show();
    }
}
