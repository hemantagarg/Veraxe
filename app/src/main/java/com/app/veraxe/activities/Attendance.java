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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterAttendanceList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Attendance extends AppCompatActivity implements OnCustomItemClicListener, DatePickerDialog.OnDateSetListener, ApiResponse {

    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private RecyclerView mRecyclerView;
    private ModelStudent itemList;
    private AdapterAttendanceList adapterAttendanceList;
    private ArrayList<ModelStudent> arrayList;
    private Button button_search;
    private Spinner spinner_class, spinner_section;
    private TextView text_select_date;
    private ConnectionDetector cd;
    private ArrayAdapter<String> adapter_class, adapter_secton;
    private LinearLayoutManager layoutManager;
    private TextView text_save, text_attstatus;
    private TextView text_result;
    private LinearLayout linear_messageto, linear_sendmessage;
    private ArrayList<String> class_list;
    private ArrayList<String> class_listId;
    private ArrayList<String> section_list;
    private ArrayList<String> section_listId;
    private CheckBox check_all, check_abset_leave, check_father, check_mother, check_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
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
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.selectlist_class);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void sectionList(String classid) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", AppConstants.AUTHKEY);
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
        class_list = new ArrayList<>();
        class_listId = new ArrayList<>();
        section_list = new ArrayList<>();
        section_listId = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        button_search = (Button) findViewById(R.id.button_search);
        spinner_class = (Spinner) findViewById(R.id.spinner_class);
        spinner_section = (Spinner) findViewById(R.id.spinner_section);
        text_select_date = (TextView) findViewById(R.id.text_select_date);
        text_attstatus = (TextView) findViewById(R.id.text_attstatus);
        text_save = (TextView) findViewById(R.id.text_save);
        linear_sendmessage = (LinearLayout) findViewById(R.id.linear_sendmessage);
        linear_messageto = (LinearLayout) findViewById(R.id.linear_messageto);
        check_all = (CheckBox) findViewById(R.id.check_all);
        check_abset_leave = (CheckBox) findViewById(R.id.check_abset_leave);
        check_father = (CheckBox) findViewById(R.id.check_father);
        check_mother = (CheckBox) findViewById(R.id.check_mother);
        check_notification = (CheckBox) findViewById(R.id.check_notification);
        text_result = (TextView) findViewById(R.id.text_result);

    }

    private void getstudentList() {

        //   date = 2016 - 10 - 30 (date format must be YYYY -MM - DD)

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("classid", class_listId.get(spinner_class.getSelectedItemPosition()));
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("sectionid", section_listId.get(spinner_section.getSelectedItemPosition()));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("date", text_select_date.getText().toString());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.attendancestatus);
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

        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveAttendance();
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

                DatePickerDialog dpd = DatePickerDialog.newInstance(Attendance.this,
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

                getstudentList();
            }
        });

        check_abset_leave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    if (check_all.isChecked()) {
                        check_all.setChecked(false);
                    }
                }

            }
        });
        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (check_abset_leave.isChecked()) {
                        check_abset_leave.setChecked(false);
                    }
                }

            }
        });


    }

    private void saveAttendance() {

     /*   Service Url:http:
// manage.veraxe.com/api/markattendance

        Parameters
                authkey = 23d e92fe7f8f6babd6fa31beacd81798
                schoolid = 6
        date = 2016 - 11 - 28 (Attendace Date - YYYY - MM - DD)
        data[0][id] =
                data[0][student_class_id] = 17
        data[0][attn_status] = 1
        data[0][remark] =
                data[1][id] =
                        data[1][student_class_id] = 16
        data[1][attn_status] = 3
        data[1][remark] = leave remark

                sendsms = 2 (1 = ALL | 2 = Absent & Leave)
        sendto_father = 1 (1 = Yes | 2 = No)
        sendto_mother = 0 (1 = Yes | 2 = No)
*/
        String sendsms = "0";
        if (check_all.isChecked()) {
            sendsms = "1";
        } else {
            if (check_abset_leave.isChecked()) {
                sendsms = "2";
            }
        }

        String sendnotification = "0";
        if (check_notification.isChecked()) {
            sendnotification = "1";
        } else {
            sendnotification = "0";
        }

        String sendto_father = "0";
        if (check_father.isChecked()) {
            sendto_father = "1";
        } else {
            sendto_father = "2";
        }
        String sendto_mother = "0";
        if (check_mother.isChecked()) {
            sendto_mother = "1";
        } else {
            sendto_mother = "2";
        }

        JSONArray main = new JSONArray();
        try {

            for (int i = 0; i < arrayList.size(); i++) {

                JSONObject jo = new JSONObject();
                jo.put("attn_status", arrayList.get(i).getAttn_status());
                jo.put("remark", arrayList.get(i).getRemark());
                jo.put("id", arrayList.get(i).getId());
                jo.put("student_class_id", arrayList.get(i).getStudent_class_id());
                main.put(jo);
            }
            Log.e("jsonArray", "**" + main);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("date", text_select_date.getText().toString());
            hm.put("data", main.toString());
            hm.put("sendsms", sendsms);
            hm.put("sendto_father", sendto_father);
            hm.put("sendto_mother", sendto_mother);
            hm.put("sendnotification", sendnotification);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.markattendance);
            new CommonAsyncTaskHashmap(6, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
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

                        itemList.setName(jo.getString("student_name"));
                        itemList.setId(jo.getString("id"));
                        itemList.setStudent_id(jo.getString("student_id"));
                        itemList.setStudent_class_id(jo.getString("student_class_id"));
                        itemList.setAttn_status(jo.getString("attn_status"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setRollno(jo.getString("roll_no"));
                        itemList.setRowType(1);
                        arrayList.add(itemList);
                    }
                    adapterAttendanceList = new AdapterAttendanceList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterAttendanceList);


                    if (arrayList.size() > 0) {

                        if (response.getString("attendencstatus").equalsIgnoreCase("1")) {
                            text_attstatus.setText(R.string.att_marked);
                        } else {
                            text_attstatus.setText(R.string.att_not_marked);

                        }
                        text_result.setVisibility(View.VISIBLE);
                        text_attstatus.setVisibility(View.VISIBLE);
                        text_save.setVisibility(View.VISIBLE);
                        linear_messageto.setVisibility(View.GONE);
                        check_notification.setVisibility(View.VISIBLE);
                        linear_sendmessage.setVisibility(View.GONE);

                    } else {

                        text_attstatus.setText("Student not" +
                                " registred in this class and section.");
                        text_attstatus.setVisibility(View.VISIBLE);
                        text_result.setVisibility(View.GONE);
                        text_save.setVisibility(View.GONE);
                        linear_messageto.setVisibility(View.GONE);
                        check_notification.setVisibility(View.GONE);
                        linear_sendmessage.setVisibility(View.GONE);

                    }
                } else {

                    arrayList.clear();

                    if (adapterAttendanceList != null) {
                        adapterAttendanceList.notifyDataSetChanged();
                    }
                    text_result.setVisibility(View.GONE);
                    text_save.setVisibility(View.GONE);
                    linear_messageto.setVisibility(View.GONE);
                    check_notification.setVisibility(View.GONE);
                    linear_sendmessage.setVisibility(View.GONE);
                    text_attstatus.setText(response.getString("msg"));
                    text_attstatus.setVisibility(View.VISIBLE);
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
            } else if (method == 6) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClickListener(int position, int flag) {
                  /* attn_status = 1 (Persent,
                    attn_status = 2 (Absent),
                    attn_status = 3 (Leave)*/

        if (flag == 2) {
            //present
            arrayList.get(position).setAttn_status("1");
            adapterAttendanceList.notifyDataSetChanged();

        } else if (flag == 3) {
            //absent
            arrayList.get(position).setAttn_status("2");
            adapterAttendanceList.notifyDataSetChanged();

        } else if (flag == 4) {
            //leave
            arrayList.get(position).setAttn_status("3");
            adapterAttendanceList.notifyDataSetChanged();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String date = year + "-" + month + "-" + dayOfMonth;
        text_select_date.setText(year + "-" + month + "-" + dayOfMonth);
        //  text_select_date.setText(AppUtils.getDateFromDateString(date));
    }
}
