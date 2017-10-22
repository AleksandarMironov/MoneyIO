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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.money.moneyio.R;
import io.money.moneyio.fragments.Fragment_Alarm;
import io.money.moneyio.fragments.Fragment_DataHistory;
import io.money.moneyio.fragments.Fragment_Income;
import io.money.moneyio.fragments.Fragment_Outcome;
import io.money.moneyio.fragments.Fragment_Profile;
import io.money.moneyio.fragments.Fragment_Statistics;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Utilities;

import static android.R.id.list;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button btnOutcome, btnIncome, btnProfile, btnStatistics, btnAlarms, btnQuit, btnLogOut;
    private ImageButton sandwichButton, statisticsButton;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    DatabaseReference myDatabaseRef;
    private static TextView currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        removeActionBar();
        loadFragment(new Fragment_Outcome());
        initialiseElements();
        drawerDropMenuCreator();
        logOutDrawerMenuBtnListener();
        keyboardHideListener();
    }

    private static void setCurrentFragment(String s) {
        HomeActivity.currentFragment.setText(s);
    }

    private void initialiseElements(){
        currentFragment = (TextView)findViewById(R.id.home_toolbar_app_name);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        btnOutcome = (Button)findViewById(R.id.home_outcome_btn);
        btnOutcome.setOnClickListener(this);
        btnIncome = (Button)findViewById(R.id.home_income_btn);
        btnIncome.setOnClickListener(this);
        btnStatistics = (Button)findViewById(R.id.home_statistics_btn);
        btnStatistics.setOnClickListener(this);
        btnProfile = (Button)findViewById(R.id.home_myProfile_btn);
        btnProfile.setOnClickListener(this);
        btnAlarms = (Button)findViewById(R.id.home_alarms_btn);
        btnAlarms.setOnClickListener(this);
        btnQuit = (Button) findViewById(R.id.home_quit_btn);
        btnQuit.setOnClickListener(this);
        btnLogOut = (Button) findViewById(R.id.home_logout_btn);
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid());
        readDatabase();
        statisticsButton = (ImageButton)findViewById(R.id.home_toolbar_statistics_icon_btn);
        statisticsButton.setOnClickListener(this);
    }

    private void readDatabase(){
        Utilities.data = new ArrayList<>();
        final DatabaseReference mref = myDatabaseRef;
        mref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MoneyFlow t = dataSnapshot.getValue(MoneyFlow.class);
                Utilities.data.add(t);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
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
            setCurrentFragment("Outcome");
            statisticsButton.setVisibility(View.VISIBLE);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_income_btn:
                drawerMenuButtonsAction("Income", new Fragment_Income());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_outcome_btn:
                drawerMenuButtonsAction("Outcome", new Fragment_Outcome());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_statistics_btn:
                drawerMenuButtonsAction("Statistics", new Fragment_Statistics());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_myProfile_btn:
                drawerMenuButtonsAction("My Profile", new Fragment_Profile());
                statisticsButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.home_add_friend_btn:
                break;
            case R.id.home_alarms_btn:
                drawerMenuButtonsAction("Alarms", new Fragment_Alarm());
                break;
            case R.id.home_quit_btn:
                exit();
                break;
            case R.id.home_toolbar_statistics_icon_btn:
                loadFragment(new Fragment_DataHistory());
                setCurrentFragment("My Stats");
        }
    }

    private void drawerMenuButtonsAction(String fragmentTitle, Fragment fragment) {
        loadFragment(fragment);
        setCurrentFragment(fragmentTitle);
        hideDrawer();
    }
}
