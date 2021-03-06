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
public class AdapterEventtList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ModelStudent> detail;
    Context mContext;
    OnCustomItemClicListener listener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    int[] color = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n, R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v, R.drawable.w, R.drawable.x, R.drawable.y, R.drawable.z};

    public AdapterEventtList(Context context, OnCustomItemClicListener lis, ArrayList<ModelStudent> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_eventlist, parent, false);

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

            ((CustomViewHolder) holder).text_name.setText(m1.getTitle());
            ((CustomViewHolder) holder).text_date.setText(m1.getDatetime());
            ((CustomViewHolder) holder).text_desc.setText(m1.getDescription());
           /* ((CustomViewHolder) holder).text_day.setText(m1.getDay());
            ((CustomViewHolder) holder).text_month.setText(m1.getMonth());
            ((CustomViewHolder) holder).text_year.setText(m1.getYear());*/

            try {
                if (detail.get(position).getTitle().length() > 0) {
                    char c = detail.get(position).getTitle().toUpperCase().charAt(0);
                    int pos = (int) c;
                    pos = pos % 65;
                    ((CustomViewHolder) holder).circle_image.setImageResource(color[pos]);
                    ((CustomViewHolder) holder).text_name_title.setText(detail.get(position).getTitle().toUpperCase().charAt(0) + "");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((CustomViewHolder) holder).card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 1);
                }
            });
            ((CustomViewHolder) holder).image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position, 2);
                }
            });
            ((CustomViewHolder) holder).image_edit.setOnClickListener(new View.OnClickListener() {
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

        TextView text_name, text_desc, text_date, text_name_title, text_day, text_month, text_year;
        ImageView image_delete, image_edit;
        CardView card_view;
        ImageView circle_image;

        public CustomViewHolder(View view) {
            super(view);

            this.text_name = (TextView) view.findViewById(R.id.text_name);
            this.text_date = (TextView) view.findViewById(R.id.text_date);
            this.text_desc = (TextView) view.findViewById(R.id.text_desc);
            this.text_day = (TextView) view.findViewById(R.id.text_day);
            this.text_month = (TextView) view.findViewById(R.id.text_month);
            this.text_year = (TextView) view.findViewById(R.id.text_year);
            this.text_name_title = (TextView) view.findViewById(R.id.text_name_title);
            this.card_view = (CardView) view.findViewById(R.id.card_view);
            this.circle_image = (ImageView) view.findViewById(R.id.circle_image);
            this.image_delete = (ImageView) view.findViewById(R.id.image_delete);
            this.image_edit = (ImageView) view.findViewById(R.id.image_edit);
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

