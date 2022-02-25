package com.dnd.moneyroutine.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpenseForm implements Serializable {
    private int categoryId;
    private boolean custom;
    private String date;
    private int expense;
    private String expenseDetail;
    private String emotion;
    private String emotionDetail;
}
