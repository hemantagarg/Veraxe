package com.app.veraxe.student;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.activities.ChangePassword;
import com.app.veraxe.activities.HolidayList;
import com.app.veraxe.activities.LeaveList;
import com.app.veraxe.activities.LibraryManagement;
import com.app.veraxe.activities.Login;
import com.app.veraxe.activities.TransportVehicle;
import com.app.veraxe.adapter.AdapterStudentDashBoard;
import com.app.veraxe.asyncTask.CommonAsyncTask;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ApiResponse, OnCustomItemClicListener {

    Context context;
    RelativeLayout rl_attendance, rl_message, rl_events, rl_homework, rl_timetable, rl_profile;
    private BroadcastReceiver broadcastReceiver;
    RecyclerView recycler_view;
    ModelStudent itemList;
    AdapterStudentDashBoard adapterStudentDashBoard;
    ArrayList<ModelStudent> arrayList;
    ImageView icon_attendance, icon_attendance_report, icon_self_attendance, icon_homework, icon_timetable, icon_profile;
    ImageView image_bg_attendance, image_bg_attendance_report, image_bg_attendance_profile, image_bg_self_attendance, image_bg_homework, image_bg_timetable;
    TextView text_attendance,nav_holiday, text_attendance_report, text_self_attendance, text_homework, text_timetable, text_profile, text_username_top;
    ImageView user_image, image_user_top;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};
    int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        context = this;
        init();
        arrayList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darkblue_background));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView text_username = (TextView) header.findViewById(R.id.username);
        user_image = (ImageView) header.findViewById(R.id.user_image);
        text_username.setText(AppUtils.getUserName(context));
        text_username_top.setText(AppUtils.getUserName(context));

        if (!AppUtils.getUserImage(context).equalsIgnoreCase("")) {
            Picasso.with(context).load(AppUtils.getUserImage(context)).placeholder(R.drawable.user).transform(new CircleTransform()).into(user_image);
            Picasso.with(context).load(AppUtils.getUserImage(context)).placeholder(R.drawable.user).transform(new CircleTransform()).into(image_user_top);
        }
        navigationView.setNavigationItemSelectedListener(this);
        setListener();

        studentList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppUtils.getUserImage(context).equalsIgnoreCase("")) {
            Picasso.with(context).load(AppUtils.getUserImage(context)).placeholder(R.drawable.user).transform(new CircleTransform()).into(user_image);
            Picasso.with(context).load(AppUtils.getUserImage(context)).placeholder(R.drawable.user).transform(new CircleTransform()).into(image_user_top);
        }

    }


    public void studentList() {

        if (AppUtils.isNetworkAvailable(context)) {

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.dashboard_student);
            new CommonAsyncTask(1, context, this).getquery(url);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

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

        rl_attendance = (RelativeLayout) findViewById(R.id.rl_attendance);
        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        rl_events = (RelativeLayout) findViewById(R.id.rl_events);
        rl_homework = (RelativeLayout) findViewById(R.id.rl_homework);
        rl_timetable = (RelativeLayout) findViewById(R.id.rl_timetable);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recycler_view.setLayoutManager(layoutManager);
        rl_profile = (RelativeLayout) findViewById(R.id.rl_profile);

        icon_attendance = (ImageView) findViewById(R.id.icon_attendance);
        image_user_top = (ImageView) findViewById(R.id.image_user_top);
        icon_attendance_report = (ImageView) findViewById(R.id.icon_attendance_report);
        icon_self_attendance = (ImageView) findViewById(R.id.icon_self_attendance);
        icon_homework = (ImageView) findViewById(R.id.icon_homework);
        icon_timetable = (ImageView) findViewById(R.id.icon_timetable);
        icon_profile = (ImageView) findViewById(R.id.icon_profile);
        text_attendance = (TextView) findViewById(R.id.text_attendance);
        text_profile = (TextView) findViewById(R.id.text_profile);
        text_attendance_report = (TextView) findViewById(R.id.text_attendance_report);
        text_self_attendance = (TextView) findViewById(R.id.text_self_attendance);
        text_homework = (TextView) findViewById(R.id.text_homework);
        text_homework = (TextView) findViewById(R.id.text_homework);
        text_timetable = (TextView) findViewById(R.id.text_timetable);
        text_username_top = (TextView) findViewById(R.id.text_username_top);
        nav_holiday = (TextView) findViewById(R.id.nav_holiday);

        image_bg_attendance = (ImageView) findViewById(R.id.image_bg_attendance);
        image_bg_attendance_report = (ImageView) findViewById(R.id.image_bg_attendance_report);
        image_bg_attendance_profile = (ImageView) findViewById(R.id.image_bg_attendance_profile);
        image_bg_homework = (ImageView) findViewById(R.id.image_bg_homework);
        image_bg_self_attendance = (ImageView) findViewById(R.id.image_bg_self_attendance);
        image_bg_timetable = (ImageView) findViewById(R.id.image_bg_timetable);

    }

    private void setListener() {

        rl_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MessageList.class);
                startActivity(intent);
            }
        });
        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, StudentProfile.class);
                startActivity(intent);
            }
        });
        rl_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, StudentEventList.class);
                startActivity(intent);
            }
        });
        rl_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, StudentHomework_list.class);
                startActivity(intent);
            }
        });
        rl_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, StudentSelfAttendance.class);
                startActivity(intent);
            }
        });
        rl_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, StudentTimeTable.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent intent = new Intent(context, StudentProfile.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.nav_changepassword) {

            Intent intent = new Intent(context, ChangePassword.class);
            startActivity(intent);

        } else if (id == R.id.nav_monthly_planner) {

            Intent intent = new Intent(context, MonthlyPlanner.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            showLogoutBox();

        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(context, MessageList.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedabck) {

            Intent intent = new Intent(context, StudentFeedbackList.class);
            startActivity(intent);
        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(context, StudentEventList.class);
            startActivity(intent);

        } else if (id == R.id.nav_students) {
            Intent intent = new Intent(context, StudentList.class);
            startActivity(intent);

        } else if (id == R.id.nav_Leaves) {
            Intent intent = new Intent(context, LeaveList.class);
            startActivity(intent);
        }else if (id == R.id.nav_holiday) {
            Intent intent = new Intent(context, HolidayList.class);
            startActivity(intent);
        } else if (id == R.id.nav_Library) {
            Intent intent = new Intent(context, LibraryManagement.class);
            startActivity(intent);
        } else if (id == R.id.nav_Transport) {
            Intent intent = new Intent(context, TransportVehicle.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }

        } else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Veraxe ,a fastest growing App ,It is very useful for Students as well as Schools" +
                    ", So why are you waiting Get it now." + "http://play.google.com/store/apps/details?id=" + context.getPackageName());
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutBox() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                StudentDashboardActivity.this);

        alertDialog.setTitle("LOG OUT !");

        alertDialog.setMessage("Are you sure you want to Logout?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AppUtils.setUserId(context, "");
                        AppUtils.setSchoolId(context, "");
                        AppUtils.setStudentId(context, "");
                        AppUtils.setStudentList(context, "");
                        AppUtils.setUserName(context, "");
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        finish();


                    }

                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == event.KEYCODE_BACK) {

            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(
                    context);

            alertDialog.setMessage("Are you sure you want to exit ?");

            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            Intent in = new Intent();
                            in.setAction("com.package.ACTION_LOGOUT");
                            sendBroadcast(in);
                            finish();

                        }
                    });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
        return super.

                onKeyDown(keyCode, event);
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
                        itemList.setTitle(jo.getString("name"));
                        itemList.setIcon(jo.getString("icon"));
                        itemList.setImage(jo.getString("image"));

                        arrayList.add(itemList);
                        if (jo.getString("id").equalsIgnoreCase("1")) {

                         /*   Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_attendance);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_attendance);
                            text_attendance.setText(itemList.getTitle());

                        } else if (jo.getString("id").equalsIgnoreCase("2")) {
                      /*      Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_homework);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_homework);
                            text_homework.setText(itemList.getTitle());

                        } else if (jo.getString("id").equalsIgnoreCase("3")) {

                        /*    Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_timetable);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_timetable);
                            text_timetable.setText(itemList.getTitle());

                        } else if (jo.getString("id").equalsIgnoreCase("4")) {

                         /*   Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_attendance_report);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_attendance_report);
                            text_attendance_report.setText(itemList.getTitle());

                        } else if (jo.getString("id").equalsIgnoreCase("5")) {

                      /*      Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_self_attendance);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_self_attendance);
                            text_self_attendance.setText(itemList.getTitle());

                        } else if (jo.getString("id").equalsIgnoreCase("6")) {

                         /*   Picasso.with(context)
                                    .load(itemList.getIcon())
                                    .into(icon_profile);*/
                            Picasso.with(context)
                                    .load(itemList.getImage())
                                    .into(image_bg_attendance_profile);
                            text_profile.setText(itemList.getTitle());

                        }

                    }
                    adapterStudentDashBoard = new AdapterStudentDashBoard(context, this, arrayList);
                    recycler_view.setAdapter(adapterStudentDashBoard);

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClickListener(int position, int flag) {
        if (flag == 1) {
            if (arrayList.get(position).getId().equalsIgnoreCase("1")) {
                Intent intent = new Intent(context, StudentSelfAttendance.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("2")) {
                Intent intent = new Intent(context, StudentHomework_list.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("3")) {
                Intent intent = new Intent(context, StudentTimeTable.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("4")) {
                Intent intent = new Intent(context, MessageList.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("5")) {
                Intent intent = new Intent(context, StudentEventList.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("6")) {
                Intent intent = new Intent(context, StudentProfile.class);
                startActivity(intent);
            }


        }
    }
}


