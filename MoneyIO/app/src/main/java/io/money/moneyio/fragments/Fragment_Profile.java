package io.money.moneyio.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.money.moneyio.R;
import io.money.moneyio.activities.HomeActivity;
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
    private TextView email, name, noTypes;
    private EditText salary, type, dayOfSalary;
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private ArrayList<Type> typeFilter;
    private PlannedFlow plannedFlow;
    private ShowCustomTypesRecyclerViewAdapter adapter;
    private long mLastClickTime;
    private MonthYearPicker monthYearPicker;
    private int payDay;
    private ImageView okImg, deleteImg, saveType, imgEye, imgQuestionSalary, imgQuestionType;
    private LinearLayout layout;
    private Spinner changeLanguage;

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
        seOnImgEyeClickListener();
        seOnImgQuestionTypeClickListener();
        seOnImgQuestionSalaryClickListener();
        setSpinnerChangeLanguage();
        return view;
    }

    private void startRecycler() {
        final List<Type> types = DatabaseHelperSQLite.getInstance(view.getContext()).getUserTypes(user.getUid());
        typeFilter = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getPictureId() == R.drawable.custom_type) {
                typeFilter.add(types.get(i));
            }
        }
        if (isTypeFilerEmpty()) {
            imgEye.setVisibility(View.VISIBLE);
            noTypes.setVisibility(View.GONE);
            adapter = new ShowCustomTypesRecyclerViewAdapter(view.getContext(), typeFilter);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        } else {
            imgEye.setVisibility(View.INVISIBLE);
            noTypes.setVisibility(View.VISIBLE);
        }
    }

    private boolean isTypeFilerEmpty() {
        if (typeFilter.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(View view, int pos) {
        final int position = pos;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 700){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        AlertDialog.Builder a_builder = new AlertDialog.Builder(view.getContext());
        a_builder.setMessage(R.string.are_you_sure)
                .setCancelable(false)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteType(user.getUid(), typeFilter.get(position).getExpense(), typeFilter.get(position).getType());
                        typeFilter.remove(position);
                        recyclerView.removeViewAt(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, typeFilter.size());
                        Toast.makeText(getContext(), R.string.DELETED, Toast.LENGTH_SHORT).show();
                        if (isTypeFilerEmpty()) {
                            noTypes.setVisibility(View.GONE);
                            imgEye.setVisibility(View.VISIBLE);
                        } else {
                            noTypes.setVisibility(View.VISIBLE);
                            imgEye.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(getString(R.string.DELETE));
        alert.show();
    }

    private void initialiseElements() {
        recyclerView = view.findViewById(R.id.recycler_profile);
        db = DatabaseHelperSQLite.getInstance(view.getContext());
        monthYearPicker = new MonthYearPicker(view.getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = view.findViewById(R.id.profile_email);
        name = view.findViewById(R.id.profile_name);
        salary = view.findViewById(R.id.profile_salary);
        type = view.findViewById(R.id.profile_type);
        radioGroup = view.findViewById(R.id.profile_radiogr_kind);
        dayOfSalary = view.findViewById(R.id.profile_choose_date);
        saveType = view.findViewById(R.id.profile_save_btn);
        okImg = view.findViewById(R.id.profile_add_salary_btn);
        deleteImg = view.findViewById(R.id.profile_delete_salary_btn);
        payDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        layout = view.findViewById(R.id.fragment_profile);
        noTypes = view.findViewById(R.id.profile_no_types);
        imgEye = view.findViewById(R.id.profile_eye);
        imgQuestionType = view.findViewById(R.id.profile_add_type_question);
        imgQuestionSalary = view.findViewById(R.id.profile_salary_question);
        changeLanguage = view.findViewById(R.id.change_language_spinner);
        setTextValues();
    }

    private void setSpinnerChangeLanguage() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.languages, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        changeLanguage.setAdapter(adapter);

        changeLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("changeLanguage", "yes");
                if (i == 1) {
                    updateResources(view.getContext(), "bg");
                    getActivity().startActivity(intent);
                    changeLanguageInSharedPrefs("bg");
                    getActivity().finish();
                } else if (i == 2) {
                    updateResources(view.getContext(), "eng");
                    getActivity().startActivity(intent);
                    changeLanguageInSharedPrefs("eng");
                    getActivity().finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        
        return true;
    }

    private void changeLanguageInSharedPrefs(String newLanguadge){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", newLanguadge);
        editor.apply();
    }


    public void seOnImgEyeClickListener(){
        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getVisibility() == View.VISIBLE){
                    name.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    view.findViewById(R.id.profile_l0).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_l1).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_l2).setVisibility(View.GONE);
                    imgEye.setImageResource(R.drawable.eye_invisible);
                } else {
                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.profile_l0).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.profile_l1).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.profile_l2).setVisibility(View.VISIBLE);
                    imgEye.setImageResource(R.drawable.eye_visible);
                }
            }
        });
    }

    public void seOnImgQuestionTypeClickListener(){
        imgQuestionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.profile_text));
            }
        });
    }

    public void seOnImgQuestionSalaryClickListener(){
        imgQuestionSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.profile_salary_text));
            }
        });
    }

    private void setTextValues() {
        email.setText(getString(R.string.email_two_dots) + " " + user.getEmail());
        name.setText(getString(R.string.hello) + " " + user.getDisplayName());
        plannedFlow = db.getUserPlanned(user.getUid());
        if(plannedFlow == null){
            salary.setText("");
            dayOfSalary.setText(R.string.pay_day_ );
        } else {
            salary.setText("" + plannedFlow.getAmount());
            dayOfSalary.setText(getString(R.string.PAY_DAY) + " " + plannedFlow.getDate());
        }
    }

    public void onSaveSalaryListener(){
        okImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.isNumber(salary.getText().toString().trim())){
                    if(plannedFlow != null){
                        db.deletePlanned(plannedFlow.getUserID(), plannedFlow.getDate(), plannedFlow.getType(), plannedFlow.getAmount());
                    }
                    boolean isAdded = db.addPlanned(user.getUid(), payDay, getString(R.string.Salary),Float.parseFloat(salary.getText().toString()));
                    if(isAdded){
                        Toast.makeText(view.getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                        setTextValues();
                    } else {
                        Toast.makeText(view.getContext(), R.string.not_saved_please_try_again, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), R.string.add_salary, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(view.getContext(), getString(R.string.DELETED), Toast.LENGTH_SHORT).show();
                    plannedFlow = null;
                }
                salary.setText("");
                dayOfSalary.setText(getString(R.string.pay_day_));
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
                        dayOfSalary.setText(getString(R.string.PAY_DAY) + " " + payDay);
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
                String typeNew = type.getText().toString().trim();
                String checked = ((RadioButton)view.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

                if (!Utilities.checkString(typeNew)){
                    Toast.makeText(getContext(), R.string.invalid_type, Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ch = db.addType(user.getUid(), checked.equalsIgnoreCase(getString(R.string.income))? getString(R.string.FALSE) : getString(R.string.TRUE), typeNew, R.drawable.custom_type);
                if(ch) {
                    Toast.makeText(view.getContext(), R.string.type_added, Toast.LENGTH_SHORT).show();
                    startRecycler();
                    type.setText("");
                } else {
                    Toast.makeText(view.getContext(), R.string.already_exists, Toast.LENGTH_SHORT).show();
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
