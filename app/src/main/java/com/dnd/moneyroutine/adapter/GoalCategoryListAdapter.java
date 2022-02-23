package com.dnd.moneyroutine.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.dto.GoalCategoryCompact;
import com.dnd.moneyroutine.dto.GoalCategoryDetail;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GoalCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(GoalCategoryCompact category);
    }

    private Context context;
    private ArrayList<GoalCategoryDetail> categoryList;

    private InputMethodManager inputManager;
    private DecimalFormat decimalFormat;
    private String result = "";

    private View rootView;

    private EditText etTotalBudget;
    private TextView tvRemainBudget;
    private TextView tvTotalBudget;
    private Button btnConfirm;

    private ArrayList<String> totalAmountArray = new ArrayList<>();
    private boolean isOnTextChanged = false;
    private int finalTotal;
    private int mbudget;
    private int budgetDetail;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public GoalCategoryListAdapter(ArrayList<GoalCategoryDetail> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_update_category, parent, false);

        inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        decimalFormat = new DecimalFormat("#,###");

        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        etTotalBudget = rootView.findViewById(R.id.et_update_budget_total);
        tvRemainBudget = rootView.findViewById(R.id.tv_update_budget_remain);
        tvTotalBudget = rootView.findViewById(R.id.tv_update_budget_total);
        btnConfirm = rootView.findViewById(R.id.btn_update_budget_confirm);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            GoalCategoryDetail category = categoryList.get(position);
            ((CategoryViewHolder) holder).setItem(category);
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

        EditText etCatBudget;
        ImageView ivPencil;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategory = itemView.findViewById(R.id.iv_basic_category);
            tvEmoji = itemView.findViewById(R.id.tv_custom_category);

            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDetail = itemView.findViewById(R.id.tv_cat_detail);

            etCatBudget = itemView.findViewById(R.id.et_cat_budget);
            ivPencil = itemView.findViewById(R.id.iv_update_budget_pencil);
        }

        // 실제 view에 객체 내용을 적용시키는 메소드
        public void setItem(GoalCategoryDetail category) {
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

                ivCategory.setImageResource(Common.getBasicColorCategoryResId(category.getName()));
            }

            tvCategory.setText(category.getName());
            tvDetail.setText(category.getDetail());

            String budget = decimalFormat.format(category.getBudget());
            etCatBudget.setText(budget);

            totalAmountArray.add(String.valueOf(category.getBudget()));

            setListener();
        }

        private void setListener() {
            etTotalBudget.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (tvRemainBudget.getText().toString().contains("남음")) {
                        etTotalBudget.setTextColor(Color.parseColor("#212529"));
                        tvRemainBudget.setTextColor(Color.parseColor("#212529"));
                    } else if (tvRemainBudget.getText().toString().contains("초과")) {
                        etTotalBudget.setTextColor(Color.parseColor("#E70621"));
                        tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                    }

                    // 금액에 쉼표 추가
                    if (etTotalBudget.isFocused() && !TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                        result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                        etTotalBudget.setText(result);
                        etTotalBudget.setSelection(result.length());
                    }

                    if (etTotalBudget.length() > 0) {
                        btnConfirm.setEnabled(true);

                        if (inputManager.isAcceptingText()) {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                        } else {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                        }
                    } else {
                        btnConfirm.setEnabled(false);

                        if (inputManager.isAcceptingText()) {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                        } else {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);
                        }
                    }

//                    String remain = decimalFormat.format(goalInfo.getRemainder());
//                    tvRemainBudget.setText(remain + "원 남음");

                    tvTotalBudget.setText(result);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            etCatBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if (isFocus) {
                        ivPencil.setImageResource(R.drawable.icon_pencil_green);
                    } else {
                        ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                    }
                }
            });

            etCatBudget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                            inputManager.hideSoftInputFromWindow(etCatBudget.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            etCatBudget.clearFocus();

                            break;
                        default:
                            // 기본 엔터키 동작
                            return false;
                    }

                    return true;
                }
            });

            etCatBudget.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    isOnTextChanged = true;

                    //쉼표 추가
                    if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                        result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                        etCatBudget.setText(result);
                        etCatBudget.setSelection(result.length());
                    }

                    if (finalTotal > mbudget) {
                        if (inputManager.isAcceptingText()) {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                        } else {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);

                        }
                    } else {
                        if (inputManager.isAcceptingText()) {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                        } else {
                            btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String num = editable.toString().replaceAll("\\,", "");
                    String totalBudget = tvTotalBudget.getText().toString();
                    mbudget = Integer.parseInt(totalBudget.replaceAll("\\,", "").toString());

                    int position = getBindingAdapterPosition();
                    finalTotal = 0;

                    if (isOnTextChanged) {
                        isOnTextChanged = false;
                        try {
                            finalTotal = 0;

                            for (int i = 0; i <= position; i++) {
//                                totalAmountArray.add("0");
                                if (i == position) {
                                    totalAmountArray.set(position, num);
                                    break;
                                }
                            }

                            for (int i = 0; i <= totalAmountArray.size() - 1; i++) {
                                finalTotal += finalTotal + Integer.parseInt(totalAmountArray.get(i));
                            }


                            if (finalTotal > mbudget) {
                                //예산보다 많으면
                                String commaTotal = new DecimalFormat("#,###").format(finalTotal - mbudget);
                                tvRemainBudget.setText(commaTotal + "원 초과");
                                tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                                btnConfirm.setEnabled(false); //다음 버튼 비활성화

                            } else {
                                //예산보다 적으면
                                String commaTotal = new DecimalFormat("#,###").format(mbudget - finalTotal);
                                tvRemainBudget.setText(commaTotal + "원 남음");
                                tvRemainBudget.setTextColor(Color.parseColor("#047E74"));
                                btnConfirm.setEnabled(true);//다음 버튼 활성화
                            }

                        } catch (NumberFormatException e) {
                            finalTotal = 0;

                            for (int i = 0; i <= position; i++) {
                                if (i == position) {
                                    totalAmountArray.set(position, String.valueOf(categoryList.get(position).getBudget()));
                                }
                            }
                            for (int i = 0; i <= totalAmountArray.size() - 1; i++) {
                                finalTotal += finalTotal + Integer.parseInt(totalAmountArray.get(i));
                            }

                            if (finalTotal > mbudget) {
                                String commaTotal = new DecimalFormat("#,###").format(finalTotal - mbudget);
                                tvRemainBudget.setText(commaTotal + "원 초과");
                                tvRemainBudget.setTextColor(Color.parseColor("#FD5E6E"));
                                btnConfirm.setEnabled(false);
                                btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);

                            } else {
                                String commaTotal = new DecimalFormat("#,###").format(mbudget - finalTotal);
                                tvRemainBudget.setText(commaTotal + "원 남음");
                                btnConfirm.setEnabled(true);
                                btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);

                            }

                        }
                        if (etCatBudget.getText().toString().length() > 0){
                            budgetDetail = Integer.parseInt(etCatBudget.getText().toString().replaceAll("\\,", ""));
//                            goalCategoryCreateDtoList.get(position).setBudget(budgetDetail);
                        }else{
//                            goalCategoryCreateDtoList.get(position).setBudget(0);
                        }
                    }
                }
            });
        }
    }
}
