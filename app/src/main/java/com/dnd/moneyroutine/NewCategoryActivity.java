package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.item.CategoryItem;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class NewCategoryActivity extends AppCompatActivity {

    LinearLayout linearNewCategory;
    LinearLayout linearNewEx;
    LinearLayout linearNewIcon;
    EditText etNewCategory;
    EditText etNewEx;
    ImageView ivEraseName;
    ImageView ivEraseEx;
    ImageView ivBack;
    Button btnConfirm;
    CategoryItem newItem;
    ImageView btnEditEmoji;
    TextView tvNewEmoji;
    ConstraintLayout bgBlack;

    boolean flag = false;
//    private EmojIconActions emojIconActions;
//    private EmojiconEditText emojiconEditText;
//    private TextView emojiconTextView;
//    private View rootView;





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
        linearNewIcon=findViewById(R.id.linear_emoji);
        tvNewEmoji=findViewById(R.id.tv_emoji);
        bgBlack=findViewById(R.id.bg_black);

//        btnEditEmoji=findViewById(R.id.iv_edit_emoji);
//
//        emojiconEditText = (EmojiconEditText) findViewById(R.id.et_emoji);
//        emojiconTextView =  findViewById(R.id.tv_emoji);


        enterNewCategory(); //카테고리 이름 입력
        addEmoji();


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
                String icon = tvNewEmoji.getText().toString();

                if(name.length()>0){
                    Intent intent = new Intent(getApplicationContext(), OnboardingCategoryActivity.class);
                    newItem = new CategoryItem(icon, name, ex);
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

    private void addEmoji(){

        EditText et_emoji = findViewById(R.id.et_emoji);
        ArrayList<String> newEmoji = new ArrayList<>();
        newEmoji.add(tvNewEmoji.getText().toString());
        InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);


        linearNewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_emoji.requestFocus();
                et_emoji.setInputType(1);
                manager.showSoftInput(et_emoji, 0);
//                manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                et_emoji.requestFocus();
                et_emoji.setCursorVisible(true);
                bgBlack.setVisibility(View.VISIBLE);
            }
        });

        et_emoji.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                flag = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                //입력된 값이 이모지라면 이모지 부분 textview에 선택한 값 띄움
                if(flag){
                    flag=false;
                    if(isEmoji(et_emoji.getText().toString())){
                            if(tvNewEmoji.getText()!=et_emoji.getText()){
                                tvNewEmoji.setText(et_emoji.getText());
                                et_emoji.setText("");
                                manager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            else{
                                et_emoji.setText("");
                                manager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        bgBlack.setVisibility(View.GONE);
                    }
//                    else{
//                        et_emoji.setText(""); ->설정하면 오류나서 이모지가 아닌 값을 누른 뒤 이모지를 누르면 이모지 아닌 값이 표시돼버림
//                    }

                }
            }
        });
    }

    //입력된 값이 이모지인지 확인 -> 모든 이모지가 인식되지 않음
    private static boolean isEmoji(String message){
        Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
        Matcher matcher = rex.matcher(message);

        return matcher.find();
    }

}
