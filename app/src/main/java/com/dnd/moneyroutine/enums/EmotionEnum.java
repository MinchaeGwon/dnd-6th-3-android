package com.dnd.moneyroutine.enums;

public enum EmotionEnum {
    GOOD("GOOD"), SOSO("SOSO"), BAD("BAD");

    private String emotion;

    EmotionEnum(String emotion) {
        this.emotion = emotion;
    }

    public String getEmotion() {
        return this.emotion;
    }
}
