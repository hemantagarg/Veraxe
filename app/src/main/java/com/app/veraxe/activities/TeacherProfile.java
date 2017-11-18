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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class TeacherProfile extends AppCompatActivity implements ApiResponse {

    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private ImageView image_user;
    private TextView text_designation, text_gender, text_mobile, text_email, text_address, text_username, text_school_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        context = this;
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setListener();
        getTeacherProfile();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void getTeacherProfile() {
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userid", AppUtils.getUserId(context));
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.userprofile);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text_designation = (TextView) findViewById(R.id.text_designation);
        text_school_name = (TextView) findViewById(R.id.text_school_name);
        text_gender = (TextView) findViewById(R.id.text_gender);
        text_mobile = (TextView) findViewById(R.id.text_mobile);
        text_email = (TextView) findViewById(R.id.text_email);
        text_address = (TextView) findViewById(R.id.text_address);
        text_username = (TextView) findViewById(R.id.text_username);
        image_user = (ImageView) findViewById(R.id.image_user);

    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject result = response.getJSONObject("result");

                    Picasso.with(context).load(result.getString("avtar")).transform(new CircleTransform()).placeholder(R.drawable.user).into(image_user);
                    text_username.setText(result.getString("name"));
                    if (!result.getString("avtar").equalsIgnoreCase("")) {
                        AppUtils.setUserImage(context, result.getString("avtar"));
                    }
                    text_designation.setText(result.getString("designation"));
                    text_mobile.setText(result.getString("mobile"));
                    text_address.setText(result.getString("address"));
                    text_email.setText(result.getString("email"));
                    text_school_name.setText(result.getString("school_name"));
                    if (result.getString("gender").equalsIgnoreCase("m")) {
                        text_gender.setText(R.string.male);

                    } else {
                        text_gender.setText(R.string.female);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
