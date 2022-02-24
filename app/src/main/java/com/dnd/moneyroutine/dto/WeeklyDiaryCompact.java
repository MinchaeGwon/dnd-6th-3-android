package com.dnd.moneyroutine.dto;

import com.dnd.moneyroutine.enums.EmotionEnum;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyDiaryCompact {
    private String date;

    @SerializedName("emotion")
    private EmotionEnum emotion;
    private int expenditureId;

    public String toString() {
        return "date : " + date + ", emotion : " + emotion.getEmotion() + ", id : " + expenditureId;
    }
}
