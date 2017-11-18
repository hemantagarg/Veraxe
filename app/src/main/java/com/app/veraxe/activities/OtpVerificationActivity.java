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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.AppConstants;

import org.json.JSONObject;

import java.util.HashMap;

public class OtpVerificationActivity extends AppCompatActivity implements ApiResponse {

    Toolbar toolbar;
    EditText edit_otp;
    Context context;
    private BroadcastReceiver broadcastReceiver;
    TextView text_resendsms, text_msg, text_verify_otp;
    String mobileno = "", isregisterPage = "";
    BroadcastReceiver broadcastReceiver1;
    private String otp = "";
    TextView text_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        context = this;

        init();
        Intent in = getIntent();
        if (in.hasExtra("mobileno")) {
            mobileno = in.getExtras().getString("mobileno");
            otp = in.getExtras().getString("opt");

        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Verify " + mobileno);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text_msg.setText("(Enter the otp below in case if we fail to detect the SMS automatically)");
        setListener();


    }

    private void registerSmsReciever() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.veraxe.otprecieved");

        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("onReceive", "Sms recieved in progress");

                edit_otp.setText(intent.getStringExtra("otp"));
                edit_otp.setSelection(edit_otp.getText().length());

            }
        };

        registerReceiver(broadcastReceiver1, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerSmsReciever();
    }

    private void requestOtp()

    {
        if (AppUtils.isNetworkAvailable(context)) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("mobilenumber", mobileno);
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.resendotp);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {

            Toast.makeText(context,
                    getString(R.string.message_network_problem),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void verifyOtp() {

        if (otp.equalsIgnoreCase(edit_otp.getText().toString())) {


            Intent in = new Intent(context, SetStudentPassword.class);
            in.putExtra("mobileno",mobileno);
            startActivity(in);
            finish();

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (broadcastReceiver1 != null) {
            unregisterReceiver(broadcastReceiver1);
        }
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

        text_verify_otp = (TextView) findViewById(R.id.text_verify_otp);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edit_otp = (EditText) findViewById(R.id.edit_otp);
        text_resendsms = (TextView) findViewById(R.id.text_resendsms);
        text_msg = (TextView) findViewById(R.id.text_msg);
        text_terms = (TextView) findViewById(R.id.text_terms);
    }

    private void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        text_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TermsCondition.class);
                startActivity(intent);


            }
        });


        text_verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit_otp.getText().toString().length() > 3) {
                    verifyOtp();
                } else {
                    Toast.makeText(context, "Please input valid code", Toast.LENGTH_LONG).show();
                }
            }
        });

        text_resendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestOtp();
            }
        });
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    otp = response.getString("otp");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
