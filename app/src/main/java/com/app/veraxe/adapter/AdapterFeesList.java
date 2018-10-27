package com.app.veraxe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelFeesHistory;

import java.util.ArrayList;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterFeesList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ModelFeesHistory> detail;
    Context mContext;
    OnCustomItemClicListener listener;

    public AdapterFeesList(Context context, OnCustomItemClicListener lis, ArrayList<ModelFeesHistory> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_fees_list, parent, false);

        vh = new CustomViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CustomViewHolder) {

            ModelFeesHistory m1 = detail.get(position);

            ((CustomViewHolder) holder).text_invoice_no.setText(mContext.getString(R.string.invoice_no)+m1.getInviceNo());
            ((CustomViewHolder) holder).text_total_amount.setText("Total amount: "+m1.getTotal());
            ((CustomViewHolder) holder).mTvPaidAmount.setText("Paid amount: "+m1.getPaid());
            ((CustomViewHolder) holder).text_date.setText(m1.getCreatedDate() +" "+m1.getCreatedTime());
        }
    }


    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView text_invoice_no, text_total_amount, mTvPaidAmount, text_date;
        CardView card_view;
        ImageView mIvDownload;

        public CustomViewHolder(View view) {
            super(view);

            this.text_invoice_no = view.findViewById(R.id.text_invoice_no);
            this.text_date = view.findViewById(R.id.text_date);
            this.mTvPaidAmount = view.findViewById(R.id.mTvPaidAmount);
            this.mIvDownload = view.findViewById(R.id.mIvDownload);
            this.text_total_amount = view.findViewById(R.id.text_total_amount);
            this.card_view = view.findViewById(R.id.card_view);
        }
    }

    public void setFilter(ArrayList<ModelFeesHistory> detailnew) {
        detail = new ArrayList<>();
        detail.addAll(detailnew);
        notifyDataSetChanged();
    }

    public ModelFeesHistory getFilter(int i) {

        return detail.get(i);
    }


}

