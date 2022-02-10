package com.dnd.moneyroutine.item;

import java.io.Serializable;

public class BudgetItem implements Serializable {

    int id;
    String categoryIcon;
//    int categoryIcon;
    String categoryName;


    public BudgetItem(int id, String categoryIcon, String categoryName){
        this.id=id;
        this.categoryIcon=categoryIcon;
        this.categoryName=categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}

