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
import com.app.veraxe.utils.AppConstants;

import org.json.JSONObject;

import java.util.HashMap;


public class ChangePassword extends AppCompatActivity implements ApiResponse {

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
        setContentView(R.layout.activity_change_password);

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
                            hm.put("mobilenumber", old_password.getText().toString());
                            hm.put("password", new_password.getText().toString());
                            hm.put("confirmpassword", confirm_password.getText().toString());
                            hm.put("authkey", AppConstants.AUTHKEY);

                            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.update_studentpassword);
                            new CommonAsyncTaskHashmap(1, context, ChangePassword.this).getquery(url, hm);
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


    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!old_password.getText().toString().equalsIgnoreCase("")
                && !new_password.getText().toString().equalsIgnoreCase("")
                && !confirm_password.getText().toString().equalsIgnoreCase("")) {

            if (!new_password.getText().toString()
                    .equals(confirm_password.getText().toString())) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.pasword_mismatch), Toast.LENGTH_SHORT).show();

            } else {
                isValidLoginDetails = true;
            }

        } else {
            if (old_password.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.enter_mobileno), Toast.LENGTH_SHORT).show();


            } else if (new_password.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.enter_new_pass), Toast.LENGTH_SHORT).show();

            } else if (confirm_password.getText().toString()
                    .equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(context, getResources().getString(R.string.reenter_pass), Toast.LENGTH_SHORT).show();

            }
        }

        return isValidLoginDetails;
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
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
}
