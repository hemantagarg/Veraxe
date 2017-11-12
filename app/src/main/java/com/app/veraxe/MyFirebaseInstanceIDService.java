package com.app.veraxe;

/**
 * Created by seocor1 on 9/14/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.app.veraxe.utils.Constant;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken, deviceid;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
//         getdeviceID();
        saveregId();
        //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }

    private void getdeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceid = telephonyManager.getDeviceId();

    }

    private void saveregId() {
        pref = getApplicationContext().getSharedPreferences(Constant.MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(Constant.DEVICEID, "gdhfghghhg");
        editor.putString(Constant.REGID, refreshedToken);
        editor.commit();
    }
}