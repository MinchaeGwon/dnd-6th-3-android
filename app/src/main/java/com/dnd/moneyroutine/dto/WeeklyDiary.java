package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeeklyDiary {
    private LocalDate date;

    @SerializedName("dailyExpenditureEmotionDtoList")
    private ArrayList<ExpenditureEmotion> expenditureList;
}
