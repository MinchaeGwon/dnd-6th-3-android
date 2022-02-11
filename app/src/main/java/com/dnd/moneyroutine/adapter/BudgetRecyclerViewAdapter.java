package com.dnd.moneyroutine.adapter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.item.BudgetItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BudgetRecyclerViewAdapter extends RecyclerView.Adapter<BudgetRecyclerViewAdapter.ViewHolder>{

    private ArrayList<BudgetItem> mBudgetItem;

    Context context;
    View rootView;
    TextView tv_budget_total;
    TextView tvAlert;
    TextView tvGone;

    ArrayList<String> totalAmountArray = new ArrayList<>();


    boolean isOnTextChanged = false;
    int finalTotal;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    public BudgetRecyclerViewAdapter(ArrayList<BudgetItem> list){
        this.mBudgetItem = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_budget, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        tvAlert =rootView.findViewById(R.id.tv_budget_alert);
        tv_budget_total = rootView.findViewById(R.id.tv_budget_total);



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
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    holder.et_budget_detail.setText(result);
                    holder.et_budget_detail.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String num = editable.toString().replaceAll("\\,","");
                String totalbudget = tv_budget_total.getText().toString();
                int mbudget = Integer.parseInt(totalbudget.replaceAll("\\,","").toString());


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
                                totalAmountArray.set(position,num);

                                break;
                            }
                        }

                        for(int i =0; i<=totalAmountArray.size()-1;i++){
                            int tmpTotal = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal =  finalTotal+tmpTotal;
                        }

                        if(finalTotal>mbudget){
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal-mbudget);
                            tvAlert.setText(commaTotal+"원 초과");
                            tvAlert.setTextColor(Color.parseColor("#FD5E6E"));
                        }
                        else{
                            String commaTotal = new DecimalFormat("#,###").format(mbudget-finalTotal);
                            tvAlert.setText(commaTotal+"원 남음");
                            tvAlert.setTextColor(Color.parseColor("#0DC9B9"));

                        }

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

//                        String commaTotal = new DecimalFormat("#,###").format(mbudget-finalTotal);
//                        tv_budget_total.setText(commaTotal+"원 남음");

                        if(finalTotal>mbudget){
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal-mbudget);
                            tvAlert.setText(commaTotal+"원 초과");
                            tvAlert.setTextColor(Color.parseColor("#FD5E6E"));
                        }
                        else{
                            String commaTotal = new DecimalFormat("#,###").format(mbudget-finalTotal);
                            tvAlert.setText(commaTotal+"원 남음");
                        }

                    }

                    //합계가 예산을 넘으면


//                    if(finalTotal>Integer.parseInt(mbudget)){
//                        tvAlert.setText(totalbudget+"");
//                        tv_budget_total.setTextColor(Color.parseColor("#ff6eab"));
//                        tvtotalText.setTextColor(Color.parseColor("#ff6eab"));
//                    }
//                    else{
//                        tvAlert.setVisibility(View.INVISIBLE);
//                        tv_budget_total.setTextColor(Color.parseColor("#343A40"));
//                        tvtotalText.setTextColor(Color.parseColor("#343A40"));
//                    }
                }





            }
        });



//        holder.setItem(mBudgetItem.get(position));
        holder.bindView(position, mBudgetItem.get(position));


    }

    protected String makeStringComma(String str) {    // 천단위 콤마설정.
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
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
