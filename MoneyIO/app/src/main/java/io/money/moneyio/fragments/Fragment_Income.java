package io.money.moneyio.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.Type;
import io.money.moneyio.model.recyclers.TypeRecyclerViewAdapter;

public class Fragment_Income extends Fragment implements View.OnClickListener, TypeRecyclerViewAdapter.ItemClickListener{

    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference finalMyRef;
    private FirebaseUser firebaseUser;
    private Button save, one, two, three, four, five, six, seven, eight, nine, zero, dot, delete;
    private TextView moneyView;
    private EditText comment;
    private RecyclerView recyclerView;
    private TypeRecyclerViewAdapter adapter;
    private View dummyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income, container, false);
        initialiseElements();
        startRecycler();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.keepSynced(true);
        myRef = myRef.child(firebaseAuth.getCurrentUser().getUid());
        finalMyRef = myRef;
        keyboardHideListener();
        return view;
    }

    private void startRecycler() {
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(view.getContext());
        final ArrayList<Type> types = db.getUserTypes(firebaseUser.getUid());
        ArrayList<Type> typeFilter = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getExpense().equalsIgnoreCase("false")) {
                typeFilter.add(types.get(i));
            }
        }
        recyclerView = (RecyclerView)view.findViewById(R.id.income_recycler_view);
        adapter = new TypeRecyclerViewAdapter(view.getContext(), typeFilter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2 , LinearLayoutManager.HORIZONTAL, false));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initialiseElements() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        one = (Button) view.findViewById(R.id.income_keyboard_1);
        one.setOnClickListener(this);
        two = (Button) view.findViewById(R.id.income_keyboard_2);
        two.setOnClickListener(this);
        three = (Button) view.findViewById(R.id.income_keyboard_3);
        three.setOnClickListener(this);
        four = (Button) view.findViewById(R.id.income_keyboard_4);
        four.setOnClickListener(this);
        five = (Button) view.findViewById(R.id.income_keyboard_5);
        five.setOnClickListener(this);
        six = (Button) view.findViewById(R.id.income_keyboard_6);
        six.setOnClickListener(this);
        seven = (Button) view.findViewById(R.id.income_keyboard_7);
        seven.setOnClickListener(this);
        eight = (Button) view.findViewById(R.id.income_keyboard_8);
        eight.setOnClickListener(this);
        nine = (Button) view.findViewById(R.id.income_keyboard_9);
        nine.setOnClickListener(this);
        zero = (Button) view.findViewById(R.id.income_keyboard_0);
        zero.setOnClickListener(this);
        dot = (Button) view.findViewById(R.id.income_keyboard_dot);
        dot.setOnClickListener(this);
        delete = (Button) view.findViewById(R.id.income_keyboard_del);
        delete.setOnClickListener(this);
        moneyView = (TextView) view.findViewById(R.id.income_keyboard_result);
        comment = (EditText) view.findViewById(R.id.income_comment);
        dummyView = view.findViewById(R.id.dummy_income);
    }

    @Override
    public void onClick(View v) {
        int i;
        switch (v.getId()) {
            case R.id.income_keyboard_1:
                i = 1;
                addNumber(i);
                break;
            case R.id.income_keyboard_2:
                i = 2;
                addNumber(i);
                break;
            case R.id.income_keyboard_3:
                i = 3;
                addNumber(i);
                break;
            case R.id.income_keyboard_4:
                i = 4;
                addNumber(i);
                break;
            case R.id.income_keyboard_5:
                i = 5;
                addNumber(i);
                break;
            case R.id.income_keyboard_6:
                i = 6;
                addNumber(i);
                break;
            case R.id.income_keyboard_7:
                i = 7;
                addNumber(i);
                break;
            case R.id.income_keyboard_8:
                i = 8;
                addNumber(i);
                break;
            case R.id.income_keyboard_9:
                i = 9;
                addNumber(i);
                break;
            case R.id.income_keyboard_0:
                i = 0;
                addNumber(i);
                break;
            case R.id.income_keyboard_dot:
                char dot = '.';
                addDot(dot);
                break;
            case R.id.income_keyboard_del:
                deleteOneChar();
                break;

        }
    }

    private void addNumber(int i) {
        if (moneyView.getText().toString().trim().equalsIgnoreCase("Insert price")) {
            moneyView.setText("");
        }
        moneyView.setText(moneyView.getText().toString() + i);
    }

    private void addDot(char dot) {
        if (moneyView.getText().toString().trim().equalsIgnoreCase("Insert price") || moneyView.getText().toString().equals("")) {
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
            moneyView.setText(moneyView.getText().toString() + dot);
        }
    }

    private void deleteOneChar() {
        String s = moneyView.getText().toString();
        if (s.length() > 1 && !moneyView.getText().toString().trim().equalsIgnoreCase("Insert price")) {
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
        if (!price.equalsIgnoreCase("Insert price")) {
            if (com == null) {
                finalMyRef.push().setValue(new MoneyFlow("false", type.getType(), Float.parseFloat(price)));
                moneyView.setText("Insert price");
            } else {
                finalMyRef.push().setValue(new MoneyFlow("false", type.getType(), com, Float.parseFloat(price)));
                moneyView.setText("Insert price");
            }
            Toast.makeText(view.getContext(), "ADDED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(view.getContext(), "Price not added", Toast.LENGTH_SHORT).show();
        }
    }

    public void keyboardHideListener(){
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fragment_income);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyView.requestFocus();
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

}
