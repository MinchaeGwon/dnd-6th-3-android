package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.dto.GoalCategoryCompact;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

// 소비입력, 소비 상세 내역 : 목표 카테고리에 사용
public class GoalCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(GoalCategoryCompact category);
    }

    private Context context;
    private ArrayList<GoalCategoryCompact> categoryList;
    private GoalCategoryCompact selectCat;

    private boolean detail = false;
    private int detailCatId;
    private boolean detailCustom;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public GoalCategoryGridAdapter(ArrayList<GoalCategoryCompact> categoryList) {
        this.categoryList = categoryList;
    }

    public GoalCategoryGridAdapter(ArrayList<GoalCategoryCompact> categoryList, int detailCatId, boolean detailCustom, boolean detail) {
        this.categoryList = categoryList;
        this.detailCatId = detailCatId;
        this.detailCustom = detailCustom;
        this.detail = detail;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal_category_grid, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            GoalCategoryCompact category = categoryList.get(position);
            ((CategoryViewHolder) holder).setItem(category);

            if (detail) {
                holder.itemView.setBackgroundResource(detailCatId == category.getCategoryId() ?
                        R.drawable.button_category_clicked : R.drawable.button_category_unclicked);

                ((CategoryViewHolder) holder).tvDetail.setTextColor(detailCatId == category.getCategoryId() ?
                        Color.parseColor("#212529") : Color.parseColor("#868E96"));

                if (!category.isCustom() && !detailCustom) {
                    ((CategoryViewHolder) holder).ivCategory.setImageResource(detailCatId == category.getCategoryId() ?
                            Common.getBasicColorCategoryResId(category.getName()) : Common.getBasicGrayCategoryResId(category.getName()));
                }
            } else {
                int categoryId = selectCat != null ? selectCat.getCategoryId() : -1;

                holder.itemView.setBackgroundResource(categoryId == category.getCategoryId() ?
                        R.drawable.button_category_clicked : R.drawable.button_category_unclicked);

                ((CategoryViewHolder) holder).tvDetail.setTextColor(categoryId == category.getCategoryId() ?
                        Color.parseColor("#212529") : Color.parseColor("#868E96"));

                if (!category.isCustom()) {
                    ((CategoryViewHolder) holder).ivCategory.setImageResource(categoryId == category.getCategoryId() ?
                            Common.getBasicColorCategoryResId(category.getName()) : Common.getBasicGrayCategoryResId(category.getName()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvEmoji;

        TextView tvCategory;
        TextView tvDetail;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategory = itemView.findViewById(R.id.iv_basic_category);
            tvEmoji = itemView.findViewById(R.id.tv_custom_category);

            tvCategory = itemView.findViewById(R.id.tv_category_name);
            tvDetail = itemView.findViewById(R.id.tv_category_ex);

            // 상세 보기가 아닌 경우에만 리스너 등록
            if (!detail) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectCat != null) {
                            notifyItemChanged(categoryList.indexOf(selectCat));
                        }

                        selectCat = categoryList.get(getBindingAdapterPosition());
                        notifyItemChanged(categoryList.indexOf(selectCat));

                        onItemClickListener.onClick(selectCat);
                    }
                });
            }
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(GoalCategoryCompact category) {
            if (category.isCustom()) {
                ivCategory.setVisibility(View.INVISIBLE);
                tvEmoji.setVisibility(View.VISIBLE);

                String emoji = null;
                try {
                    emoji = URLDecoder.decode(category.getEmoji(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                tvEmoji.setText(emoji);
            } else {
                ivCategory.setVisibility(View.VISIBLE);
                tvEmoji.setVisibility(View.INVISIBLE);

                ivCategory.setImageResource(Common.getBasicGrayCategoryResId(category.getName()));
            }

            tvCategory.setText(category.getName());
            tvDetail.setText(category.getDetail());
        }
    }
}
