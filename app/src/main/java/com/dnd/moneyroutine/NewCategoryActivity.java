package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dnd.moneyroutine.item.CategoryItem;

import java.util.ArrayList;

public class NewCategoryActivity extends AppCompatActivity {

    LinearLayout linearNewCategory;
    LinearLayout linearNewEx;
    EditText etNewCategory;
    EditText etNewEx;
    ImageView ivEraseName;
    ImageView ivEraseEx;
    ImageView ivBack;
    Button btnConfirm;
    CategoryItem newItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);


        linearNewCategory = findViewById(R.id.linear_new_category);
        linearNewEx =findViewById(R.id.linear_new_category_ex);
        etNewCategory = findViewById(R.id.et_new_category);
        etNewEx =findViewById(R.id.et_new_category_ex);
        ivEraseName =findViewById(R.id.iv_erase_new_category);
        ivEraseEx =findViewById(R.id.iv_erase_new_category_ex);
        btnConfirm=findViewById(R.id.btn_next_new_category);
        ivBack = findViewById(R.id.iv_back_new_category);


        enterNewCategory(); //카테고리 이름 입력

        //이전 버튼
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //확인
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etNewCategory.getText().toString();
                String ex = etNewEx.getText().toString();
                if(name.length()>0){
                    Intent intent = new Intent(getApplicationContext(), OnboardingCategoryActivity.class);
                    newItem = new CategoryItem("", name, ex);
                    intent.putExtra("new category name",newItem);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

    }

    private void enterNewCategory(){

        //이름 edittext 누르면 background 변경경
        etNewCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    linearNewCategory.setBackgroundResource(R.drawable.textbox_typing);
                    ivEraseName.setVisibility(View.VISIBLE);

                    //x 이미지 누르면 edittext 비움
                    ivEraseName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etNewCategory.setText("");
                        }
                    });
                }
                else{
                    linearNewCategory.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseName.setVisibility(View.INVISIBLE);
                }
            }
        });

        //설명 edittext
        etNewEx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if(isFocus){
                    linearNewEx.setBackgroundResource(R.drawable.textbox_typing);
                    ivEraseEx.setVisibility(View.VISIBLE);

                    ivEraseEx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etNewEx.setText("");
                        }
                    });
                }
                else{
                    linearNewEx.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseEx.setVisibility(View.INVISIBLE);
                }

            }
        });

    }
}
