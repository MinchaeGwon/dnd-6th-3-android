package com.dnd.moneyroutine.enums;

public enum EmotionEnum {
    GOOD("GOOD", "만족"), SOSO("SOSO", "보통"), BAD("BAD", "불만족");

    private String emotion;
    private String detail;

    EmotionEnum(String emotion, String detail) {
        this.emotion = emotion;
        this.detail = detail;
    }

    public String getEmotion() {
        return this.emotion;
    }

    public String getDetail() {
        return this.detail;
    }
}
