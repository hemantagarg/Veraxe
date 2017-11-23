package com.app.veraxe.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.veraxe.R;
import com.app.veraxe.asyncTask.CommonAsyncTaskVolley;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapViewVehicle extends AppCompatActivity implements OnMapReadyCallback, ApiResponse {

    private Toolbar toolbar;
    private Context context;
    private GoogleMap map;
    private ArrayList<LatLng> markerLocation;
    private ArrayList<ModelStudent> arrayListLocation;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    private int clickedPos;
    private double mLat, mLong;
    private GPSTracker gTraker;
    private RelativeLayout rl_userdetail;
    private String token = "";
    private TextView text_address, text_distance, text_time;
    private boolean isFirstTime = true;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_vehicle);
        context = this;
        init();
        setListener();
        gTraker = new GPSTracker(context);

        if (gTraker.canGetLocation()) {

            mLat = gTraker.getLatitude();
            mLong = gTraker.getLongitude();

            Log.e("mLat", "" + mLat);
            Log.e("mLong", "" + mLong);

        } else {
            showSettingsAlert();
            // getTrainingList();
        }

        String locationarray = getIntent().getStringExtra("location");
        token = getIntent().getStringExtra("token");
        setData(locationarray);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMap();
    }

    private void refreshMap() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                deviceLocation();
                refreshMap();
            }
        };
        handler.postDelayed(runnable, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop(); if (handler != null) {
            handler.removeCallbacks(runnable);
        }

    }

    public void deviceLocation() {
        try {
            if (AppUtils.isNetworkAvailable(context)) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                jsonObject.put("imei", AppUtils.getimei(context));

                String url = "http://globetrack.co.in/apis/device-location/";
                new CommonAsyncTaskVolley(1, context, this).getqueryJsonbjectNoProgress(url, jsonObject, Request.Method.POST);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showSettingsAlert() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /*
          * initialize all views
          */
    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_userdetail = (RelativeLayout) findViewById(R.id.rl_userdetail);
        text_address = (TextView) findViewById(R.id.text_address);
        text_distance = (TextView) findViewById(R.id.text_distance);
        text_time = (TextView) findViewById(R.id.text_time);
    }


    private void setData(String location) {

        arrayListLocation = new ArrayList<>();
        markerLocation = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(location);

            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                ModelStudent itemList = new ModelStudent();
                JSONObject packet = jsonObject.getJSONObject("packet");
                itemList.setLat(packet.getString("lat"));
                itemList.setAddress(packet.getString("address"));
                itemList.setLng(packet.getString("lng"));
                text_address.setText(itemList.getAddress());
                arrayListLocation.add(itemList);

                LatLng lat = new LatLng(Double.parseDouble(packet.getString("lat")), Double.parseDouble(packet.getString("lng")));
                markerLocation.add(lat);
            }
            if (isFirstTime) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                isFirstTime = false;
            }else {
                setMapData();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * manage click listener of all views
    */
    private void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        setMapData();

    }

    private void setMapData() {
        map.addMarker(new MarkerOptions().position(new LatLng(mLat, mLong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.redmap)).title("Current Location"));
        try {
            map.setMyLocationEnabled(true);
            try {
                for (int i = 0; i < markerLocation.size(); i++) {
                    Log.e("markerLocation", "**" + markerLocation.get(i));
                    Marker marker = map.addMarker(new MarkerOptions().position(markerLocation.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.redmap)).title(""));
                    mHashMap.put(marker, i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            map.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            map.getUiSettings().setMyLocationButtonEnabled(true);

            // mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation.get(0)));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerLocation.get(0)).zoom(9).build();

            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            LatLng origin = new LatLng(mLat, mLong);
            LatLng dest = new LatLng(0, 0);
            if (markerLocation.size() > 0) {
                dest = markerLocation.get(0);
                //   getAddress(markerLocation.get(0).latitude, markerLocation.get(0).longitude);
            }

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getAddress(double lat1, double lon1) {
        try {
            GetAddressFromURLTask1 task1 = new GetAddressFromURLTask1();
            task1.execute(new String[]{lat1 + "", lon1 + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception nloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        String distance = (String) point.get("distance");
                        text_distance.setText("Distance : " + distance);
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        String duration = (String) point.get("duration");
                        text_time.setText("Time : " + duration);
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(3);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route

            if (lineOptions != null) {
                map.addPolyline(lineOptions);
            }
        }
    }

    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("ok")) {
                    JSONArray location = response.getJSONArray("location");
                    arrayListLocation.clear();
                    for (int i = 0; i < location.length(); i++) {
                        JSONObject jsonObject = location.getJSONObject(i);
                        ModelStudent itemList = new ModelStudent();
                        JSONObject packet = jsonObject.getJSONObject("packet");
                        itemList.setLat(packet.getString("lat"));
                        itemList.setAddress(packet.getString("address"));
                        itemList.setLng(packet.getString("lng"));
                        arrayListLocation.add(itemList);
                    }
                    setData(location.toString());
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private class GetAddressFromURLTask1 extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... urls) {

            String response = "";
            HttpResponse response2 = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + urls[0] + "," + urls[1] + "&ln=en");

                HttpClient client = new DefaultHttpClient();
                Log.e("Url ", "http://maps.google.com/maps/api/geocode/json?ln=en&latlng=" + urls[0] + "," + urls[1]);
                try {
                    response2 = client.execute(httpGet);

                    HttpEntity entity = response2.getEntity();

                    char[] buffer = new char[2048];
                    Reader reader = new InputStreamReader(entity.getContent(), "UTF-8");

                    while (true) {
                        int n = reader.read(buffer);
                        if (n < 0) {
                            break;
                        }
                        stringBuilder.append(buffer, 0, n);
                    }

                    Log.e("Url response1", stringBuilder.toString());

                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (Exception e) {

                e.printStackTrace();
                Log.e("Error 2 :>>", "error in doINBackground OUTER");
                //infowindow.setText("Error in connecting to Google Server... try again later");
            }
            return stringBuilder.toString();
            //return jsonObject;
        }


        protected void onPostExecute(String result) {

            try {
                if (result != null) {
                    //result=	Html.fromHtml(result).toString();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultsObject = jsonObject.getJSONArray("results");
                    JSONObject formattedAddress = (JSONObject) resultsObject.get(0);
                    String formatted_address = formattedAddress.getString("formatted_address");

                    Log.e("formatted Adss from>>", formatted_address);
                    text_address.setText(formatted_address);

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }
}
