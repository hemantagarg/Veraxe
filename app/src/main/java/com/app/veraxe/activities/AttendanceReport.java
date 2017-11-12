package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterAttendanceReportList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AttendanceReport extends AppCompatActivity implements OnCustomItemClicListener, DatePickerDialog.OnDateSetListener, ApiResponse {


    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private RecyclerView mRecyclerView;
    private ModelStudent itemList;
    private AdapterAttendanceReportList adapterAttendanceList;
    private ArrayList<ModelStudent> arrayList;
    private Button button_search;
    private Spinner spinner_class, spinner_section;
    private TextView text_select_date;
    private ConnectionDetector cd;
    private ArrayAdapter<String> adapter_class, adapter_secton;
    private LinearLayoutManager layoutManager;
    private ArrayList<String> class_list;
    private ArrayList<String> class_listId;
    private ArrayList<String> section_list;
    private ArrayList<String> section_listId;
    TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
        context = this;
        init();
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Attendance");

        setListener();
        classList();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        text_select_date.setText(dateFormat.format(date));

        section_list.add("Select Section");
        section_listId.add("-1");
        adapter_secton = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, section_list);
        spinner_section.setAdapter(adapter_secton);

    }


    public void classList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("userid", AppUtils.getUserId(context));
            hm.put("userrole", AppUtils.getUserRole(context));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.selectlist_class);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void sectionList(String classid) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", Constant.AUTHKEY);
            hm.put("classid", classid);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userid", AppUtils.getUserId(context));
            hm.put("userrole", AppUtils.getUserRole(context));


            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.selectlist_section);
            new CommonAsyncTaskHashmap(3, context, this).getquery(url, hm);

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
        class_listId = new ArrayList<>();
        class_list = new ArrayList<>();
        section_list = new ArrayList<>();
        section_listId = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        button_search = (Button) findViewById(R.id.button_search);
        spinner_class = (Spinner) findViewById(R.id.spinner_class);
        spinner_section = (Spinner) findViewById(R.id.spinner_section);
        text_select_date = (TextView) findViewById(R.id.text_select_date);
        text_result = (TextView) findViewById(R.id.text_result);

    }


    private void getstudentList() {

        //   date = 2016 - 10 - 30 (date format must be YYYY -MM - DD)

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", Constant.AUTHKEY);
            hm.put("classid", class_listId.get(spinner_class.getSelectedItemPosition()));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("sectionid", section_listId.get(spinner_section.getSelectedItemPosition()));
            hm.put("date", text_select_date.getText().toString());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.attendancereport);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinner_class.getSelectedItemPosition() != 0) {
                    sectionList(class_listId.get(spinner_class.getSelectedItemPosition()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        text_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();

                DatePickerDialog dpd = DatePickerDialog.newInstance(AttendanceReport.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //   dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_class.getSelectedItemPosition() != 0) {
                    getstudentList();
                } else {
                    Toast.makeText(context, R.string.select_section, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        itemList.setName(jo.getString("student_name"));
                        itemList.setStudent_id(jo.getString("student_id"));
                        itemList.setAttn_status(jo.getString("attn_status"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setAttn_date(jo.getString("attn_date"));
                        itemList.setAttendance_name(jo.getString("attendance_name"));
                        itemList.setRowType(1);
                        arrayList.add(itemList);
                    }
                    adapterAttendanceList = new AdapterAttendanceReportList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterAttendanceList);
                    if (arrayList.size() > 0) {
                        text_result.setVisibility(View.VISIBLE);

                    } else {
                        text_result.setVisibility(View.GONE);

                    }

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("result");
                    class_list.add("Select Class");
                    class_listId.add("-1");


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        class_list.add(jo.getString("class_name"));
                        class_listId.add(jo.getString("id"));

                    }
                    adapter_class = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, class_list);
                    spinner_class.setAdapter(adapter_class);

                }
            } else if (method == 3) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    section_list.clear();
                    section_listId.clear();
                    JSONArray array = response.getJSONArray("section");

                    section_list.add("Select Section");
                    section_listId.add("-1");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        section_list.add(jo.getString("section_name"));
                        section_listId.add(jo.getString("id"));

                    }

                    adapter_secton = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, section_list);
                    spinner_section.setAdapter(adapter_secton);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {
            Intent in = new Intent(context, StudentAttendanceReport.class);
            in.putExtra("studentid", arrayList.get(position).getStudent_id());
            startActivity(in);

        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        // text_select_date.setText(year + "-" + month + "-" + dayOfMonth);
        String date = dayOfMonth + "-" + month + "-" + year;
        text_select_date.setText(year + "-" + month + "-" + dayOfMonth);
    }
}
