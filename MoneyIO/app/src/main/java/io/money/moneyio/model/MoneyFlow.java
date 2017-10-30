package io.money.moneyio.model;


import java.util.Calendar;

public class MoneyFlow {
    private String expense;
    private String type;
    private long calendar;
    private String comment;
    private float sum;

    //this constructor is required for firebase
    public MoneyFlow() {
    }

    public MoneyFlow(String expense, String type, String comment, float sum) {
        this.expense = expense;
        this.type = type;
        this.calendar = Calendar.getInstance().getTimeInMillis();
        this.comment = comment;
        this.sum = sum;
    }

    public MoneyFlow(String expense, String type, float sum) {
        this(expense, type, "", sum);
    }

    public String getExpense() {
        return expense;
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

    public float getSum() {
        return sum;
    }
}
