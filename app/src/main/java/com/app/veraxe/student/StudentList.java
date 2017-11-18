package com.app.veraxe.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterStudentList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class StudentList extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterStudentList adapterStudentList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentlist);

        context = this;
        init();
        mAdView = (AdView) findViewById(R.id.adView);

        if (AppUtils.getAdd_status(context).equalsIgnoreCase("1")) {
            mAdView.setVisibility(View.VISIBLE);
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-5990787515520459~9332653723");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Students");
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        setListener();
        //studentList();
        getStudentList();

    }

    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

    }


    public void getStudentList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("mobilenumber", AppUtils.getStudentMobile(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.getstudentlist);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void getStudentListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("mobilenumber", AppUtils.getStudentMobile(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.getstudentlist);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

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
        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));

    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getStudentListRefresh();
            }
        });


    }


    public void studentList() {
        try {
            String data = AppUtils.getStudentList(context);
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {

                JSONObject jo = array.getJSONObject(i);
                itemList = new ModelStudent();

                itemList.setStudent_id(jo.getString("student_id"));
                itemList.setName(jo.getString("name"));
                itemList.setSchool_id(jo.getString("school_id"));
                itemList.setRowType(1);
                itemList.setAvtar(jo.getString("avtar"));
                itemList.setGender(jo.getString("gender"));
                itemList.setAdd_status(jo.getString("add_status"));

                arrayList.add(itemList);

            }
            adapterStudentList = new AdapterStudentList(context, this, arrayList);
            mRecyclerView.setAdapter(adapterStudentList);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {

            AppUtils.setUserId(context, arrayList.get(position).getStudent_id());
            AppUtils.setUserImage(context, arrayList.get(position).getAvtar());
            AppUtils.setUserName(context, arrayList.get(position).getName());
            AppUtils.setStudentId(context, arrayList.get(position).getStudent_id());
            AppUtils.setSchoolId(context, arrayList.get(position).getSchool_id());
            Intent intent = new Intent(context, StudentDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("result");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelStudent();

                        itemList.setStudent_id(jo.getString("student_id"));
                        itemList.setName(jo.getString("name"));
                        itemList.setSchool_id(jo.getString("school_id"));
                        itemList.setRowType(1);
                        itemList.setAvtar(jo.getString("avtar"));
                        itemList.setGender(jo.getString("gender"));
                        itemList.setLogo(jo.getString("logo"));
                        itemList.setAdd_status(jo.getString("add_status"));

                        arrayList.add(itemList);

                    }
                    adapterStudentList = new AdapterStudentList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterStudentList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
