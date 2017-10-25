package io.money.moneyio.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.money.moneyio.R;
import io.money.moneyio.model.DatabaseHelper;
import io.money.moneyio.model.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.ShowCustomTypesRecyclerViewAdapter;
import io.money.moneyio.model.Type;
import io.money.moneyio.model.Utilities;

public class Fragment_Profile extends Fragment implements  ShowCustomTypesRecyclerViewAdapter.ItemClickListener{

    private View view;
    private DatabaseHelper db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView email, names;
    private EditText salary, type;
    private RadioGroup radioGroup;
    private Button dayOfSalary, saveType;
    private View dummyView;
    private RecyclerView recyclerView;
    private ArrayList<Type> typeFilter;
    private ShowCustomTypesRecyclerViewAdapter adapter;
    private long mLastClickTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialiseElements();
        startRecycler();
        addType();
        keyboardHideListener();
        mLastClickTime = SystemClock.elapsedRealtime();
        return view;
    }

    private void startRecycler() {
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_profile);
        final ArrayList<Type> types = DatabaseHelper.getInstance(view.getContext()).getUserTypes(firebaseUser.getUid());
        typeFilter = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getPictureId() == R.mipmap.ic_launcher) {
                typeFilter.add(types.get(i));
            }
        }
        adapter = new ShowCustomTypesRecyclerViewAdapter(view.getContext(), typeFilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 700){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        db.deleteType(firebaseUser.getUid(), typeFilter.get(position).getExpense(), typeFilter.get(position).getType());
        typeFilter.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, typeFilter.size());
        Toast.makeText(view.getContext(), "DELETED", Toast.LENGTH_SHORT).show();
    }

    private void initialiseElements() {
        db = DatabaseHelper.getInstance(view.getContext());
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

                boolean ch = db.addType(firebaseUser.getUid(), checked.equalsIgnoreCase("income")? "FALSE" : "TRUE", typeNew, R.mipmap.ic_launcher);
                if(ch) {
                    Toast.makeText(view.getContext(), "Type added", Toast.LENGTH_SHORT).show();
                    startRecycler();
                } else {
                    Toast.makeText(view.getContext(), "Already exists", Toast.LENGTH_SHORT).show();
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
