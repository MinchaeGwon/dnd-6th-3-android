package com.dnd.moneyroutine.dto;

import java.time.LocalDate;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenditure implements Comparable<MonthlyExpenditure> {
    private LocalDate date;
    private ArrayList<MonthlyDetail> expenditureList;

    @Override
    public int compareTo(MonthlyExpenditure o) {
        return o.date.compareTo(date);
    }
}
