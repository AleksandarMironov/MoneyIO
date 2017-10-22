package io.money.moneyio.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.money.moneyio.R;

public class Fragment_Profile extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView email, names;
    private EditText salary, type;
    private RadioGroup radioGroup;
    private Button dayOfSalary, saveType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialiseElements();
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
        setValues();
    }

    private void setValues() {
        email.setText(email.getText() + firebaseUser.getEmail().toString());
        names.setText(names.getText() + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }
}
