package io.money.moneyio.model;


import java.util.Calendar;

public class MoneyFlow {
    private String expense;
    private String type;
    private long calendar;
    private String comment;
    private double sum;

    public MoneyFlow() {
    }

    public MoneyFlow(String expense, String type, String comment, double sum) {
        this.expense = expense;
        this.type = type;
        this.calendar = Calendar.getInstance().getTimeInMillis();
        this.comment = comment;
        this.sum = sum;
    }

    public MoneyFlow(String expense, String type, double sum) {
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

    public double getSum() {
        return sum;
    }
}
