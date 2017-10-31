package io.money.moneyio.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.recyclers.ShowCustomTypesRecyclerViewAdapter;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.PlannedFlow;
import io.money.moneyio.model.Type;
import io.money.moneyio.model.utilities.Utilities;

public class Fragment_Profile extends Fragment implements  ShowCustomTypesRecyclerViewAdapter.ItemClickListener{

    private View view;
    private DatabaseHelperSQLite db;
    private FirebaseUser user;
    private TextView email, names;
    private EditText salary, type, dayOfSalary;
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private ArrayList<Type> typeFilter;
    private PlannedFlow plannedFlow;
    private ShowCustomTypesRecyclerViewAdapter adapter;
    private long mLastClickTime;
    private MonthYearPicker monthYearPicker;
    private int payDay;
    private ImageView okImg, deleteImg, saveType;
    private LinearLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialiseElements();
        startRecycler();
        addType();
        keyboardHideListener();
        onSaveSalaryListener();
        onDeleteSalaryListener();
        mLastClickTime = SystemClock.elapsedRealtime();
        onDayOfSalaryClickListener();
        return view;
    }

    private void startRecycler() {
        final List<Type> types = DatabaseHelperSQLite.getInstance(view.getContext()).getUserTypes(user.getUid());
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
    public void onItemClick(View view, int pos) {
        final int position = pos;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 700){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        AlertDialog.Builder a_builder = new AlertDialog.Builder(view.getContext());
        a_builder.setMessage("Are you shore?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteType(user.getUid(), typeFilter.get(position).getExpense(), typeFilter.get(position).getType());
                        typeFilter.remove(position);
                        recyclerView.removeViewAt(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, typeFilter.size());
                        Toast.makeText(getContext(), "DELETED", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("DELETE");
        alert.show();




    }

    private void initialiseElements() {
        recyclerView = view.findViewById(R.id.recycler_profile);
        db = DatabaseHelperSQLite.getInstance(view.getContext());
        monthYearPicker = new MonthYearPicker(view.getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = view.findViewById(R.id.profile_email);
        names = view.findViewById(R.id.profile_name);
        salary = view.findViewById(R.id.profile_salary);
        type = view.findViewById(R.id.profile_type);
        radioGroup = view.findViewById(R.id.profile_radiogr_kind);
        dayOfSalary = view.findViewById(R.id.profile_choose_date);
        saveType = view.findViewById(R.id.profile_save_btn);
        okImg = view.findViewById(R.id.profile_add_salary_btn);
        deleteImg = view.findViewById(R.id.profile_delete_salary_btn);
        payDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        layout = (LinearLayout) view.findViewById(R.id.fragment_profile);
        setTextValues();
    }

    private void setTextValues() {
        email.setText(user.getEmail());
        names.setText(user.getDisplayName());
        plannedFlow = db.getUserPlanned(user.getUid());
        if(plannedFlow == null){
            salary.setText("");
            dayOfSalary.setText("pay day: SELECT" );
        } else {
            salary.setText("" + plannedFlow.getAmount());
            dayOfSalary.setText("pay day: " + plannedFlow.getDate());
        }
    }

    public void onSaveSalaryListener(){
        okImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.isNumber(salary.getText().toString())){
                    if(plannedFlow != null){
                        //TODO make edit method :D
                        db.deletePlanned(plannedFlow.getUserID(), plannedFlow.getDate(), plannedFlow.getType(), plannedFlow.getAmount());
                    }
                    boolean isAdded = db.addPlanned(user.getUid(), payDay, "Salary",Float.parseFloat(salary.getText().toString()));
                    if(isAdded){
                        Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                        setTextValues();
                    } else {
                        Toast.makeText(view.getContext(), "NOT saved, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Add salary", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onDeleteSalaryListener(){
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(plannedFlow != null){
                    db.deletePlanned(plannedFlow.getUserID(), plannedFlow.getDate(), plannedFlow.getType(), plannedFlow.getAmount());
                    Toast.makeText(view.getContext(), "DELETED", Toast.LENGTH_SHORT).show();
                    plannedFlow = null;
                }
                salary.setText("");
                dayOfSalary.setText("pay day: SELECT" );
            }
        });
    }

    public void onDayOfSalaryClickListener(){
        dayOfSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        payDay = monthYearPicker.getSelectedDay();
                        dayOfSalary.setText("pay day: " + payDay);
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                Calendar calendar = Calendar.getInstance();
                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, true, false, false);
                monthYearPicker.show();
            }
        });
    }

    private void hideKeyboard(){
        layout.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                boolean ch = db.addType(user.getUid(), checked.equalsIgnoreCase("income")? "FALSE" : "TRUE", typeNew, R.mipmap.ic_launcher);
                if(ch) {
                    Toast.makeText(view.getContext(), "Type added", Toast.LENGTH_SHORT).show();
                    startRecycler();
                } else {
                    Toast.makeText(view.getContext(), "Already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void keyboardHideListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        type.setOnKeyListener(new View.OnKeyListener() {
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

}
