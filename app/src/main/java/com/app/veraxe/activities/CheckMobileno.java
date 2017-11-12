package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;

import org.json.JSONObject;

import java.util.HashMap;


public class CheckMobileno extends AppCompatActivity implements ApiResponse {

    EditText old_password, new_password, confirm_password;
    Context context;
    Button Change_password;
    ConnectionDetector cd;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    RelativeLayout rl_main_layout, rl_network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mobile);

        context = this;
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(getApplicationContext());

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

                    if (!old_password.getText().toString().equalsIgnoreCase("")) {
                        try {

                            if (AppUtils.isNetworkAvailable(context)) {

                                HashMap<String, Object> hm = new HashMap<>();
                                hm.put("mobilenumber", old_password.getText().toString());
                                hm.put("authkey", Constant.AUTHKEY);

                                String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.resendotp);
                                new CommonAsyncTaskHashmap(1, context, CheckMobileno.this).getquery(url, hm);

                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "Please enter mobileno", Toast.LENGTH_SHORT).show();
                    }
//
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();

                }

            }

        });


    }


    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!old_password.getText().toString().equalsIgnoreCase("")
                && !new_password.getText().toString().equalsIgnoreCase("")
                && !confirm_password.getText().toString().equalsIgnoreCase("")) {

            if (!new_password.getText().toString()
                    .equals(confirm_password.getText().toString())) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.pasword_mismatch), Toast.LENGTH_SHORT);
            } else {
                isValidLoginDetails = true;
            }

        } else {
            if (old_password.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.enter_mobileno), Toast.LENGTH_SHORT);

            } else if (new_password.getText().toString().equalsIgnoreCase("")) {
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

    private void requestOtp()

    {
        if (AppUtils.isNetworkAvailable(context)) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("mobilenumber", old_password.getText().toString());
            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.resendotp);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {

            Toast.makeText(context,
                    getString(R.string.message_network_problem),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    // Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, OtpVerificationActivity.class);
                    intent.putExtra("opt", response.getString("otp"));
                    intent.putExtra("mobileno", old_password.getText().toString());
                    startActivity(intent);

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {

                if (response.getString("response").equalsIgnoreCase("1")) {

                    Intent intent = new Intent(context, OtpVerificationActivity.class);
                    intent.putExtra("opt", response.getString("otp"));
                    intent.putExtra("mobileno", old_password.getText().toString());
                    startActivity(intent);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
