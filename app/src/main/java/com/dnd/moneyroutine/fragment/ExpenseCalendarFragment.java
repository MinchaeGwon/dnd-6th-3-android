package com.dnd.moneyroutine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.adapter.CalendarAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// 지출 날짜 bottom sheet fragment
public class ExpenseCalendarFragment extends BottomSheetDialogFragment {

    public interface OnSelectListener {
        void onSelect(View view, Calendar selectDate);
    }

    private static final int DAYS_COUNT = 42;

    private ImageButton ibCancel;
    private GridView gvCalendar;
    private TextView tvHeader;

    private Calendar selectDate;

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public ExpenseCalendarFragment() {}

    public ExpenseCalendarFragment(Calendar selectDate) {
        this.selectDate = selectDate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_calendar, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setHideable(true);
                behavior.setPeekHeight(0);
            }
        });

        init(view);
        setCalendar();
    }

    private void init(View v) {
        initView(v);
        setBtnClickListeners();
    }

    // UI 초기화
    private void initView(View v) {
        ibCancel = v.findViewById(R.id.ib_calendar_cancel);
        tvHeader = v.findViewById(R.id.tv_calendar_header);
        gvCalendar = v.findViewById(R.id.gv_calendar);
    }

    // 리스너 설정
    private void setBtnClickListeners() {
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        gvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Date date = (Date) adapterView.getItemAtPosition(position);

                selectDate = Calendar.getInstance();
                selectDate.setTime(date);

                dismiss();
                onSelectListener.onSelect(view, selectDate);
            }
        });
    }

    // 캘린더 설정
    public void setCalendar() {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) selectDate.clone();

        int year = selectDate.get(Calendar.YEAR);
        int month = selectDate.get(Calendar.MONTH) + 1;

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        CalendarAdapter calendarAdapter = new CalendarAdapter(getContext(), cells, year, month, selectDate);
        gvCalendar.setAdapter(calendarAdapter);

        // update title
        tvHeader.setText(year + "년 " + month + "월");
    }
}
