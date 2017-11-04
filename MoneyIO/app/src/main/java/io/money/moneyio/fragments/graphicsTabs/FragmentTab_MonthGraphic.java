package io.money.moneyio.fragments.graphicsTabs;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.utilities.GraphicUtilities;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_MonthGraphic extends Fragment {

    private View view;
    private EditText editDate;
    private DatabaseHelperFirebase fdb;
    private List<MoneyFlow> filteredArr;
    private PieChart pieChart;
    private BarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private List<MoneyFlow> moneyFlowData; ///filtered arr, equal to Utilities.data onCreate
    private Calendar calendar;
    private MonthYearPicker monthYearPicker;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;
    private int year, month;
    private TextView income, expense, overall;
    private ImageView questionPie, questionCombined, questionHorizontal, plusMinus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialiseElements();
        filterDataOnStart();
        setFilterClickListener();
        setSpinnerSettings();
        setQuestionListeners();
        return view;
    }
    //method used for initialisations
    private void initialiseElements() {
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        moneyFlowData = fdb.getData();
        pieChart = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = (BarChart) view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        monthYearPicker = new MonthYearPicker(view.getContext());
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.graphics_date_edit);
        editDate.setText("" + calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH)+1));
        spinner = view.findViewById(R.id.statistics_spinner_menu);
        spinnerPosition = 0;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        income = view.findViewById(R.id.statistics_income_bar);
        expense = view.findViewById(R.id.statistics_expense_bar);
        overall = view.findViewById(R.id.statistics_overall_bar);
        questionPie = view.findViewById(R.id.statistics_question_pie);
        questionCombined = view.findViewById(R.id.statistics_question_combined);
        questionHorizontal = view.findViewById(R.id.statistics_question_horizontal_bar);
        plusMinus = view.findViewById(R.id.ststistics_plusminus);
    }

    public void setQuestionListeners(){
        questionPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.pie_chart) +
                getString(R.string.pc_text));
            }
        });

        questionHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.horizontal_bar_chart) +
                        getString(R.string.hbc_month_text));
            }
        });
    }

    private void setSpinnerSettings() {
        //creating spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.history_spinner, R.layout.support_simple_spinner_dropdown_item);
        //set the theme of the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        //setting the spinner's adapter
        spinner.setAdapter(adapter);

        //spinner item selected listener functionality
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                calendar.set(year, month, 1, 0, 0, 0);
                start = calendar.getTimeInMillis();

                if(month == 12){
                    calendar.set(Calendar.YEAR, year + 1);
                    calendar.set(Calendar.MONTH, 1);
                } else {
                    calendar.set(Calendar.MONTH, month + 1);
                }
                end = calendar.getTimeInMillis();

                filteredArr = DatabaseHelperFirebase.filterData(start, end, position);
                spinnerPosition = position;
                incomeExpenseDay();
            }

            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    private void setFilterClickListener(){
        //edit text with calendar settings click listener for changing the date
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //date picker settings
                        calendar.set(monthYearPicker.getSelectedYear(), monthYearPicker.getSelectedMonth(), 1, 0,0,0);

                        start = calendar.getTimeInMillis();

                        if(monthYearPicker.getSelectedMonth() == 12){
                            calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);
                            calendar.set(Calendar.MONTH, 1);
                        } else {
                            calendar.set(Calendar.MONTH, monthYearPicker.getSelectedMonth() + 1);
                        }

                        end = calendar.getTimeInMillis();

                        filteredArr = fdb.filterData(start, end, spinnerPosition);
                        editDate.setText("" + monthYearPicker.getSelectedYear() + " / " + (monthYearPicker.getSelectedMonth()+1));
                        calendar = Calendar.getInstance();
                        monthYearPicker = new MonthYearPicker(view.getContext());
                        incomeExpenseDay();
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick,false, true, true);
                monthYearPicker.show();
            }
        });
    }
    //this method sets hour range which is passed to the filter array
    private void filterDataOnStart(){
        end = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        start = calendar.getTimeInMillis();
    }
    //start the charts and set them visible/gone
    private void incomeExpenseDay() {

        chart.setVisibility(View.GONE);
        questionCombined.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
        questionPie.setVisibility(View.VISIBLE);
        horizontalBarChart.setVisibility(View.VISIBLE);
        questionHorizontal.setVisibility(View.VISIBLE);

        GraphicUtilities.dataFilerForCurrentTab(income, expense, overall, filteredArr, plusMinus);
        GraphicUtilities.pieChart(pieChart, filteredArr);
        GraphicUtilities.horizontalBarChart(horizontalBarChart, filteredArr);
    }
}
