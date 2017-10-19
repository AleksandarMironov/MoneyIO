package io.money.moneyio.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.money.moneyio.R;
import io.money.moneyio.fragments.Fragment_Income;
import io.money.moneyio.fragments.Fragment_Outcome;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String firstName, secondName;
    private Button outcome, income;
    private ImageButton sandwichButton;
    private  DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Fragment_Outcome fragmentOutcome = new Fragment_Outcome();
        loadFragment(fragmentOutcome);
        getSupportActionBar().hide();
        outcome = (Button)findViewById(R.id.home_outcome_btn);
        income = (Button)findViewById(R.id.home_income_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        firstName = getIntent().getStringExtra("firstName");
        secondName = getIntent().getStringExtra("secondName");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        drawerDropMenuCreator();
        outcomeDrawerMenuBtnListener();
        incomeDrawerMenuBtnListener();
    }

    public void incomeDrawerMenuBtnListener() {
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Income fragmentIncome = new Fragment_Income();
                loadFragment(fragmentIncome);
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });
    }

    public void outcomeDrawerMenuBtnListener() {
        outcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Outcome fragmentOutcome = new Fragment_Outcome();
                loadFragment(fragmentOutcome);
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
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
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
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
}
