package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dnd.moneyroutine.adapter.MonthlyDetailAdapter;
import com.dnd.moneyroutine.dto.ExpenditureDetailDto;

import java.util.ArrayList;

public class ExpenseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        initView();
    }

    private void initView() {
        ImageButton ibBack = findViewById(R.id.ib_dtl_expense_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}