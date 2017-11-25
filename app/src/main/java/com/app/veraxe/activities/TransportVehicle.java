package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterTransportVehicleList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.asyncTask.CommonAsyncTaskVolley;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class TransportVehicle extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterTransportVehicleList adapterTransportVehicleList;
    ArrayList<ModelStudent> arrayList;
    ArrayList<ModelStudent> arrayListLocation = new ArrayList<>();
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;
    private String user = "", password = "";
    private String token = "";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportlist);

        context = this;
        init();
        setListener();
        transportList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();

        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                transportListRefresh();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void deviceLocation() {
        try {
            if (AppUtils.isNetworkAvailable(context)) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                jsonObject.put("imei", AppUtils.getimei(context));

                String url = "http://globetrack.co.in/apis/device-location/";
                new CommonAsyncTaskVolley(4, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void trackVehicle() {
        try {
            if (AppUtils.isNetworkAvailable(context)) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("imei", AppUtils.getimei(context));
                jsonObject.put("imsi", AppUtils.getimsi(context));
                jsonObject.put("login-name", user);
                jsonObject.put("password", password);

                String url = "http://globetrack.co.in/apis/login/";
                new CommonAsyncTaskVolley(5, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void transportList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("school_id", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.transport_list);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    public void trackingAvailability() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("school_id", AppUtils.getSchoolId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.tracking_availability);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    public void transportListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("student_id", AppUtils.getStudentId(context));
            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("school_id", AppUtils.getSchoolId(context));
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.transport_list);
            new CommonAsyncTaskHashmap(1, context, this).getqueryNoProgress(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject jobj = response.getJSONObject("result");
                    arrayList.clear();
                    itemList = new ModelStudent();

                    itemList.setId(jobj.getString("id"));
                    itemList.setVehicle_no(jobj.getString("vehicle_no"));
                    itemList.setColor(jobj.getString("color"));
                    itemList.setRowType(1);
                    itemList.setType(jobj.getString("type"));
                    itemList.setGps(jobj.getString("gps"));
                    itemList.setImei(jobj.getString("imei"));
                    itemList.setImsi(jobj.getString("imsi"));

                    AppUtils.setimei(context, jobj.getString("imei"));
                    AppUtils.setimsi(context, jobj.getString("imsi"));

                    arrayList.add(itemList);

                    adapterTransportVehicleList = new AdapterTransportVehicleList(context, TransportVehicle.this, arrayList);
                    mRecyclerView.setAdapter(adapterTransportVehicleList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }
                    if (itemList.getGps().equalsIgnoreCase("1")) {
                        trackingAvailability();
                    }

                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {
                if (response.getString("track").equalsIgnoreCase("1")) {
                    JSONObject gps = response.getJSONObject("gps");
                    user = gps.getString("user");
                    password = gps.getString("pass");
                    trackVehicle();
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 5) {
                if (response.getString("status").equalsIgnoreCase("OK")) {
                    JSONObject data = response.getJSONObject("data");
                    token = data.getString("token");

                } else {
                    JSONObject data = response.getJSONObject("data");
                    Toast.makeText(context, data.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("status").equalsIgnoreCase("ok")) {
                    JSONArray location = response.getJSONArray("location");
                    arrayListLocation.clear();
                    for (int i = 0; i < location.length(); i++) {
                        JSONObject jsonObject = location.getJSONObject(i);
                        itemList = new ModelStudent();
                        JSONObject packet = jsonObject.getJSONObject("packet");
                        itemList.setLat(packet.getString("lat"));
                        itemList.setAddress(packet.getString("address"));
                        itemList.setLng(packet.getString("lng"));
                        arrayListLocation.add(itemList);
                    }
                    Intent intent = new Intent(context, MapViewVehicle.class);
                    intent.putExtra("location", location.toString());
                    intent.putExtra("token", token);
                    startActivity(intent);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                } else {
                    JSONObject data = response.getJSONObject("data");
                    Toast.makeText(context, data.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClickListener(int position, int flag) {
        if (flag == 2) {
            deviceLocation();
        }
    }
}
