package com.app.veraxe.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.activities.DownLoadFile;
import com.app.veraxe.activities.PlayVideo;
import com.app.veraxe.activities.ZoomImageAcivity;
import com.app.veraxe.adapter.AdapterEventPhotoDetail;
import com.app.veraxe.adapter.AdapterEventVideoDetail;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.ConnectionDetector;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppUtils;
import com.app.veraxe.utils.Constant;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class StudentEventDetail extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {


    Context context;
    ModelStudent itemList;
    ArrayList<ModelStudent> arrayList;
    ArrayList<ModelStudent> videoList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    Toolbar toolbar;
    String eventId = "";
    TextView text_detail, text_date, text_event_name, text_eventtitle;
    private BroadcastReceiver broadcastReceiver;
    RecyclerView mRecyclerView, recycler_view_video;
    AdapterEventPhotoDetail adapterEventPhotoDetail;
    AdapterEventVideoDetail adapterEventVideoDetail;
    ImageView background_image;
    RelativeLayout rl_zoomimage;
    SubsamplingScaleImageView image_zoom;
    ImageView img_back;
    boolean isImageVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);

        context = this;
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();
        videoList = new ArrayList<>();

        Intent in = getIntent();
        eventId = in.getStringExtra("eventId");

        setListener();
        eventList();

    }

    @Override
    protected void onResume() {
        super.onResume();

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
        text_eventtitle = (TextView) findViewById(R.id.text_eventtitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        rl_zoomimage = (RelativeLayout) findViewById(R.id.rl_zoomimage);
        image_zoom = (SubsamplingScaleImageView) findViewById(R.id.image_zoom);
        img_back = (ImageView) findViewById(R.id.img_back);
        background_image = (ImageView) findViewById(R.id.background_image);
        text_detail = (TextView) findViewById(R.id.text_detail);
        text_date = (TextView) findViewById(R.id.text_date);
        text_event_name = (TextView) findViewById(R.id.text_event_name);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view_video = (RecyclerView) findViewById(R.id.recycler_view_video);
        GridLayoutManager gridlayoutManager = new GridLayoutManager(context, 2);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridlayoutManager);

        GridLayoutManager gridlayoutManagervideo = new GridLayoutManager(context, 2);
        gridlayoutManagervideo.setOrientation(GridLayoutManager.VERTICAL);
        recycler_view_video.setLayoutManager(gridlayoutManagervideo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rl_zoomimage.getVisibility() == View.VISIBLE) {
                    rl_zoomimage.setVisibility(View.GONE);
                    return;
                } else {
                    finish();
                }

            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });


    }


    public void eventList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("eventid", eventId);
            hm.put("authkey", Constant.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.student_events_detail);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClickListener(int position, int flag) {
        if (flag == 3) {

            Intent in = new Intent(context, PlayVideo.class);
            in.putExtra("videoPath", videoList.get(position).getUrl());
            startActivity(in);

        } else if (flag == 2) {

            Intent intent = new Intent(context, ZoomImageAcivity.class);
            try {
                intent.putExtra("imageurl", arrayList.get(position).getOrgiginalImage());
                startActivity(intent);
            } catch (Exception e) {
                Log.w(getClass().toString(), e);
            }
        } else if (flag == 4) {

            Intent intent = new Intent(context, DownLoadFile.class);
            intent.putExtra(DownLoadFile.FILENAME, arrayList.get(position).getFilename());
            intent.putExtra(DownLoadFile.URL,
                    arrayList.get(position).getOrgiginalImage());
            context.startService(intent);

            Toast.makeText(context, "Your file download is in progress", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rl_zoomimage.getVisibility() == View.VISIBLE) {
                rl_zoomimage.setVisibility(View.GONE);
                try {
                    image_zoom.destroyDrawingCache();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject result = response.getJSONObject("result");
                    text_event_name.setText(result.getString("title"));
                    text_eventtitle.setText(result.getString("title"));
                    text_detail.setText(result.getString("description"));
                    text_date.setText(result.getString("start_date") + " - " + result.getString("end_date"));

                    JSONArray photos = result.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {

                        JSONObject jo = photos.getJSONObject(i);
                        itemList = new ModelStudent();
                        itemList.setId(jo.getString("id"));
                        itemList.setOrgiginalImage(jo.getString("org"));
                        itemList.setFilename(jo.getString("filename"));
                        itemList.setUrl(jo.getString("url"));
                        itemList.setRowType(1);
                        arrayList.add(itemList);
                    }
                    adapterEventPhotoDetail = new AdapterEventPhotoDetail(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterEventPhotoDetail);

                    JSONArray videos = result.getJSONArray("videos");
                    if (videos.length() > 0) {
                        for (int i = 0; i < videos.length(); i++) {

                            JSONObject jo = videos.getJSONObject(i);
                            itemList = new ModelStudent();
                            itemList.setId(jo.getString("id"));
                            itemList.setFilename(jo.getString("filename"));
                            itemList.setUrl(jo.getString("url"));
                            itemList.setRowType(1);
                            //  itemList.setThubnail(retriveVideoFrameFromVideo(jo.getString("url")));
                            //  Log.e("thumbnail", "*" + retriveVideoFrameFromVideo(jo.getString("url")));

                            videoList.add(itemList);
                        }
                        adapterEventVideoDetail = new AdapterEventVideoDetail(context, this, videoList);
                        recycler_view_video.setAdapter(adapterEventVideoDetail);

                        Picasso.with(context).load(result.getString("banner")).into(background_image);

                    }
//                    "result": {
//                        "id": 1,
//                                "title": "Diwali Function",
//                                "description": "Dewali .....",
//                                "start_date": "29/10/2016",
//                                "end_date": "29/10/2016",
//                                "photos": [
//                        {
//                            "id": "10",
//                                "filename": "982fed236637cb9d698ec4e73f6b3b74.jpg",
//                                "url": " http://manage.veraxe.com /uploads/school/event/photo/220/982fed236637cb9d698ec4e73f6b3b74.jpg"
//                        }
//                        ],
//                        "videos": []
//                    }


                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    class ImageLoad extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Bitmap feed) {
            image_zoom.setImage(ImageSource.bitmap(feed));
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

}
