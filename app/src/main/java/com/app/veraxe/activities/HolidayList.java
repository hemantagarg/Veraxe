package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterHolidayList;
import com.app.veraxe.adapter.AdapterLeaveList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
    public class HolidayList extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterHolidayList adapterLeaveList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private Button btn_need_leave;
    private Spinner spinner_leave;
    FloatingActionButton btn_addevent;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;
    private int deletePosition;
    private TextView mTvFromDate, mTvToDate, mTvtype_of_leave;
    ArrayAdapter<String> adapterLeaveTypes;
    ArrayList<String> leaveList = new ArrayList<>();
    ArrayList<String> leaveListId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holidaylist);

        context = this;
        init();
        setListener();
        HolidayList();
    }

    private void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();

        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        btn_need_leave = (Button) findViewById(R.id.btn_need_leave);
        mRecyclerView.setLayoutManager(layoutManager);
        btn_addevent = (FloatingActionButton) findViewById(R.id.btn_addevent);
        btn_addevent.setVisibility(View.GONE);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HolidayList();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK) {

            HolidayList();
        }
    }



    public void HolidayList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));


            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.holiday_list);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClickListener(int position, int flag) {

    }






    @Override
    public void getResponse(int method, JSONObject response) {
        try{
        if (method == 1) {
            if (response.getString("response").equalsIgnoreCase("1")) {

                JSONArray array = response.getJSONArray("result");
                arrayList.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jo = array.getJSONObject(i);
                    itemList = new ModelStudent();

                    itemList.setName(jo.getString("name"));
                    itemList.setStart_date(jo.getString("start_date"));
                    itemList.setEnd_date(jo.getString("end_date"));
                    itemList.setRowType(1);

                    arrayList.add(itemList);
                }
                adapterLeaveList = new AdapterHolidayList(context, this, arrayList);
                mRecyclerView.setAdapter(adapterLeaveList);
                if (swipe_refresh != null) {
                    swipe_refresh.setRefreshing(false);
                }

            } else {

                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
            }

        }
    }catch (
                Exception e)

        {
            e.printStackTrace();
        }
}}
