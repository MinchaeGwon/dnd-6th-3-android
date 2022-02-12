package com.dnd.moneyroutine.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.dnd.moneyroutine.CategoryGridItemView;
import com.dnd.moneyroutine.OnboardingCategoryActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.item.CategoryItem;

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

            TextView ic_category_icon = view.findViewById(R.id.ic_category_icon);
            TextView tv_category_name = (TextView) view.findViewById(R.id.tv_category_name);
//            ImageView ic_category_name = (view.findViewById(R.id.ic_category_name));
            TextView tv_category_ex = (TextView) view.findViewById(R.id.tv_category_ex);

            ic_category_icon.setText(categoryItem.getCategoryIcon());
//            iv_category_icon.setImageResource(categoryItem.getCategoryIcon());
            tv_category_name.setText(categoryItem.getCategoryName());
            tv_category_ex.setText(categoryItem.getCategoryEx());

        } else {
            View myView = new View(context);
            myView = (View) view;
        }

        return view;
    }
}
