package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterHomeworkList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;
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
public class Homework_list extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse, OnDateSelectedListener {


    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterHomeworkList adapterHomeworkList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    LinearLayout rl_main_layout;
    LinearLayoutManager layoutManager;
    FloatingActionButton btn_addhomework;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    //   SwipeRefreshLayout swipe_refresh;
    private int deletePosition;
    MaterialCalendarView widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeworklist);

        context = this;
        init();

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
        rl_main_layout = (LinearLayout) findViewById(R.id.rl_main_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridlayoutManager);
        btn_addhomework = (FloatingActionButton) findViewById(R.id.btn_addhomework);
        //      swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //  widget.setFirstDayOfWeek(Calendar.MONDAY);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Log.e("date", "**" + date.getDay() + date.getMonth() + date.getYear());
        int month = date.getMonth() + 1;
        Date d = date.getDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date();
        String currentdate = dateFormat.format(d);
        homeworkList(currentdate);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {
     /*   swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                homeworkListRefresh();

            }
        });*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        btn_addhomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(context, AddHomework.class);
                startActivityForResult(in, 21);
            }
        });

    }


    public void homeworkList(String date) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("userid", AppUtils.getUserId(context));
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userrole", AppUtils.getUserRole(context));
            hm.put("classid", "");
            hm.put("sectionid", "");
            hm.put("streamid", "");
            hm.put("subjectid", "");
            hm.put("date", date);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.homework);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    public void homeworkListRefresh(String date) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("userid", AppUtils.getUserId(context));
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userrole", AppUtils.getUserRole(context));
            hm.put("classid", "");
            hm.put("sectionid", "");
            hm.put("streamid", "");
            hm.put("subjectid", "");
            hm.put("date", date);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.homework);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String currentdate = dateFormat.format(date);
            homeworkListRefresh(currentdate);

        }
    }

    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {
            Intent intent = new Intent(context, HomeworkDetail.class);
            intent.putExtra("messageId", arrayList.get(position).getId());
            startActivity(intent);

        } else if (flag == 2) {
            deletePosition = position;
            showDeleteConfirmation(position);
        }
    }

    public void deleteHomework(int position) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("id", arrayList.get(position).getId());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.delete_homework);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation(final int position) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle("DELETE !");

        alertDialog.setMessage("Are you sure you want to Delete this Homework?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        deleteHomework(position);

                    }

                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();


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
                        itemList.setClassname(jo.getString("class"));
                        itemList.setSection(jo.getString("section"));
                        itemList.setStream_name(jo.getString("stream_name"));

                        arrayList.add(itemList);

                    }
                    adapterHomeworkList = new AdapterHomeworkList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterHomeworkList);
                   /* if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }*/
                } else {
                    arrayList.clear();
                    if (adapterHomeworkList != null) {
                        adapterHomeworkList.notifyDataSetChanged();
                    }
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    arrayList.remove(deletePosition);
                    adapterHomeworkList.notifyDataSetChanged();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
