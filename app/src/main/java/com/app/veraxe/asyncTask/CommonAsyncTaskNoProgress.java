package com.app.veraxe.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.app.veraxe.R;
import com.app.veraxe.interfaces.ApiResponse;

import org.json.JSONObject;

/**
 * Created by Hemanta on 11/7/2016.
 */
public class CommonAsyncTaskNoProgress {
    private ProgressDialog pd;
    private AQuery aq;
    private Context context;
    private ApiResponse listener;
    int method;

    public CommonAsyncTaskNoProgress(int method, Context context, ApiResponse response) {
        aq = new AQuery(context);
        this.context = context;
        listener = response;
        this.method = method;
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait ... ");
        pd.setCancelable(false);

    }

    public static AQuery getAqueryObj(Context context) {
        return new AQuery(context);
    }


    public void getquery(String url) {
        // String url = context.getResources().getString(R.string.base_url) + addurl;

        Log.e("request", ": " + url);
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject obj, AjaxStatus status) {
                        super.callback(url, obj, status);
                        Log.e("response", "** " + obj);
                        if (obj != null) {
                            try {
                                if (listener != null) {
                                    listener.getResponse(method, obj);
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
                        } else
                            Toast.makeText(context, context.getString(R.string.message_problem), Toast.LENGTH_SHORT).

                                    show();
                        //   Alerts.okAlert(context, context.getString(R.string.something_went_wrong));
                    }
                }

        );


    }


}
