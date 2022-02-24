package com.dnd.moneyroutine.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpenditureDetail implements Serializable {
    private int expenditureId;
    private int categoryId;
    private String name;
    private String detail;
    private LocalDate date;
    private int expense;
    private String expenseDetail;
    private String emotionDetail;
    private boolean custom;
}
