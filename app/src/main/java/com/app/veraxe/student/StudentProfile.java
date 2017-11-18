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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.CircleTransform;
import com.app.veraxe.utils.AppConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class StudentProfile extends AppCompatActivity implements ApiResponse {

    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    Context context;
    ImageView image_user;
    TextView text_classname, text_username, text_sectionname, text_streamname, text_rollno, text_admissionno;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        context = this;
        init();
        mAdView = (AdView) findViewById(R.id.adView);

        if (AppUtils.getAdd_status(context).equalsIgnoreCase("1")) {
            mAdView.setVisibility(View.VISIBLE);
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-5990787515520459~9332653723");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setListener();

        getStudentProfile();
    }


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


    private void getStudentProfile() {


        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.studentdetail);
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
        text_classname = (TextView) findViewById(R.id.text_classname);
        text_username = (TextView) findViewById(R.id.text_username);
        text_sectionname = (TextView) findViewById(R.id.text_sectionname);
        text_streamname = (TextView) findViewById(R.id.text_streamname);
        text_rollno = (TextView) findViewById(R.id.text_rollno);
        text_admissionno = (TextView) findViewById(R.id.text_admissionno);
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
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject result = response.getJSONObject("result");

                    Picasso.with(context).load(result.getString("avtar")).transform(new CircleTransform()).placeholder(R.drawable.user).into(image_user);
                    text_admissionno.setText(result.getString("admission_no"));
                    text_username.setText(result.getString("name"));
                    text_classname.setText(result.getString("class"));
                    text_sectionname.setText(result.getString("section"));
                    text_rollno.setText(result.getString("rollno"));
                    text_streamname.setText(result.getString("stream"));


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
