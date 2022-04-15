package com.dnd.moneyroutine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.MainActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.BudgetDetailModel;
import com.dnd.moneyroutine.dto.CategoryCompact;
import com.dnd.moneyroutine.dto.GoalCategoryCreateDto;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BudgetRecyclerViewAdapter extends RecyclerView.Adapter<BudgetRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private View rootView;

    private TextView tvBudgetTotal;
    private TextView tvAlert;
    private Button btnNext;

    private String token;

    private ArrayList<String> totalAmountArray = new ArrayList<>();
    private ArrayList<CategoryCompact> selectCategories;
    private List<GoalCategoryCreateDto> goalCategoryCreateDtoList;

    private InputMethodManager inputManager;
    private BudgetDetailModel budgetDetailModel;

    private boolean isOnTextChanged = false;
    private int finalTotal; // 카테고리별 예산을 다 더한 값
    private int mBudget; // 전체 예산 저장

    private DecimalFormat decimalFormat;
    private String result = "";

    public BudgetRecyclerViewAdapter(ArrayList<CategoryCompact> selectCategories) {
        this.selectCategories = selectCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_budget, parent, false);

        context = parent.getContext();
        inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        decimalFormat = new DecimalFormat("#,###");

        token = PreferenceManager.getToken(context, Constants.tokenKey);

        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        tvAlert = rootView.findViewById(R.id.tv_budget_alert);
        tvBudgetTotal = rootView.findViewById(R.id.tv_budget_total);
        btnNext = rootView.findViewById(R.id.btn_next_detail_budget);

        goalCategoryCreateDtoList = (ArrayList<GoalCategoryCreateDto>) ((Activity) context).getIntent().getSerializableExtra("goalCategoryCreateDtoList");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.etBudget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(holder.etBudget.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        holder.etBudget.clearFocus();

                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        });

        //예산 입력하면 합산하여 띄움
        holder.etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isOnTextChanged = true;
                //쉼표 추가
                if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    holder.etBudget.setText(result);
                    holder.etBudget.setSelection(result.length());
                }

                if (finalTotal > mBudget) {
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
                String totalBudget = tvBudgetTotal.getText().toString();
                mBudget = Integer.parseInt(totalBudget.replaceAll("\\,", ""));

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

                        if (finalTotal > mBudget) {
                            //예산보다 많으면
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal - mBudget);
                            tvAlert.setText(commaTotal + "원 초과");
                            tvAlert.setTextColor(Color.parseColor("#E70621"));
                            btnNext.setEnabled(false); //다음 버튼 비활성화

                        } else {
                            //예산보다 적으면
                            String commaTotal = new DecimalFormat("#,###").format(mBudget - finalTotal);
                            tvAlert.setText(commaTotal + "원 남음");
                            tvAlert.setTextColor(Color.parseColor("#047E74"));
                            btnNext.setEnabled(true);//다음 버튼 활성화
                        }

                    } catch (NumberFormatException e) {
                        finalTotal = 0;
                        for (int i = 0; i <= position; i++) {
                            if (i == position) {
                                totalAmountArray.set(position, "0");
                            }
                        }
                        for (int i = 0; i <= totalAmountArray.size() - 1; i++) {
                            int tmp = Integer.parseInt(totalAmountArray.get(i));
                            finalTotal = finalTotal + tmp;
                        }

                        if (finalTotal > mBudget) {
                            String commaTotal = new DecimalFormat("#,###").format(finalTotal - mBudget);
                            tvAlert.setText(commaTotal + "원 초과");
                            tvAlert.setTextColor(Color.parseColor("#FD5E6E"));
                            btnNext.setEnabled(false);
                            btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                        } else {
                            String commaTotal = new DecimalFormat("#,###").format(mBudget - finalTotal);
                            tvAlert.setText(commaTotal + "원 남음");
                            btnNext.setEnabled(true);
                            btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                        }
                    }

                    if (holder.etBudget.getText().toString().length() > 0) {
                        int budgetDetail = Integer.parseInt(holder.etBudget.getText().toString().replaceAll("\\,", ""));
                        goalCategoryCreateDtoList.get(position).setBudget(budgetDetail);
                    } else {
                        goalCategoryCreateDtoList.get(position).setBudget(0);
                    }

                }
            }
        });

        holder.bindView(position, selectCategories.get(position));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.etBudget.isFocused()) {
                    holder.etBudget.clearFocus();
                }

                budgetDetailModel = new BudgetDetailModel();
                budgetDetailModel.setGoalCategoryCreateDtoList(goalCategoryCreateDtoList); //list
                budgetDetailModel.setTotalBudget(mBudget); //budget

                goalToServer();
            }
        });
    }

    //서버로 categoryid, budget, iscustom, 전체 budget 보냄
    private void goalToServer() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.goal(budgetDetailModel);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject post = response.body();
                    Log.d("BudgetDetailModel", post.toString());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    Log.e("BudgetDetailModel", "error: " + response.code());
                    return;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("custom category", "fail: " + t.getMessage());
                Toast.makeText(context, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return selectCategories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llBudget;
        TextView tvCategoryEmoji;
        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        EditText etBudget;
        ImageView ivWon;
        TextView tvWon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llBudget = itemView.findViewById(R.id.linear_budget_detail);
            ivWon = itemView.findViewById(R.id.won_detail);
            tvWon = itemView.findViewById(R.id.tv_budget_detail_won);
            etBudget = itemView.findViewById(R.id.et_budget_detail);
            tvCategoryEmoji = itemView.findViewById(R.id.ic_category_item_detail);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_item_detail);
            tvCategoryName = itemView.findViewById(R.id.tv_category_item_detail);
        }

        public void bindView(int position, CategoryCompact item) {
            //edittext 커서 위치에 따라 색 변경
            etBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if (isFocus) {
                        llBudget.setBackgroundResource(R.drawable.textbox_typing);
                        ivWon.setImageResource(R.drawable.won_black);
                        tvWon.setTextColor(Color.parseColor("#495057"));
                    } else {
                        if (etBudget.length() > 0) {
                            ivWon.setImageResource(R.drawable.won_black);
                            tvWon.setTextColor(Color.parseColor("#495057"));
                            llBudget.setBackgroundResource(R.drawable.textbox_default);
                        } else {
                            llBudget.setBackgroundResource(R.drawable.textbox_default);
                            ivWon.setImageResource(R.drawable.won_gray);
                            tvWon.setTextColor(Color.parseColor("#ADB5BD"));
                        }
                    }
                }
            });

            if (item.isCustom()) {
                ivCategoryIcon.setVisibility(View.INVISIBLE);
                tvCategoryEmoji.setVisibility(View.VISIBLE);

                String emoji = null;
                try {
                    emoji = URLDecoder.decode(item.getEmoji(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                tvCategoryEmoji.setText(emoji);
            } else {
                tvCategoryEmoji.setVisibility(View.INVISIBLE);
                ivCategoryIcon.setVisibility(View.VISIBLE);

                ivCategoryIcon.setImageResource(Common.getBasicColorCategoryResId(item.getName()));
            }

//            if (item.getCategoryIcon().contains("@drawable/")) { //기본 카테고리
//                ic_category_item_detail.setVisibility(View.INVISIBLE);
//                iv_category_item_detail.setVisibility(View.VISIBLE);
//                int resId = itemContext.getResources().getIdentifier(item.getCategoryIcon(), "drawable", itemContext.getPackageName());
//                iv_category_item_detail.setImageResource(resId);
//            } else { //사용자 생성 카테고리 이모지
//                iv_category_item_detail.setVisibility(View.INVISIBLE);
//                ic_category_item_detail.setVisibility(View.VISIBLE);
//
//                String emoji = null;
//                try {
//                    emoji = URLDecoder.decode(item.getCategoryIcon(), "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                ic_category_item_detail.setText(emoji);
//            }

            tvCategoryName.setText(item.getName());
        }
    }
}
