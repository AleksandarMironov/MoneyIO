package io.money.moneyio.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.money.moneyio.R;
import io.money.moneyio.model.DatabaseHelper;
import io.money.moneyio.model.Utilities;

public class Fragment_Profile extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView email, names;
    private EditText salary, type;
    private RadioGroup radioGroup;
    private Button dayOfSalary, saveType;
    private View dummyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialiseElements();
        addType();
        keyboardHideListener();
        return view;
    }

    private void initialiseElements() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email = (TextView)view.findViewById(R.id.profile_email);
        names = (TextView)view.findViewById(R.id.profile_name);
        salary = (EditText)view.findViewById(R.id.profile_salary);
        type = (EditText)view.findViewById(R.id.profile_type);
        radioGroup = (RadioGroup)view.findViewById(R.id.profile_radiogr_kind);
        dayOfSalary = (Button)view.findViewById(R.id.profile_choose_date);
        saveType = (Button)view.findViewById(R.id.profile_save_btn);
        dummyView = view.findViewById(R.id.dummy_profile);
        setValues();
    }

    private void setValues() {
        email.setText(email.getText() + firebaseUser.getEmail().toString());
        names.setText(names.getText() + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    private void addType() {
        saveType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeNew = type.getText().toString();
                String checked = ((RadioButton)view.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

                if (!Utilities.checkString(typeNew)){
                    Toast.makeText(getContext(), "Invalid type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checked.equalsIgnoreCase("income")) {
                    DatabaseHelper.getInstance(view.getContext()).addType(firebaseUser.getUid(), "TRUE", typeNew, R.mipmap.ic_launcher);
                    Toast.makeText(view.getContext(), "Income type added", Toast.LENGTH_SHORT).show();
                } else if(checked.equalsIgnoreCase("outcome")) {
                    DatabaseHelper.getInstance(view.getContext()).addType(firebaseUser.getUid(), "FALSE", typeNew, R.mipmap.ic_launcher);
                    Toast.makeText(view.getContext(), "Outcome type added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void keyboardHideListener(){
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fragment_profile);
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
