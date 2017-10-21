package io.money.moneyio.model;


import android.icu.text.LocaleDisplayNames;
import java.util.Calendar;

public class MoneyFlow {
    private boolean isExpense;
    private String Type;
    private long calendar;
    private String comment;
    private double sum;

    public MoneyFlow() {
    }

    public MoneyFlow(boolean isExpense, String type, String comment, double sum) {
        this.isExpense = isExpense;
        Type = type;
        this.calendar = Calendar.getInstance().getTimeInMillis();
        this.comment = comment;
        this.sum = sum;
    }

    public MoneyFlow(boolean isExpense, String type, double sum) {
        this(isExpense, type, "", sum);
    }

    public boolean isExpense() {
        return isExpense;
    }

    public String getType() {
        return Type;
    }

    public long getCalendar() {
        return calendar;
    }

    public String getComment() {
        return comment;
    }

    public double getSum() {
        return sum;
    }
}
