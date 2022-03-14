package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.MonthlyDetailActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.GoalCategoryInfo;
import com.dnd.moneyroutine.dto.MonthlyDiary;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

// 다이어리 월별 카테고리, 소비내역 주/월별 카테고리에 사용되는 adpater
public class ExpenditureCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MonthlyDiary> monthlyList;
    private ArrayList<GoalCategoryInfo> categoryList;
    private boolean diary;
    private boolean week;

    private LocalDate startDate;
    private LocalDate endDate;

    public ExpenditureCategoryAdapter(ArrayList<MonthlyDiary> monthlyList, boolean diary) {
        this.monthlyList = monthlyList;
        this.diary = diary;
    }

    public ExpenditureCategoryAdapter(ArrayList<GoalCategoryInfo> categoryList, boolean diary, boolean week) {
        this.categoryList = categoryList;
        this.diary = diary;
        this.week = week;
    }

    public ExpenditureCategoryAdapter(ArrayList<GoalCategoryInfo> categoryList, boolean diary, boolean week, LocalDate startDate, LocalDate endDate) {
        this.categoryList = categoryList;
        this.diary = diary;
        this.week = week;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_expenditure_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            if (diary) {
                MonthlyDiary monthly = monthlyList.get(position);
                ((CategoryViewHolder) holder).setDiaryItem(monthly);
            } else {
                GoalCategoryInfo category = categoryList.get(position);
                ((CategoryViewHolder) holder).setExpenditureItem(category);
            }
        }
    }

    @Override
    public int getItemCount() {
        return diary ? monthlyList.size() : categoryList.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {
        View colorView;
        TextView tvCategoryName;
        TextView tvCategoryCnt;
        TextView tvCategoryMore;
        ImageView ivHold;
        ImageView ivBottomMore;
        ImageView ivRightMore;
        RecyclerView rvExpense;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            colorView = itemView.findViewById(R.id.view_ex_color);
            tvCategoryName = itemView.findViewById(R.id.tv_ex_cat_name);
            tvCategoryCnt = itemView.findViewById(R.id.tv_ex_cat_cnt);
            tvCategoryMore = itemView.findViewById(R.id.tv_ex_cat_more);
            ivHold = itemView.findViewById(R.id.iv_ex_cat_hold);
            ivBottomMore = itemView.findViewById(R.id.iv_ex_bottom_more);
            ivRightMore = itemView.findViewById(R.id.iv_ex_right_more);
            rvExpense = itemView.findViewById(R.id.rv_ex_expense);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (diary || week) {
                        if (rvExpense.getVisibility() == View.GONE) {
                            rvExpense.setVisibility(View.VISIBLE);
                            ivHold.setVisibility(View.VISIBLE);
                            ivBottomMore.setVisibility(View.GONE);

                            if (diary) {
                                tvCategoryMore.setText("접기");
                            }
                        } else {
                            rvExpense.setVisibility(View.GONE);
                            ivHold.setVisibility(View.GONE);
                            ivBottomMore.setVisibility(View.VISIBLE);

                            if (diary) {
                                tvCategoryMore.setText("더보기");
                            }
                        }
                    } else {
                        Intent intent = new Intent(context, MonthlyDetailActivity.class);

                        GoalCategoryInfo category = categoryList.get(getBindingAdapterPosition());

                        intent.putExtra("totalExpense", category.getExpense());
                        intent.putExtra("percentage", category.getPercentage());
                        intent.putExtra("categoryName", category.getCategoryName());
                        intent.putExtra("categoryType", category.getCategoryType());

                        intent.putExtra("startDate", startDate);
                        intent.putExtra("endDate", endDate);

                        context.startActivity(intent);
                    }
                }
            });
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setDiaryItem(MonthlyDiary monthly) {
            setCategoryColor(getBindingAdapterPosition());

            tvCategoryName.setText(monthly.getName());
            tvCategoryCnt.setText(monthly.getCount() + "번");

            MonthlyDiaryAdapter monthlyDiaryAdapter = new MonthlyDiaryAdapter(monthly.getExpenditureList());
            rvExpense.setAdapter(monthlyDiaryAdapter);
            rvExpense.setLayoutManager(new LinearLayoutManager(context));
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setExpenditureItem(GoalCategoryInfo category) {
            setCategoryColor(getBindingAdapterPosition());

            tvCategoryName.setText(category.getCategoryName());
            tvCategoryCnt.setText(category.getPercentage() + "%");

            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String expense = decimalFormat.format(category.getExpense()) + "원";
            tvCategoryMore.setText(expense);
            tvCategoryMore.setTextColor(Color.parseColor("#212529"));

            WeeklyExpenditureAdapter weeklyExpenditureAdapter = new WeeklyExpenditureAdapter(category.getExpenditureList());
            rvExpense.setAdapter(weeklyExpenditureAdapter);
            rvExpense.setLayoutManager(new LinearLayoutManager(context));

            if (week) {
                ivRightMore.setVisibility(View.GONE);
                ivBottomMore.setVisibility(View.VISIBLE);
            } else {
                ivRightMore.setVisibility(View.VISIBLE);
                ivBottomMore.setVisibility(View.GONE);
            }
        }

        private void setCategoryColor(int index) {
            switch (index) {
                case 0:
                    colorView.setBackgroundResource(R.drawable.square_c896fa);
                    tvCategoryCnt.setTextColor(Color.parseColor("#8F30E9"));
                    break;
                case 1:
                    colorView.setBackgroundResource(R.drawable.square_a3bcff);
                    tvCategoryCnt.setTextColor(Color.parseColor("#2F6EE0"));
                    break;
                case 2:
                    colorView.setBackgroundResource(R.drawable.square_7ae2f9);
                    tvCategoryCnt.setTextColor(Color.parseColor("#0C6672"));
                    break;
                case 3:
                    colorView.setBackgroundResource(R.drawable.square_ced4da);
                    tvCategoryCnt.setTextColor(Color.parseColor("#868E96"));
                    break;
            }
        }
    }
}
