package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.student.StudentList;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;


public class SetStudentPassword extends AppCompatActivity implements ApiResponse {

    EditText old_password, new_password, confirm_password;
    Context context;
    Button Change_password;
    ConnectionDetector cd;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    RelativeLayout rl_main_layout, rl_network;
    String mobileno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        context = this;
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(getApplicationContext());
        mobileno = getIntent().getStringExtra("mobileno");
        setListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

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
        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        Change_password = (Button) findViewById(R.id.btn_changePassword);
        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);

    }

    private void setListener() {

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO bind to drawer with... injection?
                finish();

            }

        });


        Change_password.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cd.isConnectingToInternet()) {

                    if (isValidLoginDetails()) {
                        try {

                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("mobilenumber", mobileno);
                            hm.put("password", new_password.getText().toString());
                            hm.put("confirmpassword", confirm_password.getText().toString());
                            hm.put("authkey", AppConstants.AUTHKEY);
                            hm.put("devicetype", AppConstants.DEVICETYPE);
                            hm.put("devicetoken", AppUtils.getGcmRegistrationKey(context));
                            hm.put("emeino", getemeiNo());

                            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.setup_studentpassword);
                            new CommonAsyncTaskHashmap(1, context, SetStudentPassword.this).getquery(url, hm);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    private String getemeiNo() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String no = telephonyManager.getDeviceId();

        return no;
    }

    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!new_password.getText().toString().equalsIgnoreCase("")
                && !confirm_password.getText().toString().equalsIgnoreCase("")) {

            if (!new_password.getText().toString()
                    .equals(confirm_password.getText().toString())) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.pasword_mismatch), Toast.LENGTH_SHORT);
            } else {
                isValidLoginDetails = true;
            }

        } else {
            if (new_password.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.enter_new_pass), Toast.LENGTH_SHORT);
            } else if (confirm_password.getText().toString()
                    .equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.reenter_pass), Toast.LENGTH_SHORT);
            }
        }

        return isValidLoginDetails;
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    //  Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                    AppUtils.setStudentMobile(context, mobileno);
                    AppUtils.setAdd_status(context, response.getString("showads"));
                    AppUtils.setStudentList(context, response.getJSONArray("result").toString());
                    AppUtils.setIsStudent(context, true);
                    Intent intent = new Intent();
                    intent.setAction("com.package.ACTION_LOGOUT");
                    sendBroadcast(intent);

                    Intent in = new Intent(context, StudentList.class);
                    startActivity(in);
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
