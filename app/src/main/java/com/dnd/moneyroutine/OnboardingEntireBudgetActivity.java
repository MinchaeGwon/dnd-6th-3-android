package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.item.BudgetItem;
import com.dnd.moneyroutine.item.CategoryItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OnboardingEntireBudgetActivity extends AppCompatActivity {

    private EditText etEnter;
    private Button btnNext;
    private String budget;
    private TextView tLastvDay;
    private ArrayList<BudgetItem> bgList;
    private LinearLayout linearEntireBudget;
    private ImageView ivWon;
    private TextView tvWon;
    private ImageView ivBack;
    private Button btn_20w;
    private Button btn_30w;
    private Button btn_40w;
    private Button btn_50w;

    private ArrayList<CategoryItem> newItem;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_entire_budget);

        btnNext=(Button)findViewById(R.id.btn_next2);
        etEnter = (EditText)findViewById(R.id.et_entire_budget);
        linearEntireBudget=findViewById(R.id.linear_entire_budget);
        ivWon=findViewById(R.id.iv_won_entire);
        tvWon=findViewById(R.id.tv_won_entire);


        getEndDate(); //달의 마지막 날 구하기
        addListener();

        ivBack = findViewById(R.id.iv_back_entire);
        //뒤로가기
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        //다음 버튼 누르면 예산 입력된 값 넘기기
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budget = etEnter.getText().toString();
                String budgetToString = budget.replaceAll("\\,","");


                bgList =(ArrayList<BudgetItem>)getIntent().getSerializableExtra("BudgetItem");
                newItem=(ArrayList<CategoryItem>)getIntent().getSerializableExtra("New item");


                Intent intent = new Intent(getApplicationContext(), OnboardingDetailBudgetActivity.class);
                intent.putExtra("Budget",budgetToString);
                intent.putExtra("BudgetItem", bgList);
                intent.putExtra("New Item", newItem);

                startActivity(intent);
            }
        });


    }

    //달의 마지막 날 구하기
    private void getEndDate(){
        DecimalFormat df = new DecimalFormat("00");
        Calendar cal = Calendar.getInstance();

        tLastvDay=(TextView)findViewById(R.id.tv_last_day);

        String month  = df.format(cal.get(Calendar.MONTH) + 1); //월
        String day =  df.format(cal.getActualMaximum(Calendar.DAY_OF_MONTH )); //일

        tLastvDay.setText(month+"월 "+day+"일까지의 예산입니다");

    }

    private void addListener(){

        //예산 입력 창이 눌리면 입력창 background 변경
        etEnter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if(isFocus){
                    linearEntireBudget.setBackgroundResource(R.drawable.textbox_typing);
                    ivWon.setImageResource(R.drawable.won);
                    tvWon.setTextColor(Color.parseColor("#495057"));
                }
                else {
                    linearEntireBudget.setBackgroundResource(R.drawable.textbox_default);
                    ivWon.setImageResource(R.drawable.won_grey);
                    tvWon.setTextColor(Color.parseColor("#ADB5BD"));
                }
            }
        });


        etEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //쉼표 추가
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    etEnter.setText(result);
                    etEnter.setSelection(result.length());
                }

                //입력되면 버튼 활성화
                if(etEnter.getText().toString().length()>0){
                    btnNext.setEnabled(true);
                }
                else{
                    btnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //n만원 버튼 클릭시 n만원으로 edittext setText
        btn_20w=findViewById(R.id.btn_20w);
        btn_30w=findViewById(R.id.btn_30w);
        btn_40w=findViewById(R.id.btn_40w);
        btn_50w=findViewById(R.id.btn_50w);

        View.OnClickListener onClickListener = new View.OnClickListener(){
            String text;
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.btn_20w:
                        text="200,000";
                        break;

                    case  R.id.btn_30w:
                        text="300,000";
                        break;

                    case R.id.btn_40w:
                        text="400,000";
                        break;

                    case R.id.btn_50w:
                        text="500,000";
                        break;

                }
                etEnter.setText(text);
            }
        };

        btn_20w.setOnClickListener(onClickListener);
        btn_30w.setOnClickListener(onClickListener);
        btn_40w.setOnClickListener(onClickListener);
        btn_50w.setOnClickListener(onClickListener);
    }
}