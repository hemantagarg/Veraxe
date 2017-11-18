package com.app.veraxe.student;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterStudentEventtList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.AppConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class StudentEventList extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterStudentEventtList adapterEventtList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;
    private AdView mAdView;
    private ImageView image_back;
    private Spinner spinner_spam_reason;
    private ArrayList<String> reasonList = new ArrayList<>();
    private ArrayList<String> reasonListId = new ArrayList<>();
    private ArrayAdapter<String> adapterReasonTypes;
    private int lastSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studenteventlist);

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

        setListener();
        eventList();


    }

    private void init() {
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
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
        image_back = (ImageView) findViewById(R.id.image_back);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));

        reasonList.add("It's sexually inappropriate");
        reasonList.add("It's violent or prohibited content");
        reasonList.add("It's offensive");
        reasonList.add("It's misleading or a scam");
        reasonList.add("I disagree with it");
        reasonList.add("Something else");

        reasonListId.add("2");
        reasonListId.add("3");
        reasonListId.add("4");
        reasonListId.add("5");
        reasonListId.add("6");
        reasonListId.add("1");
        adapterReasonTypes = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, reasonList);
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


    public void setListener() {

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eventListRefresh();
            }
        });
    }


    public void eventList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_events);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void eventListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("studentid", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_events);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClickListener(int position, int flag) {
        if (flag == 1) {
            Intent intent = new Intent(context, StudentEventDetail.class);
            intent.putExtra("eventId", arrayList.get(position).getId());
            startActivity(intent);
        } else if (flag == 3) {
            lastSelectedPosition = position;
            openSpamDialog();
        }
    }

    /**
     * Open dialog for the apply leave
     */
    private void openSpamDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // inflate the layout dialog_layout.xml and set it as contentView
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.spam_dialog, null, false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            final EditText mEdtComment = (EditText) view.findViewById(R.id.mEdtComment);
            final TextView mTvtype_of_leave = (TextView) view.findViewById(R.id.mTvtype_of_leave);
            spinner_spam_reason = (Spinner) view.findViewById(R.id.spinner_spam_reason);
            Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

            spinner_spam_reason.setAdapter(adapterReasonTypes);
            mTvtype_of_leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinner_spam_reason.performClick();
                }
            });

            spinner_spam_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mTvtype_of_leave.setText(spinner_spam_reason.getSelectedItem().toString());
                    if (reasonListId.get(spinner_spam_reason.getSelectedItemPosition()).equalsIgnoreCase("1")) {
                        mEdtComment.setVisibility(View.VISIBLE);
                    } else {
                        mEdtComment.setVisibility(View.GONE);
                        mEdtComment.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mEdtComment.getVisibility() == View.VISIBLE) {
                        if (!mEdtComment.getText().toString().equalsIgnoreCase("")) {
                            reportSpamApi(mEdtComment.getText().toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        reportSpamApi(mEdtComment.getText().toString());
                        dialog.dismiss();
                    }

                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            Log.e("Leave List", " Exception error : " + e);
        }
    }

    public void reportSpamApi(String remark) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            //   hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("spam_reason_id", reasonListId.get(spinner_spam_reason.getSelectedItemPosition()));
            hm.put("id", arrayList.get(lastSelectedPosition).getId());
            hm.put("other_reason", remark);
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("student_id", AppUtils.getStudentId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.report_spam_event);
            new CommonAsyncTaskHashmap(4, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
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
                        itemList.setTitle(jo.getString("title"));
                        itemList.setDatetime(jo.getString("datetime"));
                        itemList.setRowType(1);
                        itemList.setDescription(jo.getString("description"));
                        JSONObject date = jo.getJSONObject("date");
                        itemList.setDay(date.getString("day"));
                        itemList.setMonth(date.getString("month"));
                        itemList.setYear(date.getString("year"));
                        itemList.setTime(date.getString("time"));

                        arrayList.add(itemList);
                    }
                    adapterEventtList = new AdapterStudentEventtList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterEventtList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    eventListRefresh();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
