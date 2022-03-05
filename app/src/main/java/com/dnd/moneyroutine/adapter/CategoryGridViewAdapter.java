package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.dnd.moneyroutine.CategoryGridItemView;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.CategoryItem;

import java.util.ArrayList;

public class CategoryGridViewAdapter extends BaseAdapter {

    ArrayList<CategoryItem> items = new ArrayList<CategoryItem>();


    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(CategoryItem item) {
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
        CategoryItem categoryItem = items.get(position);


        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_category, viewGroup, false);

            ImageView iv_category_icon = view.findViewById(R.id.iv_category_icon);
            TextView tv_category_icon = view.findViewById(R.id.ic_category_icon);
            TextView tv_category_name = (TextView) view.findViewById(R.id.tv_category_name);
            TextView tv_category_ex = (TextView) view.findViewById(R.id.tv_category_ex);

            if (categoryItem.getCategoryIcon().contains("@drawable/")){ //기본 카테고리 선택시
                tv_category_icon.setVisibility(View.INVISIBLE);
                iv_category_icon.setVisibility(View.VISIBLE);
                int resId = context.getResources().getIdentifier( categoryItem.getCategoryIcon(), "drawable", context.getPackageName());
                iv_category_icon.setImageResource(resId); //imageview에 이미지 띄움
            } else{
                iv_category_icon.setVisibility(View.INVISIBLE);
                tv_category_icon.setVisibility(View.VISIBLE);
                tv_category_icon.setText(categoryItem.getCategoryIcon()); //textview에 아이콘 띄움
            }

            tv_category_name.setText(categoryItem.getCategoryName());
            tv_category_ex.setText(categoryItem.getCategoryEx());
        }

        return view;
    }
}
