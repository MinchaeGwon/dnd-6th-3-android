package com.dnd.moneyroutine.adapter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.MainActivity;
import com.dnd.moneyroutine.OnboardingDetailBudgetActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CustomCategoryModel;
import com.dnd.moneyroutine.dto.GoalCategoryCreateDtoList;
import com.dnd.moneyroutine.item.BudgetItem;
import com.dnd.moneyroutine.service.RequestService;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetRecyclerViewAdapter extends RecyclerView.Adapter<BudgetRecyclerViewAdapter.ViewHolder> {


    private Context context;
    private View rootView;

    private TextView tv_budget_total;
    private TextView tvAlert;
    private Button btnNext;


    private ArrayList<String> totalAmountArray = new ArrayList<>();
    private ArrayList<BudgetItem> mBudgetItem;
    private ArrayList<GoalCategoryCreateDtoList> goalCategoryCreateDtoList;
    private InputMethodManager inputManager;
    private BudgetDetailModel budgetDetailModel;


    private boolean isOnTextChanged = false;
    private int finalTotal;
    private int mbudget;
    private int budgetDetail;
    private String entireBudget;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result = "";

    public BudgetRecyclerViewAdapter(ArrayList<BudgetItem> list) {
        this.mBudgetItem = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_budget, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        tvAlert = rootView.findViewById(R.id.tv_budget_alert);
        tv_budget_total = rootView.findViewById(R.id.tv_budget_total);
        btnNext = rootView.findViewById(R.id.btn_next_detail_budget);
        inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        goalCategoryCreateDtoList=(ArrayList<GoalCategoryCreateDtoList>) ((Activity)context).getIntent().getSerializableExtra("goalCategoryCreateDtoList");
        entireBudget = ((Activity)context).getIntent().getStringExtra("Budget");


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
                //쉼표 추가
                if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    holder.et_budget_detail.setText(result);
                    holder.et_budget_detail.setSelection(result.length());
                }

                if (finalTotal > mbudget) {
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_false);

                    }
                } else {
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String num = editable.toString().replaceAll("\\,", "");
                String totalbudget = tv_budget_total.getText().toString();
                mbudget = Integer.parseInt(totalbudget.replaceAll("\\,", "").toString());


                finalTotal = 0;
                if (isOnTextChanged) {
                    isOnTextChanged = false;
                    try {
                        finalTotal = 0;

                        for (int i = 0; i <= position; i++) {
                            if (i != position) {
                                totalAmountArray.add("0");
                            } else {
                                totalAmountArray.add("0");
                                totalAmountArray.set(position, num);

                                break;
                            }
                        }

                        for (int i = 0; i <= totalAmountArray.size() - 1; i++) {
                            int tmpTotal = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal = finalTotal + tmpTotal;
                        }


                        if (finalTotal > mbudget) {
                            //예산보다 많으면
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal - mbudget);
                            tvAlert.setText(commaTotal + "원 초과");
                            tvAlert.setTextColor(Color.parseColor("#E70621"));
                            btnNext.setEnabled(false); //다음 버튼 비활성화

                        } else {
                            //예산보다 적으면
                            String commaTotal = new DecimalFormat("#,###").format(mbudget - finalTotal);
                            tvAlert.setText(commaTotal + "원 남음");
                            tvAlert.setTextColor(Color.parseColor("#047E74"));
                            btnNext.setEnabled(true);//다음 버튼 활성화
                        }

                    } catch (NumberFormatException e) {
                        finalTotal = 0;
                        for (int i = 0; i <= position; i++) {
                            if (i == position) {
                                totalAmountArray.set(position, "0");
//                                    nameArray.set(position,"0");
                            }
                        }
                        for (int i = 0; i <= totalAmountArray.size() - 1; i++) {
                            int tmp = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal = finalTotal + tmp;
                        }

                        if (finalTotal > mbudget) {
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal - mbudget);
                            tvAlert.setText(commaTotal + "원 초과");
                            tvAlert.setTextColor(Color.parseColor("#FD5E6E"));
                            btnNext.setEnabled(false);
                            btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);

                        } else {
                            String commaTotal = new DecimalFormat("#,###").format(mbudget - finalTotal);
                            tvAlert.setText(commaTotal + "원 남음");
                            btnNext.setEnabled(true);
                            btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);

                        }

                    }
                    if(holder.et_budget_detail.getText().toString().length()>0){
                        budgetDetail = Integer.parseInt(holder.et_budget_detail.getText().toString().replaceAll("\\,", ""));
                        goalCategoryCreateDtoList.get(position).setBudget(budgetDetail);
                    }else{
                        goalCategoryCreateDtoList.get(position).setBudget(0);
                    }

                }

            }
        });

        budgetDetailModel=new BudgetDetailModel(goalCategoryCreateDtoList, mbudget);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                goaltoServer();

            }

        });

        holder.bindView(position, mBudgetItem.get(position));

    }

    //서버로 categoryid, budget, iscustom, 전체 budget 보냄
    private void goaltoServer(){
        Call<BudgetDetailModel> call = RequestService.getInstance().goal(budgetDetailModel);
        call.enqueue(new Callback<BudgetDetailModel>() {

            @Override
            public void onResponse(Call<BudgetDetailModel> call, Response<BudgetDetailModel> response) {
                if (response.isSuccessful()) {
                    BudgetDetailModel post = response.body();
                    Log.d("BudgetDetailModel", post.toString());
                } else {
                    Log.e("BudgetDetailModel", "error: " + response.code());
                    return;
                }
            }
            @Override
            public void onFailure(Call<BudgetDetailModel> call, Throwable t) {
                Log.e("custom category", "fail: " + t.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mBudgetItem.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
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

        public void bindView(int position, BudgetItem item) {

            //edittext 커서 위치에 따라 색 변경
            et_budget_detail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if (isFocus) {
                        linear_budget_detail.setBackgroundResource(R.drawable.textbox_typing);
                        won_detail.setImageResource(R.drawable.won);
                        tv_budget_detail_won.setTextColor(Color.parseColor("#495057"));

                    } else {

                        if (et_budget_detail.getText().toString().length() > 0) {
                            won_detail.setImageResource(R.drawable.won);
                            tv_budget_detail_won.setTextColor(Color.parseColor("#495057"));
                            linear_budget_detail.setBackgroundResource(R.drawable.textbox_default);
                        } else {
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
