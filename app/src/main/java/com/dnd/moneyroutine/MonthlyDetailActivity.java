package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MonthlyDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvCategoryName;
    private TextView tvPercent;
    private TextView tvTotal;
    private RecyclerView rcContent;

    private String getCategoryName;
    private int getPercent;
    private int getTotal;

    DecimalFormat dcFormat = new DecimalFormat("#,###");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_detail);

        initView();
        setTextView();
        setListener();

    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back_monthly_detail);
        tvTitle = findViewById(R.id.tv_monthly_detail_title);
        tvCategoryName = findViewById(R.id.tv_month_detail_category);
        tvPercent = findViewById(R.id.tv_monthly_detail_percent);
        tvTotal = findViewById(R.id.tv_monthly_detail_total);
        rcContent = findViewById(R.id.rc_monthly_detail);
    }


    private void setTextView(){
//        getCategoryName=getIntent().getStringExtra("category name");
//        getPercent=getIntent().getIntExtra("percentage",0);
//        getTotal=getIntent().getIntExtra("expense",0);

        tvTitle.setText(getCategoryName);
        tvCategoryName.setText("월"+getCategoryName+"지출액");
        tvPercent.setText(getPercent+"%");
        tvTotal.setText("총 "+dcFormat.format(getTotal)+"원");

    }

    private void initAdapter(){}

    private void setListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}