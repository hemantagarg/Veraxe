package com.app.veraxe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;

import java.util.ArrayList;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterAttendanceReportList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ModelStudent> detail;
    Context mContext;
    OnCustomItemClicListener listener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterAttendanceReportList(Context context, OnCustomItemClicListener lis, ArrayList<ModelStudent> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_attendance_reportlist, parent, false);

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

            ((CustomViewHolder) holder).text_name.setText(m1.getName());
            ((CustomViewHolder) holder).text_attn_status.setText(m1.getAttendance_name());
            //  ((CustomViewHolder) holder).text_rollno.setText(m1.getRollno());

            if (m1.getAttn_status().equalsIgnoreCase("2")) {
                ((CustomViewHolder) holder).text_attn_status.setTextColor(mContext.getResources().getColor(R.color.red_color));
            } else if (m1.getAttn_status().equalsIgnoreCase("1")) {
                ((CustomViewHolder) holder).text_attn_status.setTextColor(mContext.getResources().getColor(R.color.green_color));
            } else if (m1.getAttn_status().equalsIgnoreCase("3")) {
                ((CustomViewHolder) holder).text_attn_status.setTextColor(mContext.getResources().getColor(R.color.yellow_color));
            } else if (m1.getAttn_status().equalsIgnoreCase("4")) {
                ((CustomViewHolder) holder).text_attn_status.setTextColor(mContext.getResources().getColor(R.color.blue_color));
            }

            ((CustomViewHolder) holder).card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 1);
                }
            });
            ((CustomViewHolder) holder).radio_leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 4);
                }
            });
            ((CustomViewHolder) holder).radio_present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 2);
                }
            });
            ((CustomViewHolder) holder).radio_absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 3);
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }


    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView text_name, text_rollno, text_attn_status;
        CardView card_view;
        RadioButton radio_present, radio_absent, radio_leave;

        public CustomViewHolder(View view) {
            super(view);

            this.text_name = (TextView) view.findViewById(R.id.text_name);
            this.text_attn_status = (TextView) view.findViewById(R.id.text_attn_status);
            this.text_rollno = (TextView) view.findViewById(R.id.text_rollno);
            this.radio_present = (RadioButton) view.findViewById(R.id.radio_present);
            this.radio_leave = (RadioButton) view.findViewById(R.id.radio_leave);
            this.radio_absent = (RadioButton) view.findViewById(R.id.radio_absent);
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

