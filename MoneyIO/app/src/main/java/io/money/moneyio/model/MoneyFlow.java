package io.money.moneyio.model;


import java.util.Calendar;

public class MoneyFlow {
    private boolean isExpense;
    private String type;
    private long calendar;
    private String comment;
    private double sum;

    public MoneyFlow() {
    }

    public MoneyFlow(boolean isExpense, String type, String comment, double sum) {
        this.isExpense = isExpense;
        this.type = type;
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
        return type;
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
