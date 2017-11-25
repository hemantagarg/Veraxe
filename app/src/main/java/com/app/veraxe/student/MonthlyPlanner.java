package com.app.veraxe.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.activities.DownLoadDocsFile;
import com.app.veraxe.activities.DownLoadFile;
import com.app.veraxe.adapter.AdapterMonthlyPlanner;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MonthlyPlanner extends AppCompatActivity implements ApiResponse, OnCustomItemClicListener {

    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private TextView text_download, text_jan, text_feb, text_march, text_april, text_may, text_june, text_july,
            text_august, text_september, text_october, text_november, text_december;
    private RelativeLayout rl_download;
    private String fileUrl = "", file_name = "";
    private RecyclerView recycler_view;
    private ArrayList<ModelStudent> arrayList;
    private AdapterMonthlyPlanner adapterMonthlyPlanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_planner);

        context = this;
        init();
        setListener();
        getMonthlyPlanner("");
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

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        arrayList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rl_download = (RelativeLayout) findViewById(R.id.rl_download);
        text_download = (TextView) findViewById(R.id.text_download);
        text_jan = (TextView) findViewById(R.id.text_jan);
        text_feb = (TextView) findViewById(R.id.text_feb);
        text_march = (TextView) findViewById(R.id.text_march);
        text_april = (TextView) findViewById(R.id.text_april);
        text_may = (TextView) findViewById(R.id.text_may);
        text_june = (TextView) findViewById(R.id.text_june);
        text_july = (TextView) findViewById(R.id.text_july);
        text_august = (TextView) findViewById(R.id.text_august);
        text_september = (TextView) findViewById(R.id.text_september);
        text_october = (TextView) findViewById(R.id.text_october);
        text_november = (TextView) findViewById(R.id.text_november);
        text_december = (TextView) findViewById(R.id.text_december);

    }


    private void getMonthlyPlanner(String month) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_monthly_planner);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void setAllNormal() {
        text_jan.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_feb.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_march.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_april.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_may.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_june.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_july.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_august.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_september.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_october.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_november.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
        text_december.setBackgroundColor(getResources().getColor(R.color.text_color_grey));
    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rl_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DownLoadFile.class);
                intent.putExtra(DownLoadFile.FILENAME, file_name);
                intent.putExtra(DownLoadFile.URL,
                        fileUrl);
                context.startService(intent);

                Toast.makeText(context, "Your file download is in progress", Toast.LENGTH_SHORT).show();

            }
        });

        text_jan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_jan.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("january");
            }
        });
        text_feb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_feb.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("Febuary");
            }
        });
        text_march.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_march.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("March");
            }
        });
        text_april.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_april.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("April");
            }
        });
        text_may.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_may.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("May");
            }
        });
        text_june.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_june.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("June");
            }
        });
        text_july.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_july.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("July");
            }
        });
        text_august.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_august.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("August");
            }
        });
        text_september.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_september.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("September");
            }
        });
        text_october.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_october.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("October");
            }
        });
        text_november.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_november.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("November");
            }
        });
        text_december.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllNormal();
                text_december.setBackgroundColor(getResources().getColor(R.color.red_color));
                getMonthlyPlanner("December");
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
                    JSONArray array = response.getJSONArray("result");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        ModelStudent itemList = new ModelStudent();

                        itemList.setId(jo.getString("id"));
                        itemList.setName(jo.getString("month"));
                        itemList.setFilename(jo.getString("file_name"));
                        itemList.setRowType(1);
                        itemList.setUrl(jo.getString("file_url"));

                        arrayList.add(itemList);

                    }
                    adapterMonthlyPlanner = new AdapterMonthlyPlanner(context, this, arrayList);
                    recycler_view.setAdapter(adapterMonthlyPlanner);
                  /*  "result": [
                    {
                        "id": "10",
                            "month": "January",
                            "file_name": "j2_1492866451.jpg",
                            "file_url": "http://veraxe.com/schools/uploads/student/files/j2_1492866451.jpg"
                    }
  ]*/
                    fileUrl = "";
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

            Intent intent = new Intent(context, DownLoadDocsFile.class);
            intent.putExtra(DownLoadFile.FILENAME, arrayList.get(position).getFilename());
            intent.putExtra(DownLoadFile.URL,
                    arrayList.get(position).getUrl());
            intent.putExtra(DownLoadFile.FILETYPE,
                    arrayList.get(position).getFile_type());

            context.startService(intent);

            Toast.makeText(context, "Your file download is in progress", Toast.LENGTH_SHORT).show();
        }
    }
}
