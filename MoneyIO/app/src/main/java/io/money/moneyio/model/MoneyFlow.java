package io.money.moneyio.model;


import java.util.Calendar;

public class MoneyFlow {
    private String expense;
    private String type;
    private long calendar;
    private String comment;
    private float sum;
    private String uid;

    //this constructor is required for firebase
    public MoneyFlow() {
    }

    public MoneyFlow(String uid, String expense, String type, String comment, float sum) {
        this.expense = expense;
        this.type = type;
        this.calendar = Calendar.getInstance().getTimeInMillis();
        this.comment = comment;
        this.sum = sum;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }
}
