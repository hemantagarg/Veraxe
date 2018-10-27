package com.app.veraxe.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.GPSTracker;
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

public class SelfAttendance extends AppCompatActivity implements OnDateSelectedListener, ApiResponse, OnCustomItemClicListener {


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
    LinearLayoutManager layoutManager;
    TextView text_total_present, text_total_absent, text_total_leave, text_total_holiay, text_punchOut, text_punchIn;
    private double mLat, mLong;
    private GPSTracker gTraker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_attendance);
        context = this;
        init();
        arrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Self Attendance");
        setListener();
        attendanceList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        gTraker = new GPSTracker(context);

        if (gTraker.canGetLocation()) {

            mLat = gTraker.getLatitude();
            mLong = gTraker.getLongitude();
            Log.e("mLat", "" + mLat);
            Log.e("mLong", "" + mLong);

        }
    }

    public void showSettingsAlert() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //  widget.setFirstDayOfWeek(Calendar.MONDAY);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        text_total_absent = (TextView) findViewById(R.id.text_total_absent);
        text_total_present = (TextView) findViewById(R.id.text_total_present);
        text_total_holiay = (TextView) findViewById(R.id.text_total_holiday);
        text_punchIn = (TextView) findViewById(R.id.text_punchIn);
        text_punchOut = (TextView) findViewById(R.id.text_punchOut);
        text_total_leave = (TextView) findViewById(R.id.text_total_leave);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
    }


    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_punchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gTraker.canGetLocation()) {

                    mLat = gTraker.getLatitude();
                    mLong = gTraker.getLongitude();
                    punchInPunchOut("IN");
                    Log.e("mLat", "" + mLat);
                    Log.e("mLong", "" + mLong);

                } else {
                    showSettingsAlert();
                }
            }
        });
        text_punchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gTraker.canGetLocation()) {

                    mLat = gTraker.getLatitude();
                    mLong = gTraker.getLongitude();
                    punchInPunchOut("OUT");
                    Log.e("mLat", "" + mLat);
                    Log.e("mLong", "" + mLong);

                } else {
                    showSettingsAlert();
                }

            }
        });
    }

    public void attendanceList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("userid", AppUtils.getUserId(context));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            //  hm.put("schoolid", AppUtils.getSchoolId(context));
            // hm.put("userid", AppUtils.getUserId(context));


            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.attendance_self);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void punchInPunchOut(String attendance_type) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
         /*   authkey=23de92fe7f8f6babd6fa31beacd81798&
                    school_id=3
            teacher_id=25
            lat=28.406757
            lng=77.042138
            attendance_type=IN  (IN/OUT)*/

            hm.put("teacher_id", AppUtils.getUserId(context));
            hm.put("school_id", AppUtils.getSchoolId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("lng", mLong);
            hm.put("lat", mLat);
            hm.put("attendance_type", attendance_type);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.teacherGeoAttendance);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

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

                    text_total_absent.setText(response.getString("absent_total"));
                    text_total_present.setText(response.getString("present_total"));
                    text_total_leave.setText(response.getString("leave_total"));
                    text_total_holiay.setText(response.getString("holiday_total"));

                    JSONArray array = response.getJSONArray("result");

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
                    //  mRecyclerView.setAdapter(adapterSelfAtendanceList);
                    if (arrayList.size() > 0) {

                        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
                    }
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
