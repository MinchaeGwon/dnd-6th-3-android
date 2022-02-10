package com.dnd.moneyroutine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.dnd.moneyroutine.adapter.CategoryGridViewAdapter;
import com.dnd.moneyroutine.item.BudgetItem;
import com.dnd.moneyroutine.item.CategoryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OnboardingCategoryActivity extends AppCompatActivity {

    private ExpandableHeightGridView gridView = null;
    CategoryGridViewAdapter adapter = null;
    private Button btnNext;
    private ArrayList<CategoryItem> cItem;
    private ArrayList<CategoryItem> bgList;
    private ConstraintLayout background;
    private LinearLayout linearAddcategory;

    CategoryItem newCategory;

    ArrayList<String> icon;
    ArrayList<String> name;
    ArrayList<String> ex;

    ArrayList<CategoryItem> newItem;

    ArrayList<Integer> selectedItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_catecory);


        gridView = (ExpandableHeightGridView)findViewById(R.id.gv_category);
        gridView.setExpanded(true);
        btnNext=(Button)findViewById(R.id.btn_next1);


//        int[] categoryIcons = {R.drawable.coffee, R.drawable.food, R.drawable.beer, R.drawable.book, R.drawable.bus, R.drawable.bag, R.drawable.computer, R.drawable.life, R.drawable.pill};
        String[] categoryIcons = {"☕", "🥘","🍻", "📚 ", "🚌", "👜", "🖥", "🧻", "💊"};
        String[] categoryNames = {"카페", "식비", "유흥비", "자기계발", "교통비", "쇼핑", "정기구독", "생활용품", "건강"};
        String[] categoryExs = {"커피 및 디저트", "밥값", "주류비, 취미", "책 및 강의", "택시, 버스, 지하철", "의류, 화장품", "넷플릭스 등", "가전제품 등", "병원비, 운동"};

        icon = new ArrayList<>();
        name = new ArrayList<>();
        ex = new ArrayList<>();
        cItem=new ArrayList<>();

        for(int i=0; i<categoryNames.length; i++){
            icon.add(i, categoryIcons[i]);
            name.add(i, categoryNames[i]);
            ex.add(i, categoryExs[i]);
        }

        adapter=new CategoryGridViewAdapter();

        for (int i = 0; i < categoryNames.length; i++) {
//            cItem.add();
            adapter.addItem(new CategoryItem(icon.get(i), name.get(i), ex.get(i)));
        }

//        adapter = new CategoryGridViewAdapter(this,cItem);
        gridView.setAdapter(adapter);


        //카테고리 추가 버튼
        linearAddcategory=findViewById(R.id.btn_addcategory);
        linearAddcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewCategoryActivity.class);
                startActivityResult.launch(intent);

            }
        });


        //아이템 선택
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                background = (ConstraintLayout) view;
                gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

                //선택해제
                if(selectedItem.contains(position)) {
                    background.setSelected(false);
                    selectedItem.remove(Integer.valueOf(position));
                }
                //선택
                else{
                    background.setSelected(true);
                    selectedItem.add(position);
                }

                //선택해제시
                if(background.isSelected()){
                    background.setBackgroundResource(R.drawable.button_category_clicked);
                }
                //선택시
                else{
                    background.setBackgroundResource(R.drawable.button_category_unclicked);
                }

                //하나라도 선택되면 다음 버튼 활성화
                if(selectedItem.size()>0){
                    btnNext.setEnabled(true);
                }
                else if(selectedItem.size()==0){
                    btnNext.setEnabled(false);
                }

            }
        });




        //다음 페이지로
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bgList = new ArrayList<>();
                Collections.sort(selectedItem);

                //선택된 아이템
                for(int i=0; i<selectedItem.size();i++){
                    int index=selectedItem.get(i);
                    bgList.add(new CategoryItem(icon.get(index), name.get(index), ex.get(index)));
                }
                Intent intent = new Intent(getApplicationContext(), OnboardingEntireBudgetActivity.class);
                intent.putExtra("BudgetItem", bgList);
                intent.putExtra("New Item", newItem);
                startActivity(intent);

            }
        });

    }


    //새로운 아이템 추가 페이지에서 입력된 값 받아오기
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent intent= result.getData();
                        newCategory= (CategoryItem) intent.getSerializableExtra("new category name");
//                        name.add(.);
                        String newIcon = newCategory.getCategoryIcon();
                        String newName = newCategory.getCategoryName();
                        String newEx = newCategory.getCategoryEx();

                        icon.add(newIcon);
                        name.add(newName);
                        ex.add(newEx);

                        newItem=new ArrayList<>();

                        newItem.add(new CategoryItem(newIcon, newIcon, newEx));

                        adapter.addItem(new CategoryItem(newIcon, newName, newEx));
                        gridView.invalidateViews();
                        gridView.setAdapter(adapter);


                        //새 카테고리 추가 후 이전에 선택했던 항목 선택된 상태로 background 설정
                        ViewTreeObserver vto = gridView.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                for(int x=0; x<selectedItem.size(); x++) {
                                    ConstraintLayout cl = (ConstraintLayout) gridView.getChildAt(selectedItem.get(x));
                                    cl.setBackgroundResource(R.drawable.button_category_clicked);
                                }
                            }
                        });

                    }
                }
            });


}
