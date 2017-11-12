package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterSelfAtendanceList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.decorators.EventDecorator;
import com.app.veraxe.interfaces.ApiResponse;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class StudentAttendanceReport extends AppCompatActivity implements OnDateSelectedListener, ApiResponse, OnCustomItemClicListener {


    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    Context context;
    MaterialCalendarView widget;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterSelfAtendanceList adapterSelfAtendanceList;
    ArrayList<ModelStudent> arrayList;
    ArrayList<CalendarDay> presentDates;
    ArrayList<CalendarDay> absentDates;
    TextView text_total_present, text_total_absent, text_total_leave, text_total_holiay;
    ArrayList<CalendarDay> leaveDates;
    ArrayList<CalendarDay> holidayDates;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendancereport);
        context = this;
        init();
        arrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Student Attendance");
        setListener();
        attendanceList();

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
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //  widget.setFirstDayOfWeek(Calendar.MONDAY);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        text_total_absent = (TextView) findViewById(R.id.text_total_absent);
        text_total_present = (TextView) findViewById(R.id.text_total_present);
        text_total_holiay = (TextView) findViewById(R.id.text_total_holiday);
        text_total_leave = (TextView) findViewById(R.id.text_total_leave);
    }


    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    public void attendanceList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("studentid", getIntent().getStringExtra("studentid"));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("authkey", Constant.AUTHKEY);
            //  hm.put("schoolid", AppUtils.getSchoolId(context));
            // hm.put("userid", AppUtils.getUserId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.attendancereport);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("result");

                    text_total_absent.setText(response.getString("absent_total"));
                    text_total_holiay.setText(response.getString("holiday_total"));
                    text_total_present.setText(response.getString("present_total"));
                    text_total_leave.setText(response.getString("leave_total"));

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelStudent();

                        itemList.setAttn_status(jo.getString("attn_status"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setAttn_date(jo.getString("attn_date"));
                        itemList.setRowType(1);
                        itemList.setAttendance_name(jo.getString("attendance_name"));

                        arrayList.add(itemList);

                    }
                    adapterSelfAtendanceList = new AdapterSelfAtendanceList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterSelfAtendanceList);
                    if (arrayList.size() > 0) {

                        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
                    }
                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            Calendar calendar = Calendar.getInstance();
            presentDates = new ArrayList<>();
            leaveDates = new ArrayList<>();
            absentDates = new ArrayList<>();
            holidayDates = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {

                CalendarDay day = CalendarDay.from(fromDateToCalendar(arrayList.get(i).getAttn_date()));
                if (arrayList.get(i).getAttn_status().equalsIgnoreCase("1")) {
                    presentDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("2")) {
                    absentDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("3")) {
                    leaveDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("4")) {
                    holidayDates.add(day);
                }

            }

            return presentDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.green_color), presentDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.blue_color), holidayDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.yellow_color), leaveDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.red_color), absentDates));



        }
    }

    private Calendar fromDateToCalendar(String date) {


        Calendar cal = Calendar.getInstance();

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date1 = format.parse(date);
            cal.setTime(date1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }


    @Override
    public void onItemClickListener(int position, int flag) {

    }
}
