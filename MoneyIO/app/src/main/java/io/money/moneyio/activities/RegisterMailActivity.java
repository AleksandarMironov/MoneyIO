package io.money.moneyio.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.money.moneyio.R;

public class RegisterMailActivity extends AppCompatActivity {

    private EditText email, password, password2, firstName, secondName;
    private Button register;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register_mail);
        email = (EditText)findViewById(R.id.registermail_email);
        password = (EditText)findViewById(R.id.registermail_password);
        password2 = (EditText)findViewById(R.id.registermail_repeatpassword);
        firstName = (EditText)findViewById(R.id.registermail_firstname);
        secondName = (EditText)findViewById(R.id.registermail_secondname);
        register = (Button)findViewById(R.id.registermail_register_btn);
        registerBtnListener();
    }

    public void registerBtnListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userPassword2 = password2.getText().toString();
                final String userFirstName = firstName.getText().toString();
                final String userSecondName = secondName.getText().toString();

                if (userMail != null && !userMail.isEmpty() &&
                        userPassword != null && !userPassword.isEmpty() &&
                        userPassword2 != null && !userPassword2.isEmpty() &&
                        userPassword.equals(userPassword2) &&
                        userFirstName != null && !userFirstName.isEmpty() &&
                        userSecondName != null && !userSecondName.isEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterMailActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterMailActivity.this, LoginMailActivity.class);
                                intent.putExtra("firstName", userFirstName);
                                intent.putExtra("secondName", userSecondName);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterMailActivity.this, "Could not register successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
