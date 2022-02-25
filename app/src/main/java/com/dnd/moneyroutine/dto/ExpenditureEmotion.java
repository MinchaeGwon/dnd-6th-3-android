package com.dnd.moneyroutine.dto;

import com.dnd.moneyroutine.enums.EmotionEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpenditureEmotion {
    private EmotionEnum emotion;
    private int expenditureId;
}
