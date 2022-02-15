package com.dnd.moneyroutine;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CalendarFragment extends BottomSheetDialogFragment {

    ConstraintLayout layout;

    public static CalendarFragment getInstance(){
        return new CalendarFragment();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        layout=view.findViewById(R.id.cl_calendar);
        layout.setClipToOutline(true);

        // Inflate the layout for this fragment
        return view;
    }
}