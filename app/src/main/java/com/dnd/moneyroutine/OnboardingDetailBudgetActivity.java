package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.adapter.BudgetRecyclerViewAdapter;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.CategoryCompact;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OnboardingDetailBudgetActivity extends AppCompatActivity {

    private RecyclerView rcBudget;
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvAlert;
    private ImageView ivBack;
    private Button btnNext;

    private ArrayList<CategoryCompact> selectCategories;

    private BudgetRecyclerViewAdapter adapter;

    private SoftKeyboardDetector softKeyboardDetector;
    private LinearLayout.LayoutParams contentLayoutParams;

    private float scale;
    private String entireBudget;
    private String budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_detail_budget);


        entireBudget = getIntent().getStringExtra("totalBudget");
        selectCategories = (ArrayList<CategoryCompact>) getIntent().getSerializableExtra("selectCategory");

        initView();
        initAdapter();
        setBtnListener();
        setBtnSize();
        setTextView();
    }

    //edittext 외부 누르면 키보드 내려가면서 focus 없어지게
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
            if (tvAlert.getText().toString().contains("남음")) {
                tvAlert.setTextColor(Color.parseColor("#212529"));
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void initView() {
        tvTotal = findViewById(R.id.tv_budget_total);
        tvTitle = (TextView) findViewById(R.id.tv_entire_budget);
        tvAlert = findViewById(R.id.tv_budget_alert);
        ivBack = findViewById(R.id.iv_back_detail);
        btnNext = findViewById(R.id.btn_next_detail_budget);
        rcBudget = findViewById(R.id.rc_budget);
    }

    //recyclerview adapter 연결
    private void initAdapter() {
        rcBudget.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BudgetRecyclerViewAdapter(selectCategories);
        rcBudget.setAdapter(adapter);
    }

    private void setTextView() {
        budget = new DecimalFormat("#,###").format(Integer.parseInt(entireBudget));

        tvTitle.setText(budget + "원 안으로\n세부 예산 항목을 정해주세요"); //제목
        tvAlert.setText(budget + "원 남음"); //남은 예산, 초과 예산
        tvTotal.setText(budget); //전체 예산
    }

    private void setBtnSize() {
        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));
        contentLayoutParams = (LinearLayout.LayoutParams) btnNext.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                getCurrentFocus().clearFocus();

                if (tvAlert.getText().toString().contains("남음")) {
                    tvAlert.setTextColor(Color.parseColor("#212529"));
                }

                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
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
                if (tvAlert.getText().toString().contains("남음")) {
                    tvAlert.setTextColor(Color.parseColor("#047E74"));
                }

                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
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

    private void setBtnListener() {
        //뒤로가기
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}