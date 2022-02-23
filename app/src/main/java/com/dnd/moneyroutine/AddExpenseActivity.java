package com.dnd.moneyroutine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.dnd.moneyroutine.adapter.GoalCategoryGridAdapter;
import com.dnd.moneyroutine.custom.Common;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.ExpenseForm;
import com.dnd.moneyroutine.dto.GoalCategoryCompact;
import com.dnd.moneyroutine.dto.GoalInfo;
import com.dnd.moneyroutine.fragment.ExpenseCalendarFragment;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 소비 입력 activity
public class AddExpenseActivity extends AppCompatActivity {

    private static final String TAG = "AddExpenseActivity";

    private ImageButton ibBack;
    private ImageButton ibCancel;
    private TextView tvDate;

    private LinearLayout llContent;
    private ImageView ivPencil;
    private EditText etContent;

    private LinearLayout llExpense;
    private ImageView ivWon;
    private EditText etExpense;
    private TextView tvWon;

    private RecyclerView rvCategory;
    private Button btnNext;

    private AlertDialog cancelDialog;

    private String token;

    private InputMethodManager inputManager;
    private SoftKeyboardDetector softKeyboardDetector;
    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    private DecimalFormat decimalFormat;
    private String result = "";
    private Calendar expenseDate;
    private GoalCategoryCompact selectCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initView();
        initField();
        setListener();
        setButtonSize();

        getGoalCategory();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_add_expense_back);
        ibCancel = findViewById(R.id.ib_add_expense_cancel);
        tvDate = findViewById(R.id.tv_add_expense_date);

        llContent = findViewById(R.id.ll_expense_content);
        ivPencil = findViewById(R.id.iv_expense_pencil);
        etContent = findViewById(R.id.et_expense_content);

        llExpense = findViewById(R.id.ll_expense_money);
        ivWon = findViewById(R.id.iv_expense_won);
        etExpense = findViewById(R.id.et_expense_money);
        tvWon = findViewById(R.id.tv_expense_won);

        rvCategory = findViewById(R.id.rv_expense_category);
        btnNext = findViewById(R.id.btn_add_expense_next);
    }

    private void initField() {
        token = PreferenceManager.getToken(this, Constants.tokenKey);

        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

        contentLayoutParams = (ConstraintLayout.LayoutParams) btnNext.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###");

        Calendar today = Calendar.getInstance();
        tvDate.setText(Common.getExpenseCalendarDate(today));
        expenseDate = today;
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelDialog();
                cancelDialog.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddExpenseActivity.this, AddFeelingActivity.class);
                intent.putExtra("expenseForm", setExpenseForm());
                startActivity(intent);
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpenseCalendarFragment expenseCalendarFragment = new ExpenseCalendarFragment(expenseDate);
                expenseCalendarFragment.show(AddExpenseActivity.this.getSupportFragmentManager(), "expenseCalendar");

                expenseCalendarFragment.setOnSelectListener(new ExpenseCalendarFragment.OnSelectListener() {
                    @Override
                    public void onSelect(View view, Calendar selectDate) {
                        tvDate.setText(Common.getExpenseCalendarDate(selectDate));
                        expenseDate = selectDate;
                    }
                });
            }
        });

        //  키보드에서 완료 버튼 누르면 키보드 내리기
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (etContent.isFocused()) {
                            etContent.clearFocus();
                        }

                        if (etExpense.isFocused()) {
                            etExpense.clearFocus();
                        }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }

                return true;
            }
        };

        etContent.setOnEditorActionListener(onEditorActionListener);
        etExpense.setOnEditorActionListener(onEditorActionListener);

        // 내용 입력 창이 눌리면 입력창 background 변경
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llContent.setBackgroundResource(R.drawable.textbox_typing);
                    ivPencil.setImageResource(R.drawable.icon_pencil_black);
                } else {
                    llContent.setBackgroundResource(R.drawable.textbox_default);
                    ivPencil.setImageResource(R.drawable.icon_pencil_gray);
                }
            }
        });

        // 금액 입력 창이 눌리면 입력창 background 변경
        etExpense.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    llExpense.setBackgroundResource(R.drawable.textbox_typing);
                    ivWon.setImageResource(R.drawable.won);
                    tvWon.setTextColor(Color.parseColor("#495057"));
                } else {
                    llExpense.setBackgroundResource(R.drawable.textbox_default);
                    ivWon.setImageResource(R.drawable.won_grey);
                    tvWon.setTextColor(Color.parseColor("#ADB5BD"));
                }
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 금액에 쉼표 추가
                if (etExpense.isFocused() && !TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    etExpense.setText(result);
                    etExpense.setSelection(result.length());
                }

                if (etExpense.length() > 0 && etContent.length() > 0 && selectCategory != null) {
                    btnNext.setEnabled(true);
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                    }
                } else {
                    btnNext.setEnabled(false);
                    if (inputManager.isAcceptingText()) {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                    } else {
                        btnNext.setBackgroundResource(R.drawable.button_enabled_false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        etContent.addTextChangedListener(textWatcher);
        etExpense.addTextChangedListener(textWatcher);
    }

    private void setButtonSize() {
        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                if (etExpense.length() > 0 && etContent.length() > 0 && selectCategory != null) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false);
                }

                contentLayoutParams.topMargin = (int) (10 * scale + 0.2f);
                contentLayoutParams.setMarginStart((int) (16 * scale + 0.2f));
                contentLayoutParams.setMarginEnd((int) (16 * scale + 0.2f));
                contentLayoutParams.bottomMargin = (int) (56 * scale + 0.2f);
                btnNext.setLayoutParams(contentLayoutParams);
            }
        });

        // 키보드가 올라왔을 때
        softKeyboardDetector.setOnShownKeyboard(new SoftKeyboardDetector.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {

                if (etExpense.length() > 0 && etContent.length() > 0 && selectCategory != null) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

                contentLayoutParams.topMargin = 0;
                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnNext.setLayoutParams(contentLayoutParams);
            }
        });
    }

    // 사용자가 선택한 카테고리 가져오기
    private void getGoalCategory() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.getGoalCategory();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();

                    Log.d(TAG, responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200) {
                        if (responseJson.get("data") != null) {
                            JsonArray jsonArray = responseJson.get("data").getAsJsonArray();

                            Gson gson = new Gson();
                            ArrayList<GoalCategoryCompact> responseCategory = gson.fromJson(jsonArray, new TypeToken<ArrayList<GoalCategoryCompact>>() {}.getType());

                            if (responseCategory != null) {
                                setGoalCategory(responseCategory);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AddExpenseActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGoalCategory(ArrayList<GoalCategoryCompact> categoryList) {
        GoalCategoryGridAdapter goalCategoryGridAdapter = new GoalCategoryGridAdapter(categoryList);
        goalCategoryGridAdapter.setOnItemClickListener(new GoalCategoryGridAdapter.OnItemClickListener() {
            @Override
            public void onClick(GoalCategoryCompact category) {
                selectCategory = category;

                if (etContent.isFocused() || etExpense.isFocused()) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    if (etContent.isFocused()) {
                        etContent.clearFocus();
                    }

                    if (etExpense.isFocused()) {
                        etExpense.clearFocus();
                    }
                }
            }
        });

        rvCategory.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategory.setAdapter(goalCategoryGridAdapter);
    }

    private ExpenseForm setExpenseForm() {
        ExpenseForm expenseForm = new ExpenseForm();

        expenseForm.setCategoryId(selectCategory.getCategoryId());
        expenseForm.setCustom(selectCategory.isCustom());
        expenseForm.setDate(Common.getCalendarToString(expenseDate));

        String expense = etExpense.getText().toString().replaceAll("\\,", "");
        expenseForm.setExpense(Integer.parseInt(expense));

        expenseForm.setExpenseDetail(etContent.getText().toString());

        return expenseForm;
    }

    private void setCancelDialog() {
        if (cancelDialog != null) return;
        makeCancelDialog();
    }

    // 기록 종료 확인 다이얼로그 만들기
    private void makeCancelDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        cancelDialog = builder.create();

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvContent = view.findViewById(R.id.tv_dialog_content);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);
        Button btnCancel = view.findViewById(R.id.btn_dialog_cancel);

        tvTitle.setText("기록을 종료할까요?");
        tvContent.setText("작성한 내용은 저장되지 않습니다");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });
    }
}