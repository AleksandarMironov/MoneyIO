package io.money.moneyio.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.Utilities;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS= {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 2;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText email;
    private TextInputEditText psw;
    private TextView registerMail;
    private Button loginGoogle;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPersistence();
        removeActionBar();
        verifyPermissions(this);
        initialiseElements();
        chechForLoggedUser();
        registerMailBtnListener();
        googleLoginBtnListener();
        keyboardHideListener();
        loginBtnListener();
    }

    //keep data sync offline
    private void setPersistence(){
        if(!Utilities.isFirebasePersistence()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Utilities.setIsFirebasePersistence(true);
        }
    }


    private void initialiseElements(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(this.getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, R.string.no_internet_to_reg, Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.loginmail_email);
        psw = (TextInputEditText)findViewById(R.id.loginmail_password);
        registerMail = (TextView) findViewById(R.id.main_registermail_btn);
        loginGoogle = (Button) findViewById(R.id.main_googlelogin_btn);
        layout = (LinearLayout) findViewById(R.id.layout_main);
    }

    private void chechForLoggedUser(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

    public void loginBtnListener() {
        Button login = (Button) findViewById(R.id.main_loginmail_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMail = email.getText().toString().trim();
                String userPasw = psw.getText().toString().trim();

                if(!Utilities.isMail(userMail)){
                    email.setError(getString(R.string.invalid_email));
                    return;
                }

                if(!Utilities.checkString(userMail)){
                    email.setError(getString(R.string.invalid_symbols));
                    return;
                }

                if(!Utilities.checkString(userPasw)){
                    psw.setError(getString(R.string.invalid_symbols));
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(userMail, userPasw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.invalid_user_or_password, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void keyboardHideListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        psw.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(){
        layout.requestFocus();
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
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

    public void googleLoginBtnListener(){
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(MainActivity.this, R.string.sorry_internet_problem_please_try_again, Toast.LENGTH_SHORT).show();
                Log.e(getString(R.string.money_io), result.getStatus().toString());
            }
        }
    }

    void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, getString(R.string.hello) + ((user.getDisplayName() == null)? "" : user.getDisplayName()), Toast.LENGTH_SHORT).show();
                            Log.e("MoneyIO", "signInWithCredential:success");
                        } else {
                            Log.e("MoneyIO", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed_try_later, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public static void verifyPermissions(Activity activity) {
        int permissionInternet = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int permissionWrite = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionInternet != PackageManager.PERMISSION_GRANTED || permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
        a_builder.setMessage(getString(R.string.do_you_want_to_exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(getString(R.string.quit));
        alert.show();
    }
}
