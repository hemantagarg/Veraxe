package com.app.veraxe.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFeedback extends AppCompatActivity implements ApiResponse {

    Context context;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    AdView mAdView;
    Spinner spinner_department, spinner_nature_feedback;
    EditText text_message;
    Button button_submit;
    private ArrayList<String> department_list;
    private ArrayList<String> department_listId;
    private ArrayList<String> nature_feedback_list;
    private ArrayList<String> nature_feedback_listId;
    private ArrayAdapter<String> adapter_department, adapter_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);
        context = this;
        init();
        setListener();
        getData();

        mAdView = (AdView) findViewById(R.id.adView);
        if (AppUtils.getAdd_status(context).equalsIgnoreCase("1")) {
            mAdView.setVisibility(View.GONE);
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-5990787515520459~9332653723");

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

    }

    public void getData() {
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.feedback_department);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    public void submitFeedback() {
        if (AppUtils.isNetworkAvailable(context)) {

          /*  authkey=23de92fe7f8f6babd6fa31beacd81798
                    department_id=1
            feedback_type_id=3
            student_id=2
            school_id=3
            message_text=some message text
                    */

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("school_id", AppUtils.getSchoolId(context));
            hm.put("message_text", text_message.getText().toString());
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("department_id", department_listId.get(spinner_department.getSelectedItemPosition()));
            hm.put("feedback_type_id", nature_feedback_listId.get(spinner_nature_feedback.getSelectedItemPosition()));


            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.feedback_save);
            new CommonAsyncTaskHashmap(2, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        button_submit = (Button) findViewById(R.id.button_submit);
        text_message = (EditText) findViewById(R.id.text_message);
        spinner_department = (Spinner) findViewById(R.id.spinner_department);
        spinner_nature_feedback = (Spinner) findViewById(R.id.spinner_nature_feedback);

        nature_feedback_list = new ArrayList<>();
        nature_feedback_listId = new ArrayList<>();
        department_list = new ArrayList<>();
        department_listId = new ArrayList<>();

    }

    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!text_message.getText().toString().equalsIgnoreCase("") && spinner_department.getSelectedItemPosition() != 0
                && spinner_nature_feedback.getSelectedItemPosition() != 0) {

            isValidLoginDetails = true;

        } else {
            if (spinner_department.getSelectedItemPosition() == 0) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.selecte_department, Toast.LENGTH_SHORT).show();
            } else if (spinner_nature_feedback.getSelectedItemPosition() == 0) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.select_naturefeedback, Toast.LENGTH_SHORT).show();
            } else if (text_message.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enter_message, Toast.LENGTH_SHORT).show();
            }
        }

        return isValidLoginDetails;
    }


    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidLoginDetails()) {
                    submitFeedback();

                }
            }
        });

    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {


                    JSONArray array = response.getJSONArray("department");

                    department_list.add("Select Department");
                    department_listId.add("-1");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        department_list.add(jo.getString("name"));
                        department_listId.add(jo.getString("id"));

                    }
                    adapter_department = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, department_list);
                    spinner_department.setAdapter(adapter_department);

                    JSONArray nature_of_feedback = response.getJSONArray("nature_of_feedback");

                    nature_feedback_list.add("Select Nature of Feedback");
                    nature_feedback_listId.add("-1");

                    for (int i = 0; i < nature_of_feedback.length(); i++) {
                        JSONObject jo = nature_of_feedback.getJSONObject(i);
                        nature_feedback_list.add(jo.getString("name"));
                        nature_feedback_listId.add(jo.getString("id"));

                    }
                    adapter_feedback = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, nature_feedback_list);
                    spinner_nature_feedback.setAdapter(adapter_feedback);


                }
            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();

                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
