package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class GoalCategoryCreateDtoList implements Serializable {

    private int budget;
    private int categoryId;

    private Boolean isCustom;

    public GoalCategoryCreateDtoList(){
    }

    public GoalCategoryCreateDtoList(int budget, int categoryId,  Boolean isCustom) {
        this.isCustom = isCustom;
        this.categoryId = categoryId;
        this.budget = budget;
    }

    public Boolean getCustom() {
        return isCustom;
    }

    public void setCustom(Boolean custom) {
        isCustom = custom;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "GoalCategoryCreateDtoList{" +
                "isCustom=" + isCustom +
                ", categoryId='" + categoryId + '\'' +
                ", budget='" + budget + '\'' +
                '}';
    }
}
