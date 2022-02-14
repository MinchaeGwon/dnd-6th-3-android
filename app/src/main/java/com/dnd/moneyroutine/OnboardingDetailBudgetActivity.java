package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.adapter.BudgetRecyclerViewAdapter;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.CustomCategoryModel;
import com.dnd.moneyroutine.item.BudgetItem;
import com.dnd.moneyroutine.item.CategoryItem;
import com.dnd.moneyroutine.service.RequestService;
import com.dnd.moneyroutine.service.RetrofitService;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingDetailBudgetActivity extends AppCompatActivity {

    private RecyclerView rcBudget;
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvAlert;
    private ImageView ivBack;
    private Button btnNext;

    private ArrayList<CategoryItem> cList;
    private ArrayList<BudgetItem> bgList;
    private ArrayList<CategoryItem> newItem;

    private BudgetRecyclerViewAdapter adapter;

    private SoftKeyboardDetector softKeyboardDetector;
    private InputMethodManager inputManager;
    private LinearLayout.LayoutParams contentLayoutParams;

    private EditText etItem;

    private float scale;
    private String entireBudget;
    private String budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_detail_budget);

        Intent intent = getIntent();
        entireBudget = intent.getStringExtra("Budget");
        cList = (ArrayList<CategoryItem>) getIntent().getSerializableExtra("BudgetItem");
        newItem = (ArrayList<CategoryItem>) getIntent().getSerializableExtra("NewItem");

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
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }


    //recyclerview adapter 연결
    private void initAdapter() {

        ArrayList<Integer> enteredBudget = new ArrayList<>();
        rcBudget.setLayoutManager(new LinearLayoutManager(this));
        bgList = new ArrayList<>();

        //intent로 받아온 선택된 값들 담아서 adapter에 적용
        for (int i = 0; i < cList.size(); i++) {
            bgList.add(new BudgetItem(i, cList.get(i).getCategoryIcon(), cList.get(i).getCategoryName()));
        }
        adapter = new BudgetRecyclerViewAdapter(bgList);
        rcBudget.setAdapter(adapter);

    }

    private void setTextView() {
        budget = new DecimalFormat("#,###").format(Integer.parseInt(entireBudget));

        tvTitle.setText(budget + "원 안으로\n세부 예산 항목을 정해주세요"); //제목
        tvAlert.setText(budget + " 원 남음"); //남은 예산, 초과 예산
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
                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true);
                } else {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false);
                }

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
                    tvAlert.setTextColor(Color.parseColor("#0DC9B9"));
                } else if (tvAlert.getText().toString().contains("초과")) {
                    tvAlert.setTextColor(Color.parseColor("#FD5E6E"));
                }

                if (btnNext.isEnabled()) {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);
                } else {
                    btnNext.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);
                }

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

        //다음 버튼
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingDetailBudgetActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
//                for (int i = 0; i < newItem.size(); i++) {
//                    customCategoryServer(newItem.get(i).getCategoryEx(), newItem.get(i).getCategoryName());
//                }
            }
        });

    }




}