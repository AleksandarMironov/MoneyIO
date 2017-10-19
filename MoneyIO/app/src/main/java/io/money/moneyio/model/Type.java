package io.money.moneyio.model;


public class Type {

    private String isExpense;
    private String type;
    private int pictureId;

    public Type(String isExpense, String type, int pid) {
        this.isExpense = isExpense;
        this.type = type;
        this.pictureId = pid;
    }

    public boolean isExpense() {
        return true;
    }

    public String getType() {
        return type;
    }

    public int getPictureId() {
        return pictureId;
    }
}
