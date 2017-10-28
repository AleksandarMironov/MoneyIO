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
import android.widget.ImageView;
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
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button btnOutcome, btnIncome, btnProfile, btnStatistics, btnAlarms, btnQuit, btnLogOut;
    private ImageView sandwichButton, statisticsButton;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    private DatabaseReference myDatabaseRef;
    private  TextView currentFragment;

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
        readDatabase();
        showCaseView();
    }

    private void showCaseView() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(400);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, firebaseAuth.getCurrentUser().getUid());
        sequence.setConfig(config);

        sequence.addSequenceItem(sandwichButton,
                getString(R.string.tutorial_text1), getString(R.string.tutorial_got_it_btn_text));

        sequence.addSequenceItem(statisticsButton,
                getString(R.string.tutorial_text2), getString(R.string.tutorial_got_it_btn_text));

        sequence.start();
    }

    private  void setCurrentFragment(String s) {
        currentFragment.setText(s);
    }

    private void initialiseElements(){
        sandwichButton = (ImageView)findViewById(R.id.home_toolbar_sandwich_btn);
        drawerLayout = (DrawerLayout)findViewById(R.id.dlContent);
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
        statisticsButton = (ImageView)findViewById(R.id.home_toolbar_statistics_icon_btn);
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
                a_builder.setMessage(R.string.do_you_want_to_log_out)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle(getString(R.string.log_out));
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
            setCurrentFragment(getString(R.string.expense));
            statisticsButton.setVisibility(View.VISIBLE);
        }
    }

    public void exit(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(HomeActivity.this);
        a_builder.setMessage(R.string.do_you_want_to_exit)
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

    private void hideDrawer(){
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_income_btn:
                drawerMenuButtonsAction(getString(R.string.income), new Fragment_Income());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_outcome_btn:
                drawerMenuButtonsAction(getString(R.string.expense), new Fragment_Outcome());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_statistics_btn:
                drawerMenuButtonsAction(getString(R.string.statistics), new Fragment_Statistics());
                statisticsButton.setVisibility(View.VISIBLE);
                break;
            case R.id.home_myProfile_btn:
                drawerMenuButtonsAction(getString(R.string.my_profile), new Fragment_Profile());
                statisticsButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.home_add_friend_btn:
                break;
            case R.id.home_alarms_btn:
                drawerMenuButtonsAction(getString(R.string.alarms), new Fragment_Alarm());
                break;
            case R.id.home_quit_btn:
                exit();
                break;
            case R.id.home_toolbar_statistics_icon_btn:
                drawerMenuButtonsAction(getString(R.string.my_stats), new Fragment_DataHistory());
                break;
        }
    }

    private void drawerMenuButtonsAction(String fragmentTitle, Fragment fragment) {
        loadFragment(fragment);
        setCurrentFragment(fragmentTitle);
        hideDrawer();
    }
}
