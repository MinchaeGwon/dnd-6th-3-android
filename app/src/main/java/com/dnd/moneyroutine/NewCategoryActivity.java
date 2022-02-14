package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.CustomCategoryModel;
import com.dnd.moneyroutine.item.CategoryItem;
import com.dnd.moneyroutine.service.RequestService;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.regex.Pattern.compile;

public class NewCategoryActivity extends AppCompatActivity {

    LinearLayout linearNewCategory;
    LinearLayout linearNewEx;
    LinearLayout linearNewIcon;
    ConstraintLayout bgBlack;
    TextView tvNewEmoji;
    EditText etNewCategory;
    EditText etNewEx;
    //    ImageView btnEditEmoji;
    ImageView ivEraseName;
    ImageView ivEraseEx;
    ImageView ivBack;
    Button btnConfirm;

    CategoryItem newItem;
    CustomCategoryModel customCategoryModel;

    private SoftKeyboardDetector softKeyboardDetector;
    private InputMethodManager inputManager;
    private ConstraintLayout.LayoutParams contentLayoutParams;
    private float scale;

    boolean flag = false;
//    private EmojIconActions emojIconActions;
//    private EmojiconEditText emojiconEditText;
//    private TextView emojiconTextView;
//    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);


//        btnEditEmoji=findViewById(R.id.iv_edit_emoji);
//
//        emojiconEditText = (EmojiconEditText) findViewById(R.id.et_emoji);
//        emojiconTextView =  findViewById(R.id.tv_emoji);

        initView();
        enterNewCategory(); //카테고리 이름 입력
        addEmoji(); //이모지 선택
        setBtnSize();


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

                if (name.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), OnboardingCategoryActivity.class);
                    newItem = new CategoryItem(icon, name, ex);
                    intent.putExtra("new category name", newItem);
                    setResult(RESULT_OK, intent);

                    finish();
                }

                customCategoryModel =new CustomCategoryModel(ex, name);
                customCategoryServer();


            }
        });

    }

    private void initView() {
        linearNewCategory = findViewById(R.id.linear_new_category);
        linearNewEx = findViewById(R.id.linear_new_category_ex);
        etNewCategory = findViewById(R.id.et_new_category);
        etNewEx = findViewById(R.id.et_new_category_ex);
        ivEraseName = findViewById(R.id.iv_erase_new_category);
        ivEraseEx = findViewById(R.id.iv_erase_new_category_ex);
        btnConfirm = findViewById(R.id.btn_next_new_category);
        ivBack = findViewById(R.id.iv_back_new_category);
        linearNewIcon = findViewById(R.id.linear_emoji);
        tvNewEmoji = findViewById(R.id.tv_emoji);
        bgBlack = findViewById(R.id.bg_black);
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }


    private void enterNewCategory() {
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
                } else {
                    linearNewCategory.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseName.setVisibility(View.INVISIBLE);
                }
            }
        });

        //설명 edittext 누르면 디자인 변경
        etNewEx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    linearNewEx.setBackgroundResource(R.drawable.textbox_typing);
                    ivEraseEx.setVisibility(View.VISIBLE);

                    ivEraseEx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etNewEx.setText("");
                        }
                    });
                } else {
                    linearNewEx.setBackgroundResource(R.drawable.textbox_default);
                    ivEraseEx.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void setBtnSize() {

        softKeyboardDetector = new SoftKeyboardDetector(this);
        addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));
        contentLayoutParams = (ConstraintLayout.LayoutParams) btnConfirm.getLayoutParams();
        scale = getResources().getDisplayMetrics().density;

        bgBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgBlack.setVisibility(View.GONE);
                inputManager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


        //키보드 내려갔을 때
        softKeyboardDetector.setOnHiddenKeyboard(new SoftKeyboardDetector.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {

                //키보드 내려가면 이모지 선택시 어두워졌던 배경 다시 원래대로
                bgBlack.setVisibility(View.GONE);

                if (etNewCategory.getText().toString().length() > 0) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);

                } else {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);


                }

                contentLayoutParams.setMarginStart((int) (16 * scale + 0.2f));
                contentLayoutParams.setMarginEnd((int) (16 * scale + 0.2f));
                contentLayoutParams.bottomMargin = (int) (56 * scale + 0.2f);
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });

        // 키보드가 올라왔을 때
        softKeyboardDetector.setOnShownKeyboard(new SoftKeyboardDetector.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {

                if (etNewCategory.getText().toString().length() > 0) {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);

                } else {
                    btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);


                }

                contentLayoutParams.setMarginStart(0);
                contentLayoutParams.setMarginEnd(0);
                contentLayoutParams.bottomMargin = 0;
                btnConfirm.setLayoutParams(contentLayoutParams);
            }
        });


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //지출 분야가 입력돼야지만 버튼 enable
                if (etNewCategory.getText().toString().length() > 0) {
                    btnConfirm.setEnabled(true);
                    if (inputManager.isAcceptingText()) {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_true_keyboard_up);

                    } else {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_true);
                    }
                } else {
                    btnConfirm.setEnabled(false);
                    if (inputManager.isAcceptingText()) {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_false_keyboard_up);

                    } else {
                        btnConfirm.setBackgroundResource(R.drawable.button_enabled_false);
                    }
                }

//                if(btnConfirm.isEnabled()){
//
//                }
//                else{
//
//                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        etNewCategory.addTextChangedListener(textWatcher);
        etNewEx.addTextChangedListener(textWatcher);
    }


    //이모지 편집
    private void addEmoji() {

        EditText et_emoji = findViewById(R.id.et_emoji);
        ArrayList<String> newEmoji = new ArrayList<>();
        newEmoji.add(tvNewEmoji.getText().toString());


        linearNewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이모지 편집 누르면 키보드 띄우고 배경 어둡게
                et_emoji.requestFocus();
                et_emoji.setInputType(1);
                inputManager.showSoftInput(et_emoji, 0);
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
                if (flag) {
                    flag = false;
                    if (isEmoji(et_emoji.getText().toString())) {
                        if (tvNewEmoji.getText() != et_emoji.getText()) {
                            tvNewEmoji.setText(et_emoji.getText());
//                            tvNewEmoji.setText(et_emoji.getText().toString().charAt(et_emoji.length()-1));
                            et_emoji.setText("");
                            inputManager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        } else {
                            et_emoji.setText("");
                            inputManager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        bgBlack.setVisibility(View.GONE);
                    }
//                    if(isLetter(et_emoji.getText().toString())){
//                        et_emoji.setText("");
//                    }
                }
            }
        });
    }

    private static boolean isEmoji(String message) {
//        Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
        Pattern rex = Pattern.compile("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]");
        Matcher matcher = rex.matcher(message);

        return matcher.find();
    }
//    private static boolean isLetter(String message){
//        Pattern rex = Pattern.compile("^[0-9a-zA-Z가-힣]*$");
//        Matcher matcher = rex.matcher(message);
//
//        return matcher.find();
//    }

    private void customCategoryServer() {
        Call<CustomCategoryModel> call = RequestService.getInstance().create(customCategoryModel);
        call.enqueue(new Callback<CustomCategoryModel>() {

            @Override
            public void onResponse(Call<CustomCategoryModel> call, Response<CustomCategoryModel> response) {
                if (response.isSuccessful()) {
                    CustomCategoryModel post = response.body();
                    Log.d("custom category", post.toString());
                } else {
                    Log.e("custom category", "error: " + response.code());
                    return;
                }
            }
            @Override
            public void onFailure(Call<CustomCategoryModel> call, Throwable t) {
                Log.e("custom category", "fail: " + t.getMessage());
            }
        });
    }

}
