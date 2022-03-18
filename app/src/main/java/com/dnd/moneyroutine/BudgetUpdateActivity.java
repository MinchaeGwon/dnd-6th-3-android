package com.dnd.moneyroutine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.moneyroutine.adapter.GoalCategoryListAdapter;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.GoalCategoryDetail;
import com.dnd.moneyroutine.dto.GoalInfo;
import com.dnd.moneyroutine.service.CategorySwipeHelper;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 예산 수정 activity
public class BudgetUpdateActivity extends AppCompatActivity {

    private static final String TAG = "BudgetUpdateActivity";

    private ImageButton ibBack;
    private ImageButton ibCancel;

    private TextView tvMonth;
    private EditText etTotalBudget;
    private ImageView ivPencil;

    private TextView tvTotalBudget;
    private TextView tvRemainBudget;
    private RecyclerView rvCategory;

    private LinearLayout btnAddCat;
    private Button btnConfirm;

    private GoalCategoryListAdapter goalCategoryListAdapter;
    private ArrayList<GoalCategoryDetail> categoryList;

    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;
    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    private DecimalFormat decimalFormat;
    private String result = "";

    private String token;
    private GoalInfo goalInfo;
    private boolean goalUpdate;

    private boolean isOnTextChanged = false;

    private ArrayList<String> totalAmountList;
    private int budgetTotal = 0;
    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_update);

        initView();
        initField();

        setBtnListener();
        setEditListener();
        setButtonSize();

        if (goalInfo != null) {
            setBudgetInfo();
        } else {
            getBudgetInfo();
        }
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_update_budget_back);
        ibCancel = findViewById(R.id.ib_update_budget_cancel);

        tvMonth = findViewById(R.id.tv_update_budget_month);
        etTotalBudget = findViewById(R.id.et_update_budget_total);
        ivPencil = findViewById(R.id.iv_update_budget_pencil);

        tvTotalBudget = findViewById(R.id.tv_update_budget_total);
        tvRemainBudget = findViewById(R.id.tv_update_budget_remain);

        rvCategory = findViewById(R.id.rv_update_budget_cat);

        btnAddCat = findViewById(R.id.btn_update_budget_add_cat);
        btnConfirm = findViewById(R.id.btn_update_budget_confirm);
    }

    private void initField() {
        goalInfo = (GoalInfo) getIntent().getSerializableExtra("goalInfo");
        token = PreferenceManager.getToken(this, Constants.tokenKey);

        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        contentLayoutParams = (ConstraintLayout.LayoutParams) btnConfirm.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###");

        Calendar calendar = Calendar.getInstance();
        tvMonth.setText((calendar.get(Calendar.MONTH) + 1) + "월");
    }

    private void setBtnListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToActivity();
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog(-1);
            }
        });

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetUpdateActivity.this, AddExpenseCatActivity.class);
                intent.putExtra("goalId", goalInfo.getGoalId());
                startActivityResult.launch(intent);
            }
        });
    }

    // edit 관련 리스너 설정
    private void setEditListener() {
        etTotalBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    if (tvRemainBudget.getText().toString().contains("남음")) {
                        ivPencil.setImageResource(R.drawable.icon_pencil_green);
                    } else {
                        ivPencil.setImageResource(R.drawable.icon_pencil_red);
                    }
                } else {
                    ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                }
            }
        });

        etTotalBudget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etTotalBudget.isFocused()) {
                            etTotalBudget.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        });

        etTotalBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int id, int i1, int i2) {
                isOnTextChanged = true;

                // 금액에 쉼표 추가
                if (etTotalBudget.isFocused() && !TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    etTotalBudget.setText(result);
                    etTotalBudget.setSelection(result.length());
                }

                if (etTotalBudget.length() > 0 && tvRemainBudget.getText().toString().contains("남음")) {
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
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isOnTextChanged) {
                    isOnTextChanged = false;

                    if (budgetTotal == 0) {
                        for (int i = 0; i <= totalAmountList.size() - 1; i++) {
                            budgetTotal += Integer.parseInt(totalAmountList.get(i));
                        }
                    }

                    tvTotalBudget.setText(etTotalBudget.length() > 0 ? result : String.valueOf(0));

                    int num = etTotalBudget.length() > 0 ? Integer.parseInt(etTotalBudget.getText().toString().replaceAll("\\,", "")) : 0;

                    if (budgetTotal > num) {
                        // 예산보다 많으면
                        ivPencil.setImageResource(R.drawable.icon_pencil_red);
                        etTotalBudget.setBackgroundResource(R.drawable.edit_under_error_selector);

                        String commaTotal = decimalFormat.format(budgetTotal - num);
                        tvRemainBudget.setText(commaTotal + "원 초과");
                        tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                    } else {
                        // 예산보다 적으면
                        if (first) {
                            ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                            first = false;
                        } else {
                            ivPencil.setImageResource(R.drawable.icon_pencil_green);
                        }

                        etTotalBudget.setBackgroundResource(R.drawable.edit_under_normal_selector);

                        String commaTotal = decimalFormat.format(num - budgetTotal);
                        tvRemainBudget.setText(commaTotal + "원 남음");
                        tvRemainBudget.setTextColor(Color.parseColor("#047E74"));
                    }
                }
            }
        });
    }

    private void setButtonSize() {
        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                getCurrentFocus().clearFocus();

                if (tvRemainBudget.getText().toString().contains("남음")) {
                    tvRemainBudget.setTextColor(Color.parseColor("#212529"));
                }

                if (btnConfirm.isEnabled()) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);
                }

                contentLayoutParams.topMargin = (int) (10 * scale + 0.2f);
                contentLayoutParams.setMarginStart((int) (16 * scale + 0.2f));
                contentLayoutParams.setMarginEnd((int) (16 * scale + 0.2f));
                contentLayoutParams.bottomMargin = (int) (56 * scale + 0.2f);
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });

        // 키보드가 올라왔을 때
        softKeyboardDetector.setOnShownKeyboard(new SoftKeyboardDetector.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                if (tvRemainBudget.getText().toString().contains("남음")) {
                    tvRemainBudget.setTextColor(Color.parseColor("#047E74"));
                }

                if (btnConfirm.isEnabled()) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

                contentLayoutParams.topMargin = 0;
                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });
    }

    // 수정할 예산 정보 가져오기
    private void getBudgetInfo() {
        // 헤더에 토큰 추가
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getMainGoalList(LocalDate.now());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (!responseJson.get("data").isJsonNull()) {
                            Gson gson = new Gson();
                            goalInfo = gson.fromJson(responseJson.getAsJsonObject("data"), new TypeToken<GoalInfo>() {}.getType());

                            if (goalInfo != null) {
                                setBudgetInfo();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(BudgetUpdateActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 수정할 예산 정보 바인딩
    private void setBudgetInfo() {
        totalAmountList = new ArrayList<>();
        int catBudget = 0;

        for (GoalCategoryDetail category : goalInfo.getGoalCategoryList()) {
            catBudget += category.getBudget();
            totalAmountList.add(String.valueOf(category.getBudget()));
        }

        String remain = decimalFormat.format(goalInfo.getTotalBudget() - catBudget) + "원 남음";
        tvRemainBudget.setText(remain);

        String budget = decimalFormat.format(goalInfo.getTotalBudget());
        etTotalBudget.setText(budget);
        tvTotalBudget.setText(budget);

        categoryList = goalInfo.getGoalCategoryList();

        goalCategoryListAdapter = new GoalCategoryListAdapter(goalInfo.getGoalId(), categoryList, totalAmountList);
        goalCategoryListAdapter.setOnTextChangeListener(new GoalCategoryListAdapter.OnTextChangeListener() {
            @Override
            public void onChange(int finalTotal) {
                budgetTotal = finalTotal;

                if (etTotalBudget.length() > 0) {
                    int num = Integer.parseInt(etTotalBudget.getText().toString().replaceAll("\\,", ""));

                    if (finalTotal > num) {
                        //예산보다 많으면
                        String commaTotal = decimalFormat.format(finalTotal - num);
                        tvRemainBudget.setText(commaTotal + "원 초과");
                        tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                        btnConfirm.setEnabled(false); // 다음 버튼 비활성화
                    } else {
                        //예산보다 적으면
                        String commaTotal = decimalFormat.format(num - finalTotal);
                        tvRemainBudget.setText(commaTotal + "원 남음");
                        tvRemainBudget.setTextColor(Color.parseColor("#047E74"));
                        btnConfirm.setEnabled(true); // 다음 버튼 활성화
                    }
                }
            }
        });

        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(goalCategoryListAdapter);

        new CategorySwipeHelper(this, rvCategory) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new CategorySwipeHelper.UnderlayButton(
                        BudgetUpdateActivity.this,
                        R.drawable.icon_trash,
                        Color.parseColor("#E70621"),
                        new CategorySwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                setDialog(pos);
                            }
                        }
                ));
            }
        };
    }

    // 목표 카테고리 삭제하기
    private void deleteGoalCategory(int goalCategoryId, int position) {
        // 헤더에 토큰 추가
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.deleteGoalCategory(goalCategoryId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        boolean result = responseJson.get("data").getAsBoolean();

                        if (result) {
                            goalUpdate = true;

                            int removeBudget = categoryList.get(position).getBudget();

                            categoryList.remove(position);
                            goalCategoryListAdapter.notifyItemRemoved(position);

                            totalAmountList.remove(position);
                            budgetTotal -= removeBudget;

                            int num = Integer.parseInt(etTotalBudget.getText().toString().replaceAll("\\,", ""));

                            if (budgetTotal > num) {
                                // 예산보다 많으면
                                ivPencil.setImageResource(R.drawable.icon_pencil_red);
                                etTotalBudget.setBackgroundResource(R.drawable.edit_under_error_selector);

                                String commaTotal = decimalFormat.format(budgetTotal - num);
                                tvRemainBudget.setText(commaTotal + "원 초과");
                                tvRemainBudget.setTextColor(Color.parseColor("#E70621"));
                            } else {
                                // 예산보다 적으면
                                ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                                etTotalBudget.setBackgroundResource(R.drawable.edit_under_normal_selector);

                                String commaTotal = decimalFormat.format(num - budgetTotal);
                                tvRemainBudget.setText(commaTotal + "원 남음");
                                tvRemainBudget.setTextColor(Color.parseColor("#212529"));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(BudgetUpdateActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 카테고리 삭제 또는 예산 수정 종료 확인 다이얼로그 띄우기
    private void setDialog(int position) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm;
        Button btnCancel;

        if (position >= 0) {
            btnConfirm = view.findViewById(R.id.btn_dialog_cancel);
            btnCancel = view.findViewById(R.id.btn_dialog_confirm);

            tvTitle.setText(categoryList.get(position).getName() + "을(를) 삭제할까요?");
            tvContent.setText("해당 소비 기록도 같이 삭제됩니다");
            btnConfirm.setText("확인");
            btnCancel.setText("취소");
        } else {
            btnConfirm = view.findViewById(R.id.btn_dialog_confirm);
            btnCancel = view.findViewById(R.id.btn_dialog_cancel);

            tvTitle.setText("예산 수정을 종료할까요?");
            tvContent.setText("작성한 정보는 저장되지 않습니다");
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position >= 0) {
                    deleteGoalCategory(categoryList.get(position).getGoalCategoryId(), position);
                } else {
                    moveToActivity();
                }

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void moveToActivity() {
        Intent intent = new Intent();
        intent.putExtra("goalUpdate", goalUpdate);

        setResult(RESULT_OK, intent);
        finish();
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        getBudgetInfo();
                        goalUpdate = true;
                    }
                }
            });

    // 뒤로가기 버튼 눌렀을 때 동작하는 메소드
    @Override
    public void onBackPressed() {
        moveToActivity();
        super.onBackPressed();
    }
}