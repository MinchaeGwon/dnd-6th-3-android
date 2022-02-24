package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MonthlyDiary implements Serializable {
    private String name;
    private int count;

    @SerializedName("expenditureDiaryDtoList")
    private ArrayList<ExpenditureCompact> expenditureList;
}
