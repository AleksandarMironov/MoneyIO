package io.money.moneyio.model.utilities;


import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.NumberPicker;

import io.money.moneyio.R;

@SuppressLint("InflateParams")
public class MonthYearPicker {

    private static final int MIN_YEAR = 1970;

    private static final int MAX_YEAR = 2099;

    private static final String[] PICKER_DISPLAY_MONTHS_NAMES = new String[]
            { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    private static final String[] MONTHS = new String[]
            { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

    private View view;
    private Activity activity;
    private AlertDialog.Builder builder;
    private AlertDialog pickerDialog;
    private boolean build = false;
    private NumberPicker monthNumberPicker;
    private NumberPicker yearNumberPicker;
    private int currentYear;
    private int currentMonth;

   // create new instance
    public MonthYearPicker(Context activity) {
        this.activity = (Activity)activity;
        this.view = this.activity.getLayoutInflater().inflate(R.layout.month_year_picker_view, null);
    }

    //builds the month year alert dialog. Must be build before showing!
    public void build(DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        this.build(-1, -1, positiveButtonListener, negativeButtonListener, true, true);
    }

    /**
     * Builds the month year alert dialog.
     *
     * @param selectedMonth
     *            the selected month 0 to 11 (sets current moth if invalid
     *            value)
     * @param selectedYear
     *            the selected year 1970 to 2099 (sets current year if invalid
     *            value)
     * @param positiveButtonListener
     *            the positive listener
     * @param negativeButtonListener
     *            the negative listener
     */
    public void build(int selectedMonth, int selectedYear, DialogInterface.OnClickListener positiveButtonListener,
                      DialogInterface.OnClickListener negativeButtonListener, boolean monthVisibility, boolean yearVisibility) {

        Calendar instance = Calendar.getInstance();
        currentMonth = instance.get(Calendar.MONTH);
        currentYear = instance.get(Calendar.YEAR);

        if (selectedMonth > 11 || selectedMonth < -1) {
            selectedMonth = currentMonth;
        }

        if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR) {
            selectedYear = currentYear;
        }

        if (selectedMonth == -1) {
            selectedMonth = currentMonth;
        }

        if (selectedYear == -1) {
            selectedYear = currentYear;
        }

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        monthNumberPicker = (NumberPicker) view.findViewById(R.id.monthNumberPicker);
        monthNumberPicker.setDisplayedValues(PICKER_DISPLAY_MONTHS_NAMES);

        monthNumberPicker.setMinValue(0);
        monthNumberPicker.setMaxValue(MONTHS.length - 1);

        yearNumberPicker = (NumberPicker) view.findViewById(R.id.yearNumberPicker);
        yearNumberPicker.setMinValue(MIN_YEAR);
        yearNumberPicker.setMaxValue(MAX_YEAR);

        monthNumberPicker.setValue(selectedMonth);
        yearNumberPicker.setValue(selectedYear);

        monthNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        builder.setTitle("Please select");
        builder.setPositiveButton("OK", positiveButtonListener);
        builder.setNegativeButton("Cancel", negativeButtonListener);
        builder.setCancelable(false);

        setMonthVisibility(monthVisibility);
        setYearVisibility(yearVisibility);
        build = true;
        pickerDialog = builder.create();

    }


    //Show month year picker dialog. Build first!!!!
    public void show() {
        try {
            if (build) {
                pickerDialog.show();
            } else {
                throw new IllegalStateException("Build picker before use");
            }
        }
        catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    //Gets the selected month.
    public int getSelectedMonth() {
        return monthNumberPicker.getValue();
    }

    //Gets the selected month namee
    public String getSelectedMonthName() {
        return MONTHS[monthNumberPicker.getValue()];
    }

    //Gets the selected month name.
    public String getSelectedMonthShortName() {
        return PICKER_DISPLAY_MONTHS_NAMES[monthNumberPicker.getValue()];
    }

    //Gets the selected year.
    public int getSelectedYear() {
        return yearNumberPicker.getValue();
    }

    //Gets the current year.
    public int getCurrentYear() {
        return currentYear;
    }

    // Gets the current month.
    public int getCurrentMonth() {
        return currentMonth;
    }

    //Sets the month value changed listener.
    public void setMonthValueChangedListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        monthNumberPicker.setOnValueChangedListener(valueChangeListener);
    }

    //Sets the year value changed listener.
    public void setYearValueChangedListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        yearNumberPicker.setOnValueChangedListener(valueChangeListener);
    }

    // Sets the month wrap selector wheel.
    public void setMonthWrapSelectorWheel(boolean wrapSelectorWheel) {
        monthNumberPicker.setWrapSelectorWheel(wrapSelectorWheel);
    }

    //Sets the year wrap selector wheel.
    public void setYearWrapSelectorWheel(boolean wrapSelectorWheel) {
        yearNumberPicker.setWrapSelectorWheel(wrapSelectorWheel);
    }

    private void setYearVisibility(boolean isVisible){
        if(isVisible){
            yearNumberPicker.setVisibility(View.VISIBLE);
        } else {
            yearNumberPicker.setVisibility(View.GONE);
        }
    }

    private void setMonthVisibility(boolean isVisible){
        if(isVisible){
            monthNumberPicker.setVisibility(View.VISIBLE);
        } else {
            monthNumberPicker.setVisibility(View.GONE);
        }
    }

}
