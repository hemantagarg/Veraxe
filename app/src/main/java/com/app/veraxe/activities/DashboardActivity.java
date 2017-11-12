package com.app.veraxe.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.app.veraxe.adapter.AdapterStudentDashBoard;
import com.app.veraxe.asyncTask.CommonAsyncTask;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.student.MessageList;
import com.app.veraxe.student.StudentProfile;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ApiResponse, OnCustomItemClicListener {

    Context context;
    RelativeLayout rl_attendance, rl_attendancereport, rl_self_attendance, rl_homework, rl_timetable, rl_profile;
    RecyclerView recycler_view;
    ModelStudent itemList;
    AdapterStudentDashBoard adapterStudentDashBoard;
    ArrayList<ModelStudent> arrayList;
    ImageView icon_attendance, icon_attendance_report, icon_self_attendance, icon_homework, icon_timetable, icon_profile;
    ImageView image_bg_attendance, image_bg_attendance_report, image_bg_attendance_profile, image_bg_self_attendance, image_bg_homework, image_bg_timetable;
    TextView text_attendance, text_attendance_report, text_self_attendance, text_homework, text_timetable, text_profile, text_username_top;
    ImageView user_image, image_user_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context = this;
        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        text_username.setText(AppUtils.getUserName(context));
        text_username_top.setText(AppUtils.getUserName(context));
        user_image = (ImageView) header.findViewById(R.id.user_image);
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

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.dashboard_school);
            new CommonAsyncTask(1, context, this).getquery(url);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    private void init() {
        arrayList = new ArrayList<>();
        rl_attendance = (RelativeLayout) findViewById(R.id.rl_attendance);
        rl_attendancereport = (RelativeLayout) findViewById(R.id.rl_attendancereport);
        rl_self_attendance = (RelativeLayout) findViewById(R.id.rl_self_attendance);
        rl_homework = (RelativeLayout) findViewById(R.id.rl_homework);
        rl_timetable = (RelativeLayout) findViewById(R.id.rl_timetable);
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

        image_bg_attendance = (ImageView) findViewById(R.id.image_bg_attendance);
        image_bg_attendance_report = (ImageView) findViewById(R.id.image_bg_attendance_report);
        image_bg_attendance_profile = (ImageView) findViewById(R.id.image_bg_attendance_profile);
        image_bg_homework = (ImageView) findViewById(R.id.image_bg_homework);
        image_bg_self_attendance = (ImageView) findViewById(R.id.image_bg_self_attendance);
        image_bg_timetable = (ImageView) findViewById(R.id.image_bg_timetable);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setListener() {

        rl_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TimeTable.class);
                startActivity(intent);
            }
        });

        rl_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Homework_list.class);
                startActivity(intent);
            }
        });
        rl_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Attendance.class);
                startActivity(intent);
            }
        });
        rl_self_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SelfAttendance.class);
                startActivity(intent);
            }
        });
        rl_attendancereport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AttendanceReport.class);
                startActivity(intent);
            }
        });
        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TeacherProfile.class);
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

            Intent intent = new Intent(context, TeacherProfile.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_changepassword) {

            Intent intent = new Intent(context, ChangePasswordTeacher.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            showLogoutBox();

        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(context, MessageList.class);
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

        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(context, EventList.class);
            startActivity(intent);

        } else if (id == R.id.nav_feedabck) {
            Intent intent = new Intent(context, SchoolFeedbackList.class);
            startActivity(intent);

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
                DashboardActivity.this);

        alertDialog.setTitle("LOG OUT !");

        alertDialog.setMessage("Are you sure you want to Logout?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AppUtils.setUserId(context, "");
                        AppUtils.setSchoolId(context, "");
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


                        arrayList.add(itemList);

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
                Intent intent = new Intent(context, Attendance.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("2")) {
                Intent intent = new Intent(context, Homework_list.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("3")) {
                Intent intent = new Intent(context, TimeTable.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("4")) {
                Intent intent = new Intent(context, AttendanceReport.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("5")) {
                Intent intent = new Intent(context, SelfAttendance.class);
                startActivity(intent);
            } else if (arrayList.get(position).getId().equalsIgnoreCase("6")) {
                Intent intent = new Intent(context, StudentProfile.class);
                startActivity(intent);
            }

        }
    }
}
