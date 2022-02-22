package com.dnd.moneyroutine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.WeeklyItem;

import java.util.ArrayList;

public class WeeklyDetailAdapter extends RecyclerView.Adapter<WeeklyDetailAdapter.ViewHolder> {

    private ArrayList<WeeklyItem> weeklyItems = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expenditure_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyDetailAdapter.ViewHolder holder, int position) {

        holder.onBind(weeklyItems.get(position));
    }

    @Override
    public int getItemCount() {
        return weeklyItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvName;
        private TextView tvCategory;
        private TextView tvAmount;

        public ViewHolder(@NonNull View v) {
            super(v);

            tvDate = v.findViewById(R.id.tv_date_week_detail);
            tvName = v.findViewById(R.id.tv_name_week_detail);
            tvCategory = v.findViewById(R.id.tv_category_week_detail);
            tvAmount = v.findViewById(R.id.tv_amount_week_detail);
        }

        public void onBind(WeeklyItem weeklyItem) {
            tvDate.setText(weeklyItem.getSpendDate());
            tvName.setText(weeklyItem.getName());
            tvCategory.setText(weeklyItem.getCategory());
            tvAmount.setText(weeklyItem.getAmount());
        }
    }
}
