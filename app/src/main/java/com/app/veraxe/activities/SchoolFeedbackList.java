package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterSchoolFeedbackList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.student.AddFeedback;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class SchoolFeedbackList extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterSchoolFeedbackList adapterStudentFeedbackList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    FloatingActionButton btn_addfeedback;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schoolfeedbacklist);

        context = this;
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Students");
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        setListener();
        messageList();


    }


    @Override
    public void onDestroy() {

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
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
        btn_addfeedback = (FloatingActionButton) findViewById(R.id.btn_addfeedback);



    }


    public void setListener() {

        btn_addfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(context, AddFeedback.class);
                startActivityForResult(in, 21);

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                messageListRefresh();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 21 && resultCode == RESULT_OK) {
            messageListRefresh();
        }
    }

    public void messageList() {
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("user_id", AppUtils.getUserId(context));
            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.school_feedback_list);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void messageListRefresh() {
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("user_id", AppUtils.getUserId(context));
            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.school_feedback_list);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClickListener(int position, int flag) {

        if (flag == 1) {

            Intent intent = new Intent(context, SchoolFeedbackDetail.class);
            intent.putExtra("feedback_id", arrayList.get(position).getId());
            intent.putExtra("status_id", arrayList.get(position).getStatus_id());
            intent.putExtra("student_name", arrayList.get(position).getStudent_name());
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
                        itemList.setStudent_id(jo.getString("student_id"));
                        itemList.setStudent_name(jo.getString("student_name"));
                        itemList.setDepartment_id(jo.getString("department_id"));
                        itemList.setDepartment_label(jo.getString("department_label"));
                        itemList.setDatetime(jo.getString("datetime"));
                        itemList.setFeedback_type_id(jo.getString("feedback_type_id"));
                        itemList.setRowType(1);
                        itemList.setFeedback_type_label(jo.getString("feedback_type_label"));
                        itemList.setMessage_text(jo.getString("message_text"));
                        itemList.setStatus_id(jo.getString("status_id"));
                        itemList.setStatus_name(jo.getString("status_name"));

                        arrayList.add(itemList);

                    }
                    adapterStudentFeedbackList = new AdapterSchoolFeedbackList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterStudentFeedbackList);
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
