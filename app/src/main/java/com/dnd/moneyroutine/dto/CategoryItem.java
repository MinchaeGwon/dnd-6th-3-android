package com.dnd.moneyroutine.dto;

import java.io.Serializable;

public class CategoryItem implements Serializable {

    String categoryIcon;
    //    int categoryIcon;
    String categoryName;
    String categoryEx;
//    boolean isSelected;


    public CategoryItem(String categoryIcon, String categoryName, String categoryEx) {
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.categoryEx = categoryEx;
//        this.isSelected=isSelected;
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

    public String getCategoryEx() {
        return categoryEx;
    }

    public void setCategoryEx(String categoryEx) {
        this.categoryEx = categoryEx;
    }

}
