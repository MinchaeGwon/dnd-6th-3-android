package com.dnd.moneyroutine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dnd.moneyroutine.adapter.CategoryGridViewAdapter;
import com.dnd.moneyroutine.item.CategoryItem;

import java.util.ArrayList;

// 지출 분야 추가 activity
public class AddExpenseCatActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private ImageButton ibCancel;

    private GridView gvCategory;
    private LinearLayout btnAddCustomCat;

    private CategoryGridViewAdapter adapter = null;

    private ArrayList<CategoryItem> bgList;
    private ArrayList<String> icon;
    private ArrayList<String> name;
    private ArrayList<String> ex;
    private ArrayList<CategoryItem> newItem;
    private ArrayList<Integer> selectedItem = new ArrayList<>();
    private CategoryItem newCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_cat);

        initView();
        setListener();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_add_cat_back);
        ibCancel = findViewById(R.id.ib_add_cat_cancel);

        gvCategory = findViewById(R.id.gv_category);

        btnAddCustomCat = findViewById(R.id.btn_add_expense_cat);
    }

    private void setListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddCustomCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewCategoryActivity.class);
                startActivityResult.launch(intent);
            }
        });
    }

    // 새로운 카테고리 추가 페이지에서 입력된 값 받아오기
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        newCategory = (CategoryItem) intent.getSerializableExtra("new category name");
//                        name.add(.);
                        String newIcon = newCategory.getCategoryIcon();
                        String newName = newCategory.getCategoryName();
                        String newEx = newCategory.getCategoryEx();

                        icon.add(newIcon);
                        name.add(newName);
                        ex.add(newEx);

                        newItem = new ArrayList<>();

                        newItem.add(new CategoryItem(newIcon, newName, newEx));

                        adapter.addItem(new CategoryItem(newIcon, newName, newEx));
                        gvCategory.invalidateViews();
                        gvCategory.setAdapter(adapter);


                        // 새 카테고리 추가 후 이전에 선택했던 항목 선택된 상태로 background 설정
                        ViewTreeObserver vto = gvCategory.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                for (int x = 0; x < selectedItem.size(); x++) {
                                    ConstraintLayout cl = (ConstraintLayout) gvCategory.getChildAt(selectedItem.get(x));
                                    cl.setBackgroundResource(R.drawable.button_category_clicked);
                                }
                            }
                        });

                    }
                }
            });
}