package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.dto.CategoryCompact;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class CategoryGridViewAdapter extends BaseAdapter {

    ArrayList<CategoryCompact> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(CategoryCompact item) {
        items.add(item);
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();
        CategoryCompact category = items.get(position);


        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_category, viewGroup, false);

            ImageView ivCategoryIcon = view.findViewById(R.id.iv_category_icon);
            TextView tvEmoji = view.findViewById(R.id.ic_category_icon);
            TextView tvCategoryName = (TextView) view.findViewById(R.id.tv_category_name);
            TextView tvCategoryDetail = (TextView) view.findViewById(R.id.tv_category_ex);

            if (category.isCustom()) {
                ivCategoryIcon.setVisibility(View.INVISIBLE);
                tvEmoji.setVisibility(View.VISIBLE);

                String emoji = null;
                try {
                    emoji = URLDecoder.decode(category.getEmoji(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                tvEmoji.setText(emoji);
            } else {
                ivCategoryIcon.setVisibility(View.VISIBLE);
                tvEmoji.setVisibility(View.INVISIBLE);

                ivCategoryIcon.setImageResource(Common.getBasicGrayCategoryResId(category.getName()));
            }

            tvCategoryName.setText(category.getName());
            tvCategoryDetail.setText(category.getDetail());
        }

        return view;
    }
}
