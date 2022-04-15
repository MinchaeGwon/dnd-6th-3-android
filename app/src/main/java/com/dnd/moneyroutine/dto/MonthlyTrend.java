package com.dnd.moneyroutine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MonthlyTrend {
    private int month;
    private int budget;
    private int monthExpense;
}
