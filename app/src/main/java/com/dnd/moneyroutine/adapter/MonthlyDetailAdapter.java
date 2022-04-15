package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.dto.MonthlyExpenditure;

import java.util.ArrayList;

public class MonthlyDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MonthlyExpenditure> monthlyList;
    private boolean etc;

    public MonthlyDetailAdapter(ArrayList<MonthlyExpenditure> monthlyList, boolean etc) {
        this.monthlyList = monthlyList;
        this.etc = etc;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_monthly_detail, parent, false);
        return new MonthlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MonthlyViewHolder) {
            MonthlyExpenditure monthlyExpenditure = monthlyList.get(position);
            ((MonthlyViewHolder) holder).setItem(monthlyExpenditure);
        }
    }

    @Override
    public int getItemCount() {
        return monthlyList.size();
    }

    private class MonthlyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView rvDetail;

        public MonthlyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date_monthly_detail);
            rvDetail = itemView.findViewById(R.id.rv_content_monthly_detail);
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(MonthlyExpenditure monthly) {
            tvDate.setText(Common.getExpenseLocalToString(monthly.getDate()));

            MonthlyDetailContentAdapter monthlyDetailContentAdapter = new MonthlyDetailContentAdapter(monthly.getExpenditureList(), etc);
            rvDetail.setLayoutManager(new LinearLayoutManager(context));
            rvDetail.setAdapter(monthlyDetailContentAdapter);
        }
    }
}
