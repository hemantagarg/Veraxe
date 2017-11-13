package com.app.veraxe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;

import java.util.ArrayList;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterLibraryList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ModelStudent> detail;
    Context mContext;
    OnCustomItemClicListener listener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    int[] color = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n, R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v, R.drawable.w, R.drawable.x, R.drawable.y, R.drawable.z};

    public AdapterLibraryList(Context context, OnCustomItemClicListener lis, ArrayList<ModelStudent> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_library_list, parent, false);

            vh = new CustomViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            this.progressBar.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CustomViewHolder) {

            ModelStudent m1 = (ModelStudent) detail.get(position);

            ((CustomViewHolder) holder).text_dapartment_label.setText(m1.getDate_start() + " - " + m1.getDate_end());
            ((CustomViewHolder) holder).text_feedbak_type.setText(m1.getLeave_type_name());
            ((CustomViewHolder) holder).text_date.setText(m1.getDatetime());
            if (m1.getStudent_remark().equalsIgnoreCase("")) {
                ((CustomViewHolder) holder).text_message.setText("Teacher Remark : " + m1.getTeacher_remark());
            } else {
                ((CustomViewHolder) holder).text_message.setText("Self Remark : " + m1.getStudent_remark());
            }

            ((CustomViewHolder) holder).image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(position,2);
                }
            });
            ((CustomViewHolder) holder).text_status.setText(m1.getStatus_name());

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }


    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView text_dapartment_label, text_feedbak_type, text_status, text_date, text_message;
        CardView card_view;
        ImageView image_delete;

        public CustomViewHolder(View view) {
            super(view);

            this.text_dapartment_label = (TextView) view.findViewById(R.id.text_dapartment_label);
            this.text_date = (TextView) view.findViewById(R.id.text_date);
            this.text_status = (TextView) view.findViewById(R.id.text_status);
            this.text_message = (TextView) view.findViewById(R.id.text_message);
            this.text_feedbak_type = (TextView) view.findViewById(R.id.text_feedbak_type);
            this.image_delete = (ImageView) view.findViewById(R.id.image_delete);
            this.card_view = (CardView) view.findViewById(R.id.card_view);
        }
    }

    public void setFilter(ArrayList<ModelStudent> detailnew) {
        detail = new ArrayList<>();
        detail.addAll(detailnew);
        notifyDataSetChanged();
    }

    public ModelStudent getFilter(int i) {

        return detail.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        ModelStudent m1 = (ModelStudent) detail.get(position);
        if (detail.get(position).getRowType() == 1) {
            return VIEW_ITEM;
        } else if (detail.get(position).getRowType() == 2) {
            return VIEW_PROG;
        }
        return -1;
    }
}

