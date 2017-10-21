package io.money.moneyio.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import io.money.moneyio.R;
import io.money.moneyio.fragments.Fragment_Alarm;
import io.money.moneyio.fragments.Fragment_Income;
import io.money.moneyio.fragments.Fragment_Outcome;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btnOutcome, btnIncome, btnAlarms, btnQuit, btnLogOut;
    private ImageButton sandwichButton;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    private static TextView currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        removeActionBar();
        loadFragment(new Fragment_Outcome());
        initialiseElements();
        drawerDropMenuCreator();
        outcomeDrawerMenuBtnListener();
        incomeDrawerMenuBtnListener();
        alarmsDrawerMenuBtnListener();
        quitDrawerMenuBtnListener();
        logOutDrawerMenuBtnListener();
        keyboardHideListener();
    }

    private static void setCurrentFragment(String s) {
        HomeActivity.currentFragment.setText(s);
    }

    private void initialiseElements(){
        currentFragment = (TextView)findViewById(R.id.home_toolbar_app_name);
        btnOutcome = (Button)findViewById(R.id.home_outcome_btn);
        btnIncome = (Button)findViewById(R.id.home_income_btn);
        btnAlarms = (Button)findViewById(R.id.home_alarms_btn);
        btnQuit = (Button) findViewById(R.id.home_quit_btn);
        btnLogOut = (Button) findViewById(R.id.home_logout_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void incomeDrawerMenuBtnListener() {
        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Income fragmentIncome = new Fragment_Income();
                loadFragment(fragmentIncome);
                setCurrentFragment("Income");
                hideDrawer();
            }
        });
    }

    public void outcomeDrawerMenuBtnListener() {
        btnOutcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Outcome fragmentOutcome = new Fragment_Outcome();
                loadFragment(fragmentOutcome);
                setCurrentFragment("Outcome");
                hideDrawer();
            }
        });
    }

    public void alarmsDrawerMenuBtnListener() {
        btnAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Alarm fragmentAlarm = new Fragment_Alarm();
                loadFragment(fragmentAlarm);
                setCurrentFragment("Alarms");
                hideDrawer();
            }
        });
    }

    public void logOutDrawerMenuBtnListener() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(HomeActivity.this);
                a_builder.setMessage("Do you want to Log Out")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                Intent i = new Intent(HomeActivity.this, MainActivity.class);
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
                alert.setTitle("Log Out");
                alert.show();
            }
        });
    }

    public void keyboardHideListener(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.home_activity);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void quitDrawerMenuBtnListener() {
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
    }
    public void drawerDropMenuCreator() {
        sandwichButton = (ImageButton)findViewById(R.id.home_toolbar_sandwich_btn);
        drawerLayout = (DrawerLayout)findViewById(R.id.dlContent);

        //drawer menu settings
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setStatusBarBackground(Color.TRANSPARENT);

        //set functionality of the button
        sandwichButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.home_main,fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);

        } else if (getFragmentManager().findFragmentById(R.id.home_main) instanceof Fragment_Outcome){
            exit();

        } else {
            Fragment_Outcome homeFragment = new Fragment_Outcome();
            loadFragment(homeFragment);
        }
    }

    public void exit(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(HomeActivity.this);
        a_builder.setMessage("Do you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Quit");
        alert.show();
    }

    private void hideDrawer(){
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        }
    }
}
