package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.ExpenseDetailActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.DiaryDetail;
import com.dnd.moneyroutine.enums.EmotionEnum;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WeeklyDiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private Context context;
    
    private EmotionEnum emotion;
    private ArrayList<DiaryDetail> expenditureList;
    
    public WeeklyDiaryAdapter(EmotionEnum emotion, ArrayList<DiaryDetail> expenditureList) {
        this.emotion = emotion;
        this.expenditureList = expenditureList;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary_weekly_expense, parent, false);
        return new ExpenditureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpenditureViewHolder) {
            DiaryDetail detail = expenditureList.get(position);
            ((ExpenditureViewHolder) holder).setItem(detail);
        }
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    private class ExpenditureViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvExpenseDetail;
        TextView tvExpense;
        TextView tvEmotionDetail;

        public ExpenditureViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_week_cat);
            tvExpenseDetail = itemView.findViewById(R.id.tv_week_expense_content);
            tvExpense = itemView.findViewById(R.id.tv_week_money);
            tvEmotionDetail = itemView.findViewById(R.id.tv_week_emotion_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ExpenseDetailActivity.class);
                    intent.putExtra("emotion", emotion);
                    intent.putExtra("expenditureDetail", expenditureList.get(getBindingAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(DiaryDetail detail) {
            tvCategory.setText(detail.getCategoryName());

            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String expense = myFormatter.format(detail.getExpense());
            tvExpense.setText(expense + "원");

            tvExpenseDetail.setText(detail.getExpenseDetail());
            tvEmotionDetail.setText(detail.getEmotionDetail());
        }
    }
}
