package com.app.veraxe.student;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

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

public class StudentSelfAttendance extends AppCompatActivity implements OnDateSelectedListener, ApiResponse, OnCustomItemClicListener {

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
    ArrayList<CalendarDay> leaveDates;
    ArrayList<CalendarDay> holidayDates;
    TextView text_total_present, text_total_absent, text_total_holiday, text_total_leave;
    LinearLayoutManager layoutManager;
    private AdView mAdView;
    RelativeLayout rl_status;
    TextView text_status;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    DateFormat YearMonthdateFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
    ImageView image_cross;
    ArrayList<String> listSessionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_self_attendance);
        context = this;
        init();
        mAdView = (AdView) findViewById(R.id.adView);
        Log.e("adstatus", AppUtils.getAdd_status(context));
        if (AppUtils.getAdd_status(context).equalsIgnoreCase("1")) {
            mAdView.setVisibility(View.VISIBLE);
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-5990787515520459~9332653723");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
        arrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Self Attendance");
        setListener();
        Calendar c = Calendar.getInstance();
        String date = YearMonthdateFormat.format(c.getTime());
        attendanceList(date);

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
        listSessionDate = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //  widget.setFirstDayOfWeek(Calendar.MONDAY);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        text_total_present = (TextView) findViewById(R.id.text_total_present);
        text_total_absent = (TextView) findViewById(R.id.text_total_absent);
        text_total_holiday = (TextView) findViewById(R.id.text_total_holiday);
        text_total_leave = (TextView) findViewById(R.id.text_total_leave);
        rl_status = (RelativeLayout) findViewById(R.id.rl_status);
        text_status = (TextView) findViewById(R.id.text_status);
        image_cross = (ImageView) findViewById(R.id.image_cross);

    }


    public void setListener() {

        widget.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String date1 = YearMonthdateFormat.format(date.getDate());
                attendanceList(date1);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        image_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rl_status.setVisibility(View.GONE);
            }
        });

    }

    public void attendanceList(String yearMonth) {
        rl_status.setVisibility(View.GONE);
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("yearMonth", yearMonth);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_attendance);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }


        return dateFormat.format(date.getDate());
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        try {
            Log.e("CalendarDay", "*" + getSelectedDatesString());
            String currentDate = getSelectedDatesString();
            if (listSessionDate.contains(currentDate)) {
                rl_status.setVisibility(View.VISIBLE);

                int itemPos = listSessionDate.indexOf(currentDate);

                if (arrayList.get(itemPos).getAttn_status().equalsIgnoreCase("1")) {
                    text_status.setBackgroundColor(getResources().getColor(R.color.green_color));
                    text_status.setText(arrayList.get(itemPos).getAttendance_name());
                } else if (arrayList.get(itemPos).getAttn_status().equalsIgnoreCase("2")) {
                    text_status.setBackgroundColor(getResources().getColor(R.color.red_color));
                    text_status.setText(arrayList.get(itemPos).getAttendance_name());
                } else if (arrayList.get(itemPos).getAttn_status().equalsIgnoreCase("3")) {
                    text_status.setBackgroundColor(getResources().getColor(R.color.yellow_color));
                    text_status.setText(arrayList.get(itemPos).getAttendance_name());
                } else if (arrayList.get(itemPos).getAttn_status().equalsIgnoreCase("4")) {
                    text_status.setBackgroundColor(getResources().getColor(R.color.blue_color));
                    text_status.setText(arrayList.get(itemPos).getAttendance_name());
                }
            } else {
                rl_status.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("result");
                    text_total_absent.setText(response.getString("absent_total"));
                    text_total_present.setText(response.getString("present_total"));
                    text_total_leave.setText(response.getString("leave_total"));
                    text_total_holiday.setText(response.getString("holiday_total"));

                   /* "present_total": 3,
                            "absent_total": 1,
                            "leave_total": 1,
                            "holiday_total": 0*/
                    arrayList.clear();
                    listSessionDate.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelStudent();

                        itemList.setAttn_status(jo.getString("attn_status"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setAttn_date(jo.getString("attn_date"));
                        listSessionDate.add(jo.getString("attn_date"));
                        itemList.setRowType(1);
                        itemList.setAttendance_name(jo.getString("attendance_name"));

                        arrayList.add(itemList);
                    }
                    adapterSelfAtendanceList = new AdapterSelfAtendanceList(context, this, arrayList);
                    //  mRecyclerView.setAdapter(adapterSelfAtendanceList);
                    if (arrayList.size() > 0) {
                        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
                    }
                } else {
                    text_total_absent.setText("0");
                    text_total_present.setText("0");
                    text_total_leave.setText("0");
                    text_total_holiday.setText("0");

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
    public void onItemClickListener(int position, int flag) {

    }
}
