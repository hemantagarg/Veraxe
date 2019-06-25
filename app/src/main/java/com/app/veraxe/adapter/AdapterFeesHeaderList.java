package com.app.veraxe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelFeesHistory;
import com.app.veraxe.model.ModelStudentFees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterFeesHeaderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ModelStudentFees.DataBean.FeesBean> detail;
    Context mContext;
    OnCustomItemClicListener listener;

    public AdapterFeesHeaderList(Context context, OnCustomItemClicListener lis, List<ModelStudentFees.DataBean.FeesBean> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_fees_header_list, parent, false);

        vh = new CustomViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CustomViewHolder) {

            ModelStudentFees.DataBean.FeesBean m1 = detail.get(position);

            if (m1.getRowType()==1) {
                ((CustomViewHolder) holder).mTvTitle.setVisibility(View.GONE);
                ((CustomViewHolder) holder).mRlContent.setVisibility(View.VISIBLE);
                ((CustomViewHolder) holder).mTvCycle.setText(m1.getCycle());
                ((CustomViewHolder) holder).text_total_amount.setText(m1.getAmount());
                ((CustomViewHolder) holder).mTvName.setText(m1.getHeaderName());
            }else {
                ((CustomViewHolder) holder).mTvTitle.setVisibility(View.VISIBLE);
                ((CustomViewHolder) holder).mRlContent.setVisibility(View.GONE);
                ((CustomViewHolder) holder).mTvTitle.setText(m1.getHeaderName());
            }
        }
    }


    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRlContent;
        TextView mTvCycle, text_total_amount, mTvName,mTvTitle;

        public CustomViewHolder(View view) {
            super(view);

            this.mTvCycle = view.findViewById(R.id.mTvCycle);
            this.mTvName = view.findViewById(R.id.mTvName);
            this.mTvTitle = view.findViewById(R.id.mTvTitle);
            this.mRlContent = view.findViewById(R.id.mRlContent);
            this.text_total_amount = view.findViewById(R.id.text_total_amount);
        }
    }


}

