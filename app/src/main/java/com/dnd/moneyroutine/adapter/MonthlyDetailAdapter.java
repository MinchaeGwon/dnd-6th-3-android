package com.dnd.moneyroutine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.dto.ExpenditureDetailDto;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MonthlyDetailAdapter  extends RecyclerView.Adapter<MonthlyDetailAdapter.ViewHolder> {

    private ArrayList<ExpenditureDetailDto> expenditureDetailList;

    public MonthlyDetailAdapter(ArrayList<ExpenditureDetailDto> list) {
        this.expenditureDetailList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_detail, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyDetailAdapter.ViewHolder holder, int position) {

        holder.onBind(expenditureDetailList.get(position));

    }

    @Override
    public int getItemCount() {
        return expenditureDetailList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private RecyclerView rcContent;
        private MonthlyDetailContentAdapter adapter;
        private ArrayList<ExpenditureDetailDto> mList;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate=itemView.findViewById(R.id.tv_date_monthly_detail);
            rcContent=itemView.findViewById(R.id.rc_content_monthly_detail);
        }

        private void onBind(ExpenditureDetailDto expenditureDetailDto){

//            tvDate.setText(expenditureDetailDto.getDate().format(DateTimeFormatter.ofPattern("M월 d일")));
//            mList = new ArrayList<>();
//            mList.add(new ExpenditureDetailDto(LocalDate.now(),3000,"dd"));
//
//
//            adapter=new MonthlyDetailContentAdapter(mList);
//            rcContent.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//            rcContent.setAdapter(adapter);


        }



    }
}
