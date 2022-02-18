package com.dnd.moneyroutine.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dnd.moneyroutine.R;

public class InformationDialogFragment extends DialogFragment {

    private Button btnConfirm;
    private Fragment fragment;

    public InformationDialogFragment() {

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.fragment_information_dialog, null);
        builder.setView(dialog);

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("information");
        btnConfirm = dialog.findViewById(R.id.btn_info_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InformationDialogFragment informationDialogFragment = (InformationDialogFragment) fragment;
                InformationDialogFragment.this.dismiss();

            }
        });


        return builder.create();
    }


}