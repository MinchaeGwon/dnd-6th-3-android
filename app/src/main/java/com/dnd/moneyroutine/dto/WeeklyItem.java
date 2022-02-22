package com.dnd.moneyroutine.dto;

public class WeeklyItem {

    private String spendDate;
    private String name;
    private String category;
    private String amount;

    public WeeklyItem(String spendDate, String name, String category, String amount) {
        this.spendDate = spendDate;
        this.name = name;
        this.category = category;
        this.amount = amount;
    }

    public String getSpendDate() {
        return spendDate;
    }

    public void setSpendDate(String spendDate) {
        this.spendDate = spendDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
