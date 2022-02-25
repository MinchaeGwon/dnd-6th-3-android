package com.dnd.moneyroutine.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureDetailDto implements Serializable {
    @SerializedName("date")
    private String date;
    private int expense;
    private String expenseDetail;
}
