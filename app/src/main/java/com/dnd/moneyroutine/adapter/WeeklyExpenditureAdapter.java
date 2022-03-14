package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.ExpenditureDetailDto;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

// 소비내역 탭 : 주별 상세 내역에 사용되는 adpater
public class WeeklyExpenditureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ExpenditureDetailDto> expenditureList;

    public WeeklyExpenditureAdapter(ArrayList<ExpenditureDetailDto> expenditureList) {
        this.expenditureList = expenditureList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expenditure_content, parent, false);
        return new ExpenditureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpenditureViewHolder) {
            ExpenditureDetailDto detail = expenditureList.get(position);
            ((ExpenditureViewHolder) holder).setItem(detail);
        }
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    private class ExpenditureViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvContent;
        TextView tvCategoryName;
        TextView tvExpense;

        public ExpenditureViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date_week_detail);
            tvContent = itemView.findViewById(R.id.tv_name_week_detail);
            tvCategoryName = itemView.findViewById(R.id.tv_category_week_detail);
            tvExpense = itemView.findViewById(R.id.tv_amount_week_detail);
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(ExpenditureDetailDto expenditure) {
            LocalDate today = LocalDate.now();
            LocalDate date = expenditure.getDate();
            if (date.getYear() != today.getYear()) {
                tvDate.setText(date.getYear() + "." + date.getMonthValue() + "." +date.getDayOfMonth());
            } else {
                tvDate.setText(date.getMonthValue() + "." +date.getDayOfMonth());
            }

            tvContent.setText(expenditure.getExpenseDetail());
            tvCategoryName.setText(expenditure.getCategory());

            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String expense = decimalFormat.format(expenditure.getExpense()) + "원";
            tvExpense.setText(expense);
        }
    }
}
