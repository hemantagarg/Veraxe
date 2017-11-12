package com.app.veraxe.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.app.veraxe.R;
import com.app.veraxe.student.StudentDashboardActivity;
import com.app.veraxe.utils.AppUtils;

public class Splash extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = this;

        try {
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
            AppUtils.setBadgeCount(context, 0);
            AppUtils.setBadge(context, AppUtils.getBadgeCount(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("Device_token", "**" + AppUtils.getGcmRegistrationKey(context));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (AppUtils.getUserId(context).equalsIgnoreCase("")) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();

                } else {
                    if (AppUtils.getIsStudent(getApplicationContext())) {
                        Intent intent = new Intent(getApplicationContext(), StudentDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

            }
        }, 1000);


    }
}
