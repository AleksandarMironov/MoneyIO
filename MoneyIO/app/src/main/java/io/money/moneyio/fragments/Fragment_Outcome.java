package io.money.moneyio.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;

public class Fragment_Outcome extends Fragment implements View.OnClickListener {

    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button save, one, two, three, four, five, six, seven, eight, nine, zero, dot, delete;
    private TextView moneyView;
    private EditText comment;
    private DatabaseReference finalMyRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_outcome, container, false);
        initialiseElements();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        myRef = myRef.child(firebaseAuth.getCurrentUser().getUid());
        finalMyRef = myRef;
        return view;
    }

    private void initialiseElements() {
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
        save = (Button) view.findViewById(R.id.outcome_add_btn);
        save.setOnClickListener(this);
        moneyView = (TextView) view.findViewById(R.id.outcome_keyboard_result);
        comment = (EditText) view.findViewById(R.id.outcome_comment);
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
            case R.id.outcome_add_btn:
                addOutcomeLineToFirebase();
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


    private void addOutcomeLineToFirebase() {
        String price = moneyView.getText().toString().trim();
        String com = comment.getText().toString().trim();
        if (!price.equalsIgnoreCase("Insert price")) {
            if (com == null) {
                finalMyRef.push().setValue(new MoneyFlow(true, "test", Integer.parseInt(price)));
            } else {
                finalMyRef.push().setValue(new MoneyFlow(true, "test", com, Integer.parseInt(price)));
            }
            Toast.makeText(view.getContext(), "ADDED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(view.getContext(), "Price not added", Toast.LENGTH_SHORT).show();
        }
    }
}

