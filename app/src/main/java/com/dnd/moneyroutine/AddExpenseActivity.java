package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dnd.moneyroutine.fragment.ExpenseCalendarFragment;

// 소비 입력 activity
public class AddExpenseActivity extends AppCompatActivity {

    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initView();
        setListener();
    }

    private void initView() {
        tvDate = findViewById(R.id.tv_add_expense_date);
    }

    private void setListener() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpenseCalendarFragment expenseCalendarFragment = new ExpenseCalendarFragment();
                expenseCalendarFragment.show(AddExpenseActivity.this.getSupportFragmentManager(), "expenseCalendar");
            }
        });
    }
}