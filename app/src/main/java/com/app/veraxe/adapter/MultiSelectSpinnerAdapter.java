package com.app.veraxe.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.app.veraxe.R;
import com.app.veraxe.interfaces.OnCustomItemClicListener;

import java.util.ArrayList;

/*****
 * Adapter class extends with ArrayAdapter
 ******/
public class MultiSelectSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList data;
    public Resources res;
    LayoutInflater inflater;
    OnCustomItemClicListener listener;
    ArrayList<Integer> selection;

    public MultiSelectSpinnerAdapter(Context mContext, OnCustomItemClicListener lis, ArrayList objects, ArrayList<Integer> selection) {
        super(mContext, 0, objects);

        this.mContext = mContext;
        this.listener = lis;
        this.selection = selection;
        data = objects;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count;
        //return count;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.row_multiselect_spinner, parent, false);
        String string = (String) data.get(position);
        TextView textView = (TextView) row.findViewById(R.id.text_item);
        textView.setText(string);
        CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
        checkbox.setText(string);

        if (selection.get(position) == 0) {
            checkbox.setChecked(false);
        } else {
            checkbox.setChecked(true);
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onItemClickListener(position, 1);
            }
        });

        return row;
    }
}
