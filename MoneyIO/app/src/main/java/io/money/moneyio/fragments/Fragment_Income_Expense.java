package io.money.moneyio.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Type;
import io.money.moneyio.model.recyclers.TypeRecyclerViewAdapter;

public class Fragment_Income_Expense extends Fragment implements View.OnClickListener, TypeRecyclerViewAdapter.ItemClickListener {

    private View view;
    private DatabaseHelperFirebase fdb;
    private FirebaseUser user;
    private Button one, two, three, four, five, six, seven, eight, nine, zero, dot, delete;
    private TextView moneyView;
    private EditText comment;
    private RecyclerView recyclerView;
    private TypeRecyclerViewAdapter adapter;
    private LinearLayout layout;
    private boolean isExpense;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income_expense, container, false);
        initialiseElements();
        startRecycler();
        keyboardHideListener();
        return view;
    }

    private void startRecycler() {
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(view.getContext());
        final List<Type> types = db.getUserTypes(user.getUid());
        ArrayList<Type> typeFilter = new ArrayList<>();

        if (!isExpense) {
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).getExpense().equalsIgnoreCase("false")) {
                    typeFilter.add(types.get(i));
                }
            }
        } else {
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).getExpense().equalsIgnoreCase("true")) {
                    typeFilter.add(types.get(i));
                }
            }
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.outcome_recycler_view);
        adapter = new TypeRecyclerViewAdapter(view.getContext(), typeFilter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initialiseElements() {
        isExpense = getArguments().getBoolean(getString(R.string.is_Expense));
        user = FirebaseAuth.getInstance().getCurrentUser();
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        one = (Button) view.findViewById(R.id.outcome_keyboard_1);
        one.setOnClickListener(this);
        two = (Button) view.findViewById(R.id.outcome_keyboard_2);
        two.setOnClickListener(this);
        three = (Button) view.findViewById(R.id.outcome_keyboard_3);
        three.setOnClickListener(this);
        four = (Button) view.findViewById(R.id.outcome_keyboard_4);
        four.setOnClickListener(this);
        five = (Button) view.findViewById(R.id.outcome_keyboard_5);
        five.setOnClickListener(this);
        six = (Button) view.findViewById(R.id.outcome_keyboard_6);
        six.setOnClickListener(this);
        seven = (Button) view.findViewById(R.id.outcome_keyboard_7);
        seven.setOnClickListener(this);
        eight = (Button) view.findViewById(R.id.outcome_keyboard_8);
        eight.setOnClickListener(this);
        nine = (Button) view.findViewById(R.id.outcome_keyboard_9);
        nine.setOnClickListener(this);
        zero = (Button) view.findViewById(R.id.outcome_keyboard_0);
        zero.setOnClickListener(this);
        dot = (Button) view.findViewById(R.id.outcome_keyboard_dot);
        dot.setOnClickListener(this);
        delete = (Button) view.findViewById(R.id.outcome_keyboard_del);
        delete.setOnClickListener(this);
        moneyView = (TextView) view.findViewById(R.id.outcome_keyboard_result);
        comment = (EditText) view.findViewById(R.id.outcome_comment);
        layout = (LinearLayout) view.findViewById(R.id.fragment_outcome);
    }

    @Override
    public void onClick(View v) {
        int i;
        switch (v.getId()) {
            case R.id.outcome_keyboard_1:
                i = 1;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_2:
                i = 2;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_3:
                i = 3;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_4:
                i = 4;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_5:
                i = 5;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_6:
                i = 6;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_7:
                i = 7;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_8:
                i = 8;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_9:
                i = 9;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_0:
                i = 0;
                addNumber(i);
                break;
            case R.id.outcome_keyboard_dot:
                char dot = '.';
                addDot(dot);
                break;
            case R.id.outcome_keyboard_del:
                deleteOneChar();
                break;
        }
    }

    private void addNumber(int i) {

        String text = moneyView.getText().toString().trim();

        if (moneyView.getText().toString().trim().equalsIgnoreCase(getString(R.string.insert_price))) {
            moneyView.setText("");
        }
        if (moneyView.getText().toString().trim().length() < 10) {
            if (!(text.length() == 1 && text.equals("0") && i == 0)) {
                if (!moneyView.getText().toString().trim().matches("[0-9]+\\.[0-9]{2}")) {
                    moneyView.setText(moneyView.getText().toString() + i);
                }
            }
        }
    }

    private void addDot(char dot) {
        if (moneyView.getText().toString().trim().equalsIgnoreCase(getString(R.string.insert_price)) || moneyView.getText().toString().equals("")) {
            moneyView.setText("0");
        }
        String text = moneyView.getText().toString();
        int countDots = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '.') {
                countDots++;
            }
        }
        if (countDots == 0) {
            if (moneyView.getText().toString().trim().length() < 9) {
                moneyView.setText(moneyView.getText().toString() + dot);
            }
        }
    }

    private void deleteOneChar() {
        String s = moneyView.getText().toString();
        if (s.length() > 1 && !moneyView.getText().toString().trim().equalsIgnoreCase(getString(R.string.insert_price))) {
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < s.length() - 1; i++) {
                buffer.append(s.charAt(i));
            }
            moneyView.setText(buffer.toString());
        } else {
            moneyView.setText("Insert price");
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Type type = adapter.getItem(position);
        String price = moneyView.getText().toString().trim();
        String com = comment.getText().toString().trim();
        String uid = user.getUid();

        if (!price.equalsIgnoreCase(getString(R.string.insert_price)) && Float.parseFloat(price) != 0) {
            if (isExpense) {
                fdb.addData(uid, new MoneyFlow(uid, getString(R.string.ex), type.getType(), com, Float.parseFloat(price)));
            } else {
                fdb.addData(uid, new MoneyFlow(uid, getString(R.string.in), type.getType(), com, Float.parseFloat(price)));
            }
            moneyView.setText(getString(R.string.insert_price));
            comment.setText("");
            Toast.makeText(view.getContext(), R.string.ADDED, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(view.getContext(), R.string.price_not_added, Toast.LENGTH_SHORT).show();
        }
    }

    public void keyboardHideListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        comment.setOnKeyListener(new View.OnKeyListener() {
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
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        layout.requestFocus();
    }
}

