package com.dnd.moneyroutine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomCategoryModel {
    private String detail;
    private String name;


    public CustomCategoryModel(String detail, String name) {
        this.detail = detail;
        this.name = name;
    }

//    @Override
//    public String toString() {
//        return "CustomCategoryModel{" +
//                "detail='" + detail + '\'' +
//                ", name='" + name + '\'' +
//                '}';
//    }

}
