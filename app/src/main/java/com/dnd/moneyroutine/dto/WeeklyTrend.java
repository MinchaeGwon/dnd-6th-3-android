package com.dnd.moneyroutine.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeeklyTrend {
    public LocalDate startDate;
    public LocalDate endDate;
    public int weekExpense;
}
