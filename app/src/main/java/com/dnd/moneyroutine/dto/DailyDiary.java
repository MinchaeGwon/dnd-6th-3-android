package com.dnd.moneyroutine.dto;

import com.dnd.moneyroutine.enums.EmotionEnum;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DailyDiary implements Serializable {
    private EmotionEnum emotion;
    private int count;
    private int amount;

    @SerializedName("expenditureDtoList")
    private ArrayList<ExpenditureDetail> expenditureList;
}
