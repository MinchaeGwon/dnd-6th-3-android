package com.dnd.moneyroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
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
import android.widget.Toast;

import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.custom.SoftKeyboardDetector;
import com.dnd.moneyroutine.dto.CustomCategoryCreateDto;
import com.dnd.moneyroutine.dto.CategoryItem;
import com.dnd.moneyroutine.dto.GoalCategoryCompact;
import com.dnd.moneyroutine.service.HeaderRetrofit;
import com.dnd.moneyroutine.service.JWTUtils;
import com.dnd.moneyroutine.service.RequestService;
import com.dnd.moneyroutine.service.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.util.regex.Pattern.compile;

public class NewCategoryActivity extends AppCompatActivity {

    private LinearLayout linearNewCategory;
    private LinearLayout linearNewEx;
    private LinearLayout linearNewIcon;
    private ConstraintLayout bgBlack;
    private TextView tvNewEmoji;
    private EditText etNewCategory;
    private EditText etNewEx;
    //    ImageView btnEditEmoji;
    private ImageView ivEraseName;
    private ImageView ivEraseEx;
    private ImageView ivBack;

    private Button btnConfirm;

    private CategoryItem newItem;
    private CustomCategoryCreateDto customCategoryCreateDto;

    private String token;
    private int userId;

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

                String iconToByte = "";
                String name = etNewCategory.getText().toString();
                String ex = etNewEx.getText().toString();
                String icon = tvNewEmoji.getText().toString();

                customCategoryCreateDto =new CustomCategoryCreateDto();
                customCategoryCreateDto.setDetail(ex);

                try {
                    iconToByte = URLEncoder.encode(icon, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                customCategoryCreateDto.setEmoji(iconToByte);
                customCategoryCreateDto.setName(name);

                if (name.length() > 0) {
                    newItem = new CategoryItem(icon, name, ex);
                }

                customCategoryServer();
            }
        });

    }

    private void initView() {
        token = PreferenceManager.getToken(NewCategoryActivity.this, Constants.tokenKey);
        userId = JWTUtils.getUserId(token);


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
                if (et_emoji.length() > 0 ) {
                    flag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                //입력된 값이 이모지라면 이모지 부분 textview에 선택한 값 띄움
                if (flag) {
                    flag = false;

                    if (isEmoji(et_emoji.getText().toString())) {
                        if (tvNewEmoji.getText() != et_emoji.getText()) {
                            tvNewEmoji.setText(et_emoji.getText());
                        }

                        inputManager.hideSoftInputFromWindow(NewCategoryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        bgBlack.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(NewCategoryActivity.this, "이모지만 입력해 주세요", Toast.LENGTH_SHORT).show();
                    }

                    et_emoji.setText("");
                }
            }
        });
    }


    private static boolean isEmoji(String message) {
        Pattern rex = Pattern.compile("[\\x{10000}-\\x{10ffff}\ud800-\udfff]");
//        Pattern rex = Pattern.compile("[^\uAC00-\uD7A3xfe0-9a-zA-Z가-힣\\s]");
        Matcher matcher = rex.matcher(message);

        return matcher.matches();
    }

//    private static boolean isLetter(String message){
//        Pattern rex = Pattern.compile("^[0-9a-zA-Z가-힣]*$");
//        Matcher matcher = rex.matcher(message);
//
//        return matcher.find();
//    }

    private void customCategoryServer() {
        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
        RetrofitService retroService = retrofit.create(RetrofitService.class);

        Call<JsonObject> call = retroService.create(customCategoryCreateDto);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    Log.d("custom category", responseJson.toString());

                    if (responseJson.get("statusCode").getAsInt() == 200 && !responseJson.get("data").isJsonNull()) {
                        int newCategoryId = responseJson.get("data").getAsInt();

                        Intent intent = new Intent(getApplicationContext(), OnboardingCategoryActivity.class);
                        intent.putExtra("new category name", newItem);
                        intent.putExtra("newCategoryId", newCategoryId);
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                } else {
                    Log.e("custom category", "error: " + response.code());
                    return;
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("custom category", "fail: " + t.getMessage());
                Toast.makeText(NewCategoryActivity.this, "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
