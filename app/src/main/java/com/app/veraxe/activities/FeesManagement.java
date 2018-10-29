package com.app.veraxe.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterFeesHeaderList;
import com.app.veraxe.adapter.AdapterFeesList;
import com.app.veraxe.adapter.AdapterLibraryList;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.asyncTask.CommonAsyncTaskVolley;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelFeesHistory;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.model.ModelStudentFees;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 06-01-2016.
 */
public class FeesManagement extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse, PaymentResultListener {

    Context context;
    RecyclerView mRecyclerView, recycler_viewret;
    ModelFeesHistory itemList;
    AdapterFeesList adapterFeesList;
    ArrayList<ModelFeesHistory> arrayList = new ArrayList<>();
    ArrayList<ModelStudent> arrayListret;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    @BindView(R.id.mTvFeeSchedule)
    TextView mTvFeeSchedule;
    @BindView(R.id.mTvSubTotal)
    TextView mTvSubTotal;
    @BindView(R.id.mTvLastFeeBalance)
    TextView mTvLastFeeBalance;
    @BindView(R.id.mTvDiscount)
    TextView mTvDiscount;
    @BindView(R.id.mTvGrandTotal)
    TextView mTvGrandTotal;
    @BindView(R.id.mTvPaymentCharges)
    TextView mTvPaymentCharges;
    @BindView(R.id.mTvPayableAmount)
    TextView mTvPayableAmount;
    @BindView(R.id.mBtnPayNow)
    Button mBtnPayNow;
    @BindView(R.id.mRlPaybaleFees)
    RelativeLayout mRlPaybaleFees;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh1;
    private Button btn_teama, btn_teamb;
    private String TAG = FeesManagement.class.getSimpleName();
    private JSONObject response;
    private String orderId = "";
    private String orderNo = "";
    private String paybleAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fees_management);
        ButterKnife.bind(this);
        context = this;
        init();
        setListener();
        studentPayableFees();

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
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        arrayListret = new ArrayList<>();

        rl_main_layout = findViewById(R.id.rl_main_layout);
        rl_network = findViewById(R.id.rl_network);
        mRecyclerView = findViewById(R.id.recycler_view);
        recycler_viewret = findViewById(R.id.recycler_viewret);
        recycler_viewret.setLayoutManager(new LinearLayoutManager(context));
        btn_teamb = findViewById(R.id.btn_teamb);
        btn_teama = findViewById(R.id.btn_teama);
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        swipe_refresh1 = findViewById(R.id.swipe_refresh1);
        swipe_refresh1.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        swipe_refresh1.setVisibility(View.GONE);
        mRlPaybaleFees.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {
        swipe_refresh1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feesListRefresh();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_teama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_teama.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_selected));
                btn_teamb.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_unselected));
                btn_teama.setTextColor(ContextCompat.getColor(context, R.color.white));
                btn_teamb.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                swipe_refresh1.setVisibility(View.GONE);
                mRlPaybaleFees.setVisibility(View.VISIBLE);
                studentPayableFees();
            }
        });
        btn_teamb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_teamb.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_selected));
                btn_teama.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg_unselected));
                btn_teamb.setTextColor(ContextCompat.getColor(context, R.color.white));
                btn_teama.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                swipe_refresh1.setVisibility(View.VISIBLE);
                mRlPaybaleFees.setVisibility(View.GONE);
                feesList();
            }
        });

    }

    /**
     * Open dialog for the apply leave
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK) {

            feesListRefresh();
        }
    }

    public void studentPayableFees() {

        if (AppUtils.isNetworkAvailable(context)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("studentId", AppUtils.getStudentId(context));
                jsonObject.put("schoolId", AppUtils.getSchoolId(context));
                jsonObject.put("authkey", AppConstants.AUTHKEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.studentPayableFees);
            new CommonAsyncTaskVolley(2, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    public void feesList() {
        if (AppUtils.isNetworkAvailable(context)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("studentId", AppUtils.getStudentId(context));
                jsonObject.put("schoolId", AppUtils.getSchoolId(context));
                jsonObject.put("authkey", AppConstants.AUTHKEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.feeHistory);
            new CommonAsyncTaskVolley(1, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    public void feesListRefresh() {
        if (AppUtils.isNetworkAvailable(context)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("studentId", AppUtils.getStudentId(context));
                jsonObject.put("schoolId", AppUtils.getSchoolId(context));
                jsonObject.put("authkey", AppConstants.AUTHKEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.feeHistory);
            new CommonAsyncTaskVolley(1, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClickListener(int position, int flag) {


    }


    public void savePayableFees() {
        if (AppUtils.isNetworkAvailable(context)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("receiptData", response.getJSONObject("data").getJSONObject("receiptData"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.savePayableFees);
            new CommonAsyncTaskVolley(3, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("data");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelFeesHistory();

                        itemList.setId(jo.getString("id"));
                        itemList.setInviceNo(jo.getString("inviceNo"));
                        itemList.setTotal(jo.getString("total"));
                        itemList.setPaid(jo.getString("paid"));
                        itemList.setBalance(jo.getString("balance"));
                        itemList.setCreatedDate(jo.getString("createdDate"));
                        itemList.setCreatedTime(jo.getString("createdTime"));
                        itemList.setDownloadLink(jo.getString("downloadLink"));

                        arrayList.add(itemList);
                    }

                    adapterFeesList = new AdapterFeesList(context, this, arrayList);
                    recycler_viewret.setAdapter(adapterFeesList);
                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    this.response = response;
                    ModelStudentFees dataModel = new ModelStudentFees();
                    Gson gson = new Gson();
                    try {
                        dataModel = gson.fromJson(response.toString(), ModelStudentFees.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (dataModel != null && dataModel.getData() != null) {

                        AdapterFeesHeaderList adapterFeesHeaderList = new AdapterFeesHeaderList(context, this, dataModel.getData().getFees());
                        mRecyclerView.setAdapter(adapterFeesHeaderList);
                        mTvFeeSchedule.setText(dataModel.getData().getFeeSchedule());
                        ModelStudentFees.DataBean.SummeryBean summery = dataModel.getData().getSummery();
                        mTvGrandTotal.setText(summery.getGrandTotal());
                        mTvSubTotal.setText(summery.getSubtotal());
                        mTvLastFeeBalance.setText(summery.getBalance());
                        mTvPaymentCharges.setText(summery.getServiceAmount());
                        mTvPayableAmount.setText(summery.getPayableAmount());
                        mTvDiscount.setText(summery.getDiscount());
                    }

                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 3) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject data = response.optJSONObject("data");
                    orderId = data.optString("orderId");
                    orderNo = data.optString("orderNo");
                    paybleAmount = data.optString("paybleAmount");
                    startPayment();
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.mBtnPayNow)
    public void onViewClicked() {
        savePayableFees();
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        String price = mTvPayableAmount.getText().toString();
        int intPrice = (int) Math.round(Double.parseDouble(price));
        intPrice = intPrice * 100;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Veraxe");
            options.put("description", "Recharge");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", intPrice);

            JSONObject preFill = new JSONObject();
            preFill.put("email", AppUtils.getUseremail(context));
            preFill.put("contact", AppUtils.getUserMobile(context));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            //    makePayment();
            updatePaymentTransaction(razorpayPaymentID, "1");
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.e("failed response", "**" + response);
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            //   updatePaymentTransaction(razorpayPaymentID, "2");
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    public void updatePaymentTransaction(String razorpayPaymentID, String status) {

        if (AppUtils.isNetworkAvailable(context)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("orderNo", orderNo);
                jsonObject.put("paymentStatus", status);
                jsonObject.put("paymentId", razorpayPaymentID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.updatePaymentTransaction);
            new CommonAsyncTaskVolley(4, context, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

/*
    private void makePayment() {
        try {
            if (AppUtils.isNetworkAvailable(mContext)) {
                //http://humtumpay.in/api/cart_payment?user_id=18
                String url = JsonApiHelper.BASEURL + JsonApiHelper.CART_PAYMENT + "user_id=" + AppUtils.getUserId(mContext);
                new CommonAsyncTaskHashmap(3, mContext, this).getqueryJsonbject(url, null, Request.Method.GET);
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
