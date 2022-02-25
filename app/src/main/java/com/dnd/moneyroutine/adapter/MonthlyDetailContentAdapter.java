package com.dnd.moneyroutine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.BudgetItem;
import com.dnd.moneyroutine.dto.ExpenditureDetailDto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MonthlyDetailContentAdapter extends RecyclerView.Adapter<MonthlyDetailContentAdapter.ViewHolder> {


    ArrayList<ExpenditureDetailDto> expenditureDetailList;

    public MonthlyDetailContentAdapter(ArrayList<ExpenditureDetailDto> list) {
        this.expenditureDetailList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_detail_content, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyDetailContentAdapter.ViewHolder holder, int position) {
        holder.onBind(expenditureDetailList.get(position));
    }

    @Override
    public int getItemCount() {
        return expenditureDetailList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.tv_name_content_monthly_detail);
            tvAmount=itemView.findViewById(R.id.tv_amount_content_month);

        }

        private void onBind(ExpenditureDetailDto expenditureDetailDto) {
            tvName.setText(expenditureDetailDto.getExpenseDetail());
            tvAmount.setText(expenditureDetailDto.getExpense()+"원");

        }

    }
}
