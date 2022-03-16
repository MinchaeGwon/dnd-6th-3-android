package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.ExpenditureCompact;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MonthlyDiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ExpenditureCompact> expenditureList;

    public MonthlyDiaryAdapter(ArrayList<ExpenditureCompact> expenditureList) {
        this.expenditureList = expenditureList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary_monthly_expense, parent, false);
        return new ExpenditureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpenditureViewHolder) {
            ExpenditureCompact expenditure = expenditureList.get(position);
            ((ExpenditureViewHolder) holder).setItem(expenditure);
        }
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    private class ExpenditureViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseDetail;
        TextView tvEmotionDetail;
        TextView tvCategoryName;
        TextView tvDate;

        public ExpenditureViewHolder(@NonNull View itemView) {
            super(itemView);

            tvExpenseDetail = itemView.findViewById(R.id.tv_month_expense_content);
            tvEmotionDetail = itemView.findViewById(R.id.tv_month_emotion_content);
            tvCategoryName = itemView.findViewById(R.id.tv_month_expense_category);
            tvDate = itemView.findViewById(R.id.tv_month_date);
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(ExpenditureCompact expenditure) {
            tvExpenseDetail.setText(expenditure.getExpenseDetail());
            tvEmotionDetail.setText(expenditure.getEmotionDetail());
            tvCategoryName.setText(expenditure.getName());

            LocalDate today = LocalDate.now();
            LocalDate date = expenditure.getDate();
            if (date.getYear() != today.getYear()) {
                tvDate.setText(date.format(DateTimeFormatter.ofPattern("y.M.d")));
            } else {
                tvDate.setText(date.format(DateTimeFormatter.ofPattern("M.d")));
            }
        }
    }
}
