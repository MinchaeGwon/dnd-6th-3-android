package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.dto.MonthlyDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MonthlyDetailContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MonthlyDetail> expenditureList;
    private boolean etc;

    public MonthlyDetailContentAdapter(ArrayList<MonthlyDetail> expenditureList, boolean etc) {
        this.expenditureList = expenditureList;
        this.etc = etc;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_monthly_detail_content, parent, false);
        return new ExpenditureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpenditureViewHolder) {
            MonthlyDetail detail = expenditureList.get(position);
            ((ExpenditureViewHolder) holder).setItem(detail);
        }
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    private class ExpenditureViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvCategory;
        TextView tvExpense;

        public ExpenditureViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tv_name_content_monthly_detail);
            tvCategory = itemView.findViewById(R.id.tv_name_content_monthly_category);
            tvExpense = itemView.findViewById(R.id.tv_amount_content_month);
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(MonthlyDetail detail) {
            tvContent.setText(detail.getExpenseDetail());

            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String expense = myFormatter.format(detail.getExpense());
            tvExpense.setText(expense + "원");

            if (etc) {
                tvCategory.setVisibility(View.VISIBLE);
            } else {
                tvCategory.setVisibility(View.GONE);
            }
        }
    }
}
