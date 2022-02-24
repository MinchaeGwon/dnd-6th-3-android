package com.dnd.moneyroutine.enums;

public enum GoalStateEnum {
    SUCCESS("성공"), PROCEED("진행중"), FAIL("실패");

    private String state;

    GoalStateEnum(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
