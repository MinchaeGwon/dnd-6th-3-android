package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MonthlyDetailActivity extends AppCompatActivity {

    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_detail);

        initView();
        setListener();
    }

    private void initView(){
        ivBack = findViewById(R.id.iv_back_monthly_detail);
    }

    private void setListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}