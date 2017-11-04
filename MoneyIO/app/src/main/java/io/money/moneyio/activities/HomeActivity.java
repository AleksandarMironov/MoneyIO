package io.money.moneyio.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.fragments.Fragment_AddFriend;
import io.money.moneyio.fragments.Fragment_Reminders;
import io.money.moneyio.fragments.Fragment_DataHistory;
import io.money.moneyio.fragments.Fragment_Income_Expense;
import io.money.moneyio.fragments.Fragment_Profile;
import io.money.moneyio.fragments.Fragment_Statistics;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnOutcome, btnIncome, btnProfile, btnStatistics, btnAlarms, btnAddFriend, btnLogOut;
    private ImageView sandwichButton, statisticsButton;
    private DrawerLayout drawerLayout;
    private DatabaseHelperFirebase fdb;
    private FirebaseAuth firebaseAuth;
    private TextView currentFragment;
    private Fragment_Income_Expense fragment_incomeExpense;
    private List<MoneyFlow> firebaseData;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialiseElements();
        removeActionBar();
        loadFragment(fragment_incomeExpense);
        drawerDropMenuCreator();
        logOutDrawerMenuBtnListener();
        keyboardHideListener();
        showCaseView();
    }

    private void showCaseView() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(250);

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
        fdb = DatabaseHelperFirebase.getInstance(this);
        firebaseData = fdb.getData();
        fragment_incomeExpense = new Fragment_Income_Expense();
        bundle = new Bundle();
        bundle.putBoolean("isExpense", true);
        fragment_incomeExpense.setArguments(bundle);
        sandwichButton = findViewById(R.id.home_toolbar_sandwich_btn);
        drawerLayout = findViewById(R.id.dlContent);
        currentFragment = findViewById(R.id.home_toolbar_app_name);
        firebaseAuth = FirebaseAuth.getInstance();
        btnOutcome = findViewById(R.id.home_outcome_btn);
        btnOutcome.setOnClickListener(this);
        btnIncome = findViewById(R.id.home_income_btn);
        btnIncome.setOnClickListener(this);
        btnStatistics = findViewById(R.id.home_statistics_btn);
        btnStatistics.setOnClickListener(this);
        btnProfile = findViewById(R.id.home_myProfile_btn);
        btnProfile.setOnClickListener(this);
        btnAlarms = findViewById(R.id.home_alarms_btn);
        btnAlarms.setOnClickListener(this);
        btnLogOut = findViewById(R.id.home_logout_btn);
        statisticsButton = findViewById(R.id.home_toolbar_statistics_icon_btn);
        statisticsButton.setOnClickListener(this);
        btnAddFriend = findViewById(R.id.home_add_friend_btn);
        btnAddFriend.setOnClickListener(this);
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    //log out button listener
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

    //creates drawer menu
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.home_main,fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        //closes drawer menu
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);

            //check witch fragment is loaded
        } else if (fragment_incomeExpense != null && fragment_incomeExpense.isVisible()){
            exit();

        } else {
            bundle.putBoolean("isExpense", true);
            fragment_incomeExpense.setArguments(bundle);
            loadFragment(fragment_incomeExpense);
            setCurrentFragment(getString(R.string.expense));
            statisticsButton.setVisibility(View.VISIBLE);
        }
    }

    //exit dialog interface
    public void exit(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(HomeActivity.this);
        a_builder.setMessage(R.string.do_you_want_to_exit)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fdb.resetData();
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

    //sets click listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_income_btn:
                bundle.putBoolean("isExpense", false);
                Fragment_Income_Expense fragment_incomeExpense1 = new Fragment_Income_Expense();
                fragment_incomeExpense1.setArguments(bundle);
                drawerMenuButtonsAction(getString(R.string.income), fragment_incomeExpense1);
                break;
            case R.id.home_outcome_btn:
                bundle.putBoolean("isExpense", true);
                fragment_incomeExpense.setArguments(bundle);
                drawerMenuButtonsAction(getString(R.string.expense), fragment_incomeExpense);
                break;
            case R.id.home_statistics_btn:
                drawerMenuButtonsAction(getString(R.string.statistics), new Fragment_Statistics());
                break;
            case R.id.home_myProfile_btn:
                drawerMenuButtonsAction(getString(R.string.my_profile), new Fragment_Profile());
                break;
            case R.id.home_add_friend_btn:
                drawerMenuButtonsAction(getString(R.string.add_friend), new Fragment_AddFriend());
                break;
            case R.id.home_alarms_btn:
                drawerMenuButtonsAction(getString(R.string.reminders), new Fragment_Reminders());
                break;
            case R.id.home_toolbar_statistics_icon_btn:
                drawerMenuButtonsAction(getString(R.string.my_stats), new Fragment_DataHistory());
                break;
        }
    }

    //on drawer button click - loads new fragment, set correct name, hide drawer
    private void drawerMenuButtonsAction(String fragmentTitle, Fragment fragment) {
        loadFragment(fragment);
        setCurrentFragment(fragmentTitle);
        hideDrawer();
    }
}
