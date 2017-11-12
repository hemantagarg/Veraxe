package com.app.veraxe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by admin on 26-11-2015.
 */
public class AdapterStudentDashBoard extends RecyclerView.Adapter<AdapterStudentDashBoard.CustomViewHolder> {
    ArrayList<ModelStudent> detail;
    Context mContext;
    OnCustomItemClicListener listener;
    private int lastPosition = -1;

    public AdapterStudentDashBoard(Context context, OnCustomItemClicListener lis, ArrayList<ModelStudent> list) {

        this.detail = list;
        this.mContext = context;
        this.listener = lis;

    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_dashboard_service, viewGroup, false);

        final CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;

    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {


        customViewHolder.service_name.setText(detail.get(i).getTitle());

        Picasso.with(mContext)
                .load(detail.get(i).getImage())
                // .transform(new CircleTransform())
                .into(customViewHolder.image);

        Picasso.with(mContext)
                .load(detail.get(i).getIcon())
                // .transform(new CircleTransform())
                .into(customViewHolder.image_icon);
        customViewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClickListener(i, 1);
            }
        });


    }

    @Override
    public int getItemCount() {
        return detail.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView service_name;
        ImageView image, image_icon;
        CardView card_view;


        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.service_name = (TextView) view.findViewById(R.id.text_services_name);
            this.image = (ImageView) view.findViewById(R.id.image_background);
            this.image_icon = (ImageView) view.findViewById(R.id.image_icon);
            this.card_view = (CardView) view.findViewById(R.id.card_view);

        }


        @Override
        public void onClick(View v) {

            listener.onItemClickListener(getPosition(), 1);
        }

    }


}