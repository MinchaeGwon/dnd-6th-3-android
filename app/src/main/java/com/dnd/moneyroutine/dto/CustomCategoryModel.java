package com.dnd.moneyroutine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class CustomCategoryModel {

    private String detail;
    private String name;

    public CustomCategoryModel(String detail, String name){
        this.detail=detail;
        this.name=name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CustomCategoryModel{" +
                "detail='" + detail + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
