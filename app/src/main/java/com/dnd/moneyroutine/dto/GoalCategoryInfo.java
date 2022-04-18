package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoalCategoryInfo implements Serializable {
    private CategoryType categoryType;
    private String categoryName;
    private int percentage;
    private int expense;

    @SerializedName("weeklyExpenditureDetailDtoList")
    private ArrayList<ExpenditureDetail> expenditureList;

    // 나머지 카테고리를 위해 추가한 것 : 월별 상세 소비 내역 카테고리에 사용
    private ArrayList<String> etcCategoryNames;
    private ArrayList<CategoryType> etcCategoryTypes;
}
