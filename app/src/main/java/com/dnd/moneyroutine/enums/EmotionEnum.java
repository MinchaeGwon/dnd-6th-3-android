package com.dnd.moneyroutine.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmotionEnum {
    GOOD("GOOD", "행복했어", "만족"), SOSO("SOSO", "그냥 그랬어", "보통"), BAD("BAD", "왜 썼지", "불만족");

    private String emotion;
    private String name;
    private String detail;
}
