package com.app.veraxe.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.student.StudentList;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity implements ApiResponse {

    Context context;
    TextView text_school, text_student, text_forgot_password;
    EditText text_mobileno, text_schoolId, text_username, text_password;
    Button btn_signin;
    boolean isStudent = true;
    RelativeLayout rl_mobile, rl_username, rl_password, rl_schoolid;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;
    TextView text_terms;
    private BroadcastReceiver broadcastReceiver;
    private static final int PERMISSIONS_REQUEST_STATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        context = this;
        init();

        setListener();
        if (isStudent) {
            text_school.setBackgroundResource(R.drawable.tab1);
            text_student.setBackgroundResource(R.drawable.tab2);
            rl_password.setVisibility(View.GONE);
            rl_username.setVisibility(View.GONE);
            rl_schoolid.setVisibility(View.GONE);
            rl_mobile.setVisibility(View.VISIBLE);

        } else {
            text_school.setBackgroundResource(R.drawable.blue_button);
            text_student.setBackgroundResource(R.drawable.light_blue_button);

            rl_password.setVisibility(View.VISIBLE);
            rl_username.setVisibility(View.VISIBLE);
            rl_schoolid.setVisibility(View.VISIBLE);
            rl_mobile.setVisibility(View.GONE);


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    private void init() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
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
        text_terms = (TextView) findViewById(R.id.text_terms);
        text_mobileno = (EditText) findViewById(R.id.text_mobileno);
        text_school = (TextView) findViewById(R.id.text_school);
        text_forgot_password = (TextView) findViewById(R.id.text_forgot_password);
        text_student = (TextView) findViewById(R.id.text_student);
        text_schoolId = (EditText) findViewById(R.id.text_schoolId);
        text_username = (EditText) findViewById(R.id.text_username);
        text_password = (EditText) findViewById(R.id.text_password);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        rl_mobile = (RelativeLayout) findViewById(R.id.rl_mobile);
        rl_password = (RelativeLayout) findViewById(R.id.rl_password);
        rl_schoolid = (RelativeLayout) findViewById(R.id.rl_schoolid);
        rl_username = (RelativeLayout) findViewById(R.id.rl_username);

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void setListener() {

        text_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStudent) {
                    Intent intent = new Intent(context, CheckMobileno.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ChangePasswordTeacher.class);
                    //startActivity(intent);
                }
            }
        });

        text_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TermsCondition.class);
                startActivity(intent);


            }
        });


        text_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStudent = false;
                text_school.setBackgroundResource(R.drawable.blue_button);
                text_student.setBackgroundResource(R.drawable.light_blue_button);

                rl_password.setVisibility(View.VISIBLE);
                text_forgot_password.setVisibility(View.GONE);
                rl_username.setVisibility(View.VISIBLE);
                rl_schoolid.setVisibility(View.VISIBLE);
                rl_mobile.setVisibility(View.GONE);

            }
        });

        text_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStudent = true;
                text_school.setBackgroundResource(R.drawable.tab1);
                text_student.setBackgroundResource(R.drawable.tab2);
                rl_password.setVisibility(View.GONE);
                rl_username.setVisibility(View.GONE);
                text_forgot_password.setVisibility(View.VISIBLE);
                rl_schoolid.setVisibility(View.GONE);
                rl_mobile.setVisibility(View.VISIBLE);

            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_STATE);
                    } else {
                        checkLogin();
                    }
                } else
                    checkLogin();

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLogin();
                } else {
                    Toast.makeText(this, "Sorry!!! Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void checkLogin() {

        if (isStudent) {

            if (!text_mobileno.getText().toString().equalsIgnoreCase("")) {
                if (rl_password.getVisibility() == View.VISIBLE) {

                    loginStudent();
                } else {

                    verifyStudentNumber();
                }

            } else {
                Toast.makeText(getApplicationContext(), R.string.enter_mobileno, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (isValidLoginDetails()) {
                loginTeacher();
            }
        }
    }

    private String getemeiNo() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String no = telephonyManager.getDeviceId();

        return no;
    }

    private void loginTeacher() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("username", text_username.getText().toString());
            hm.put("password", text_password.getText().toString());
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("devicetoken", AppUtils.getGcmRegistrationKey(context));
            hm.put("devicetype", AppConstants.DEVICETYPE);
            hm.put("schoolid", text_schoolId.getText().toString());
            hm.put("emeino", getemeiNo());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.teacherlogin);
            new CommonAsyncTaskHashmap(3, context, this).getquery(url, hm);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    private void verifyStudentNumber() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("mobilenumber", text_mobileno.getText().toString());
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.studentmobile);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    private void loginStudent() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("mobilenumber", text_mobileno.getText().toString());
            hm.put("password", text_password.getText().toString());
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("devicetoken", AppUtils.getGcmRegistrationKey(context));
            hm.put("devicetype", AppConstants.DEVICETYPE);
            hm.put("emeino", getemeiNo());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.studentlogin);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!text_schoolId.getText().toString().equalsIgnoreCase("") && !text_username.getText().toString().equalsIgnoreCase("")
                && !text_password.getText().toString().equalsIgnoreCase("")) {


            isValidLoginDetails = true;


        } else {
            if (text_schoolId.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enter_schollid, Toast.LENGTH_SHORT).show();
            } else if (text_username.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enter_username, Toast.LENGTH_SHORT).show();
            } else if (text_password.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            }
        }

        return isValidLoginDetails;
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    if (response.getString("password").equalsIgnoreCase("1")) {
                        rl_password.setVisibility(View.VISIBLE);
                    } else if (response.getString("password").equalsIgnoreCase("0")) {

                        Intent intent = new Intent(context, OtpVerificationActivity.class);
                        intent.putExtra("opt", response.getString("otp"));
                        intent.putExtra("mobileno", text_mobileno.getText().toString());
                        startActivity(intent);

                    }

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    AppUtils.setAdd_status(context, response.getString("showads"));
                    AppUtils.setStudentMobile(context, text_mobileno.getText().toString());
                    AppUtils.setStudentList(context, response.getJSONArray("result").toString());
                    AppUtils.setIsStudent(context, true);
                    Intent intent = new Intent(context, StudentList.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            } else if (method == 3) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject result = response.getJSONObject("result");
                    AppUtils.setSchoolId(context, result.getString("school_id"));
                    AppUtils.setUserId(context, result.getString("id"));
                    AppUtils.setUserRole(context, result.getString("user_role"));
                    AppUtils.setUserName(context, result.getString("name"));
                    AppUtils.setUseremail(context, result.getString("email"));
                    AppUtils.setUserMobile(context, result.getString("mobile"));
                    AppUtils.setUserImage(context, result.getString("avtar"));
                    AppUtils.setIsStudent(context, false);
                    Intent intent = new Intent(context, DashboardActivity.class);
                    startActivity(intent);
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
