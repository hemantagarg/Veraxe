package com.app.veraxe.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterTeacherLeaveList;
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
public class TeacherLeaveListApproval extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse, DatePickerDialog.OnDateSetListener {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterTeacherLeaveList adapterTeacherLeaveList;
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
        setContentView(R.layout.activity_teacherleavelist);

        context = this;
        init();
        setListener();
        leaveList();
        leaveTypeList();
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
                leaveListRefresh();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_need_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSpamDialog();
            }
        });
        btn_addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddEvent.class);
                startActivityForResult(intent, 21);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK) {

            leaveListRefresh();
        }
    }

    public void leaveTypeList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.list_leave_types);
            new CommonAsyncTaskHashmap(3, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void applyLeave(String remark) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("leave_type_id", leaveListId.get(spinner_leave.getSelectedItemPosition()));
            hm.put("start_date", mTvFromDate.getText().toString());
            hm.put("end_date", mTvToDate.getText().toString());
            hm.put("remark", remark);
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.send_apply_leave);
            new CommonAsyncTaskHashmap(4, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    public void leaveList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("user_id", AppUtils.getUserId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("school_id", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.teacherleave_list);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void leaveListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("user_id", AppUtils.getUserId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("school_id", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.teacherleave_list);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {
            openSpamDialog();

        }
    }
    private void openSpamDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // inflate the layout dialog_layout.xml and set it as contentView
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.leaveapproval_dialog, null, false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            final EditText mEdtComment = (EditText) view.findViewById(R.id.mEdtComment);
            Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);


            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mEdtComment.getVisibility() == View.VISIBLE) {
                        if (!mEdtComment.getText().toString().equalsIgnoreCase("")) {
                           // approveLeave(mEdtComment.getText().toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                     //   approveLeave(mEdtComment.getText().toString());
                        dialog.dismiss();
                    }

                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            Log.e("Leave List", " Exception error : " + e);
        }
    }
    public void approveLeave(int position) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("id", arrayList.get(position).getId());
            hm.put("status", arrayList.get(position).getId());
            hm.put("remark", arrayList.get(position).getId());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.approve_leave);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation(final int position) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle("DELETE !");

        alertDialog.setMessage("Are you sure you want to Delete this Leave Request?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        approveLeave(position);

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

                    JSONArray array = response.getJSONArray("data");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelStudent();

                        itemList.setId(jo.getString("id"));
                        itemList.setStudent_name(jo.getString("student_name"));
                        itemList.setSection_name(jo.getString("section_name"));
                        itemList.setStudent_id(jo.getString("student_id"));
                        itemList.setRowType(1);
                        itemList.setLeave_type_name(jo.getString("leave_type_name"));
                        itemList.setStart_date(jo.getString("start_date"));
                        itemList.setEnd_date(jo.getString("end_date"));
                        itemList.setStudent_remark(jo.getString("student_remark"));
                        itemList.setTeacher_remark(jo.getString("teacher_remark"));
                        itemList.setStatus_name(jo.getString("status"));
                        itemList.setSchool_id(jo.getString("school_id"));
                        itemList.setCraeted_on(jo.getString("craeted_on"));
                        itemList.setIs_approved(jo.getString("is_approved"));
                        itemList.setClass_name(jo.getString("class_name"));

                        arrayList.add(itemList);
                    }
                    adapterTeacherLeaveList = new AdapterTeacherLeaveList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterTeacherLeaveList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    arrayList.remove(deletePosition);
                    adapterTeacherLeaveList.notifyDataSetChanged();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    leaveListRefresh();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 3) {
                JSONArray array = response.getJSONArray("data");
                leaveList.clear();
                leaveListId.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jo = array.getJSONObject(i);
                    leaveListId.add(jo.getString("id"));
                    leaveList.add(jo.getString("label"));
                }
                adapterLeaveTypes = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, leaveList);

            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String date = dayOfMonth + "-" + month + "-" + year;
        if (view.getTag().equalsIgnoreCase("todate")) {
            mTvToDate.setText(date);
        } else {
            mTvFromDate.setText(date);
        }
    }
}
