package com.dnd.moneyroutine.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.item.BudgetItem;

import java.util.ArrayList;

public class BudgetRecyclerViewAdapter extends RecyclerView.Adapter<BudgetRecyclerViewAdapter.ViewHolder>{

    private ArrayList<BudgetItem> mBudgetItem;

    Context context;
    View rootView;
    TextView tv_budget_total;
    TextView tvAlert;
    TextView tvGone;
    TextView tvtotalText;

    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<String> totalAmountArray = new ArrayList<>();


    boolean isOnTextChanged = false;
    int finalTotal;

    public BudgetRecyclerViewAdapter(ArrayList<BudgetItem> list){
        this.mBudgetItem = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_budget, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        tv_budget_total =rootView.findViewById(R.id.tv_budget_total);
        tvAlert = rootView.findViewById(R.id.tv_budget_alert);
        tvGone=rootView.findViewById(R.id.tv_gone);
        tvtotalText=rootView.findViewById(R.id.tv_total_text);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //예산 입력하면 합산하여 띄움
        holder.et_budget_detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isOnTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                finalTotal = 0;
                if(isOnTextChanged){
                    isOnTextChanged=false;
                    try{
                        finalTotal = 0;

                        for(int i=0; i<=position;i++){
                            if(i!=position){
                                totalAmountArray.add("0");
                            }else{
                                totalAmountArray.add("0");
                                totalAmountArray.set(position,editable.toString());

                                break;
                            }
                        }

                        for(int i =0; i<=totalAmountArray.size()-1;i++){
                            int tmpTotal = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal =  finalTotal+tmpTotal;
                        }

                        tv_budget_total.setText(String.valueOf(finalTotal)+"원");


                    }catch (NumberFormatException e){
                        finalTotal=0;
                        for(int i=0;i<=position;i++){
                            if(i==position){
                                totalAmountArray.set(position, "0");
//                                    nameArray.set(position,"0");
                            }
                        }
                        for (int i=0;i<=totalAmountArray.size()-1; i++){
                            int tmp = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal=finalTotal+tmp;
                        }
                        tv_budget_total.setText(String.valueOf(finalTotal)+"원");

                    }

                    //합계가 예산을 넘으면
                    String totalbudget = tvGone.getText().toString();
                    if(finalTotal>Integer.parseInt(totalbudget)){
                        tvAlert.setVisibility(View.VISIBLE);
                        tvAlert.setText("앗 "+totalbudget+"원이 넘었어요!");
                        tv_budget_total.setTextColor(Color.parseColor("#ff6eab"));
                        tvtotalText.setTextColor(Color.parseColor("#ff6eab"));
                    }
                    else{
                        tvAlert.setVisibility(View.INVISIBLE);
                        tv_budget_total.setTextColor(Color.parseColor("#343A40"));
                        tvtotalText.setTextColor(Color.parseColor("#343A40"));
                    }
                }



            }
        });

//        holder.setItem(mBudgetItem.get(position));
        holder.bindView(position, mBudgetItem.get(position));


    }







    @Override
    public int getItemCount() {
        return mBudgetItem.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linear_budget_detail;
        TextView ic_category_item_detail;
        TextView tv_category_name_budget;
        EditText et_budget_detail;
        ImageView won_detail;
        TextView tv_budget_detail_won;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linear_budget_detail = itemView.findViewById(R.id.linear_budget_detail);
            won_detail = itemView.findViewById(R.id.won_detail);
            tv_budget_detail_won = itemView.findViewById(R.id.tv_budget_detail_won);
            et_budget_detail = itemView.findViewById(R.id.et_budget_detail);
//            ic_category_icon = itemView.findViewById(R.id.iv_category_icon);
            ic_category_item_detail = itemView.findViewById(R.id.ic_category_item_detail);
            tv_category_name_budget = (TextView) itemView.findViewById(R.id.tv_category_item_detail);

        }


        public void bindView(int position, BudgetItem item){

//            int id =mBudgetItem.get(position).getId();

            //edittext 커서 위치에 따라 색 변경
            et_budget_detail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if(isFocus){
                        linear_budget_detail.setBackgroundResource(R.drawable.textbox_typing);
                        won_detail.setImageResource(R.drawable.won);
                        tv_budget_detail_won.setTextColor(Color.parseColor("#495057"));
                    }
                    else{

                        if(et_budget_detail.getText().toString().length()>0){
                            won_detail.setImageResource(R.drawable.won);
                            tv_budget_detail_won.setTextColor(Color.parseColor("#495057"));
                            linear_budget_detail.setBackgroundResource(R.drawable.textbox_default);
                        }

                        else{
                            linear_budget_detail.setBackgroundResource(R.drawable.textbox_default);
                            won_detail.setImageResource(R.drawable.won_grey);
                            tv_budget_detail_won.setTextColor(Color.parseColor("#ADB5BD"));
                        }
                    }
                }
            });





            ic_category_item_detail.setText(item.getCategoryIcon());
//            ic_category_item_detail.setImageResource(item.getCategoryIcon());
            tv_category_name_budget.setText(item.getCategoryName());


        }


    }


}
