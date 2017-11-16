package com.app.veraxe.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.veraxe.R;
import com.app.veraxe.interfaces.ApiResponse;

import org.json.JSONObject;

/**
 * Created by Hemanta on 11/7/2016.
 */
public class CommonAsyncTaskVolley {

    private ProgressDialog pd;
    private RequestQueue queue;
    private Context context;
    private ApiResponse listener;
    int method;

    public CommonAsyncTaskVolley(int method, Context context, ApiResponse response) {

        queue = Volley.newRequestQueue(context);
        this.context = context;
        listener = response;
        this.method = method;
        pd = new ProgressDialog(context);
        pd.setMessage("Processing ... ");
        pd.setCancelable(false);

    }


    public void getqueryJsonNoProgress(String url, JSONObject jsonObject, int methodType) {
        // String url = context.getResources().getString(R.string.base_url) + addurl;
        Log.e("request", ": " + url + jsonObject);
        JsonObjectRequest mJsonRequest = new JsonObjectRequest(
                methodType,
                url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    if (response != null) {

                        if (listener != null)
                            listener.getResponse(method, response);
                    } else {
                        if (listener != null)
                            // listener.onPostRequestFailed(method, "Null data from server.");
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.problem_server),
                                    Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    if (listener != null) {
                        // listener.getResponse(method, error.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

        };
        // Adding request to request queue
        queue.add(mJsonRequest);

        mJsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000 * 40, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }


    public void getqueryJsonbject(String url, JSONObject jsonObject, int MethodType) {
        // String url = context.getResources().getString(R.string.base_url) + addurl;
        Log.e("request", ": " + url + "  " + jsonObject);
        pd.show();
        JsonObjectRequest mJsonRequest = new JsonObjectRequest(
                MethodType,
                url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                if (pd != null)
                    pd.cancel();
                try {
                    if (response != null) {

                        if (listener != null)
                            listener.getResponse(method, response);
                    } else {
                        if (listener != null)
                            // listener.onPostRequestFailed(method, "Null data from server.");
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.problem_server),
                                    Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // hide the progress dialog
                if (pd != null)
                    pd.cancel();
                try {
                    if (listener != null) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        // Adding request to request queue
        queue.add(mJsonRequest);

        mJsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000 * 40, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
