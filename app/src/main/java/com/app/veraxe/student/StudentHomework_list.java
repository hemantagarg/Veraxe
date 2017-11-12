package com.app.veraxe.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterStudentHomeworkList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class StudentHomework_list extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse, OnDateSelectedListener {


    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterStudentHomeworkList adapterHomeworkList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    //  RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    //  SwipeRefreshLayout swipe_refresh;
    MaterialCalendarView widget;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homeworklist);

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
        getSupportActionBar().setTitle("Homework");
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        setListener();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentdate = dateFormat.format(date);
        homeworkList(currentdate);

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
        //   rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        //  rl_network = (RelativeLayout) findViewById(rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(gridlayoutManager);
        //  swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //  widget.setFirstDayOfWeek(Calendar.MONDAY);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());

    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    /*    swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                homeworkListRefresh();

            }
        });*/

    }


    public void homeworkList(String date) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("date", date);
            //  date = 2016 - 09 - 10
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_homework_date);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }


    }

    public void homeworkListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_homework);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {
            Intent intent = new Intent(context, StudentHomeworkDetail.class);
            intent.putExtra("homeworkId", arrayList.get(position).getId());
            startActivity(intent);

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

                        itemList.setId(jo.getString("id"));
                        itemList.setSubject_id(jo.getString("subject_id"));
                        itemList.setText(jo.getString("text"));
                        itemList.setRowType(1);
                        itemList.setDate_start(jo.getString("date_start"));
                        itemList.setDate_end(jo.getString("date_end"));
                        itemList.setSubject_name(jo.getString("subject_name"));

                        arrayList.add(itemList);

                    }
                    adapterHomeworkList = new AdapterStudentHomeworkList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterHomeworkList);
                  /*  if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }*/
                } else {
                    arrayList.clear();
                    if (adapterHomeworkList != null) {
                        adapterHomeworkList.notifyDataSetChanged();
                    }
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Log.e("date", "**" + date.getDay() + date.getMonth() + date.getYear());
        int month = date.getMonth() + 1;
        Date d = date.getDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date();
        String currentdate = dateFormat.format(d);

        String selectedDate = date.getYear() + "-" + month + "-" + date.getDay();
        homeworkList(currentdate);


    }
}
