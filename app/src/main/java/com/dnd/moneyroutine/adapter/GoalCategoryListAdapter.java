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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.BudgetUpdateActivity;
import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.dto.GoalCategoryDetail;
import com.dnd.moneyroutine.dto.GoalCategoryForm;
import com.dnd.moneyroutine.dto.GoalTotalForm;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.JWTUtils;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 예산 수정 : 목표 카테고리에 사용
public class GoalCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnTextChangeListener {
        void onChange(int finalTotal);
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

    private ArrayList<String> totalAmountList; // 각 카테고리별 예산
    private boolean isOnTextChanged = false;
    private int finalTotal; // 카테고리별 예산을 다 더한 값
    private int mBudget; // 전체 예산 저장
    private ArrayList<GoalCategoryForm> modifyList = new ArrayList<>();

    private String token;
    private int userId;
    private int goalId;

    private OnTextChangeListener onTextChangeListener;

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public GoalCategoryListAdapter(int goalId, ArrayList<GoalCategoryDetail> categoryList, ArrayList<String> totalAmountList) {
        this.goalId = goalId;
        this.categoryList = categoryList;
        this.totalAmountList = totalAmountList;
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

        token = PreferenceManager.getToken(context, Constants.tokenKey);
        userId = JWTUtils.getUserId(token);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof CategoryViewHolder) {
            GoalCategoryDetail category = categoryList.get(position);
            ((CategoryViewHolder) holder).setItem(category);

            ((CategoryViewHolder) holder).etCatBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if (isFocus) {
                        ((CategoryViewHolder) holder).ivPencil.setImageResource(R.drawable.icon_pencil_green);
                    } else {
                        ((CategoryViewHolder) holder).ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                    }
                }
            });
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

            setListener();
        }

        private void setListener() {
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

                    if (finalTotal > mBudget) {
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
                    mBudget = Integer.parseInt(totalBudget.replaceAll("\\,", ""));

                    int position = getBindingAdapterPosition();
                    finalTotal = 0;

                    if (isOnTextChanged) {
                        isOnTextChanged = false;
                        try {
                            for (int i = 0; i <= position; i++) {
                                if (i == position) {
                                    totalAmountList.set(position, num);
                                    break;
                                }
                            }

                            for (int i = 0; i <= totalAmountList.size() - 1; i++) {
                                finalTotal += Integer.parseInt(totalAmountList.get(i));
                            }

                            if (finalTotal > mBudget) {
                                //예산보다 많으면
                                String commaTotal = new DecimalFormat("#,###").format(finalTotal - mBudget);
                                tvRemainBudget.setText(commaTotal + "원 초과");
                                tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                                btnConfirm.setEnabled(false); //다음 버튼 비활성화

                            } else {
                                //예산보다 적으면
                                String commaTotal = new DecimalFormat("#,###").format(mBudget - finalTotal);
                                tvRemainBudget.setText(commaTotal + "원 남음");
                                tvRemainBudget.setTextColor(Color.parseColor("#047E74"));
                                btnConfirm.setEnabled(true);//다음 버튼 활성화
                            }

                        } catch (NumberFormatException e) {
                            finalTotal = 0;

                            for (int i = 0; i <= position; i++) {
                                if (i == position) {
                                    totalAmountList.set(position, String.valueOf(categoryList.get(position).getBudget()));
                                }
                            }
                            for (int i = 0; i <= totalAmountList.size() - 1; i++) {
                                finalTotal += Integer.parseInt(totalAmountList.get(i));
                            }

                            if (finalTotal > mBudget) {
                                String commaTotal = new DecimalFormat("#,###").format(finalTotal - mBudget);
                                tvRemainBudget.setText(commaTotal + "원 초과");
                                tvRemainBudget.setTextColor(Color.parseColor("#FD5E6E"));
                                btnConfirm.setEnabled(false);
                                btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                            } else {
                                String commaTotal = new DecimalFormat("#,###").format(mBudget - finalTotal);
                                tvRemainBudget.setText(commaTotal + "원 남음");
                                btnConfirm.setEnabled(true);
                                btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                            }
                        }

                        onTextChangeListener.onChange(finalTotal);

                        GoalCategoryDetail category = categoryList.get(position);
                        int budget = etCatBudget.length() > 0 ? Integer.parseInt(etCatBudget.getText().toString().replaceAll("\\,", "")) : 0;

                        modifyList.add(new GoalCategoryForm(userId, category.getGoalCategoryId(), budget));
                    }
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etTotalBudget.isFocused()) {
                        etTotalBudget.clearFocus();
                    }

                    if (etCatBudget.isFocused()) {
                        etCatBudget.clearFocus();
                    }

                    changeTotalBudget();
                }
            });
        }

        // 전체 예산 수정하기
        private void changeTotalBudget() {
            int changeBudget = Integer.parseInt(tvTotalBudget.getText().toString().replaceAll("\\,", ""));
            GoalTotalForm goalTotalForm = new GoalTotalForm(goalId, changeBudget);

            HeaderRetrofit headerRetrofit = new HeaderRetrofit();
            Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
            RetrofitService retroService = retrofit.create(RetrofitService.class);

            Call<JsonObject> call = retroService.updateTotalBudget(goalTotalForm);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject responseJson = response.body();

                        Log.d("GoalCategoryList", responseJson.toString());

                        if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                            int budget = responseJson.get("data").getAsInt();

                            if (changeBudget == budget) {
                                Log.d("GoalCategoryList", "전체 예산 수정 성공");

                                if (modifyList.size() == 0) {
                                    Intent intent = new Intent();
                                    intent.putExtra("goalUpdate", true);

                                    ((BudgetUpdateActivity) context).setResult(Activity.RESULT_OK, intent);
                                    ((BudgetUpdateActivity) context).finish();
                                } else {
                                    for (int i = 0; i < modifyList.size(); i++) {
                                        GoalCategoryForm goalCategoryForm = modifyList.get(i);
                                        changeGoalCategoryBudget(i == modifyList.size() - 1, goalCategoryForm);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(context, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 카테고리 예산 수정하기
        private void changeGoalCategoryBudget(boolean last, GoalCategoryForm goalCategoryForm) {
            HeaderRetrofit headerRetrofit = new HeaderRetrofit();
            Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
            RetrofitService retroService = retrofit.create(RetrofitService.class);

            Call<JsonObject> call = retroService.updateGoalCategory(goalCategoryForm);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject responseJson = response.body();

                        Log.d("GoalCategoryList", responseJson.toString());

                        if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                            boolean result = responseJson.get("data").getAsBoolean();

                            if (result && last) {
                                Intent intent = new Intent();
                                intent.putExtra("goalUpdate", true);

                                ((BudgetUpdateActivity) context).setResult(Activity.RESULT_OK, intent);
                                ((BudgetUpdateActivity) context).finish();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(context, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
