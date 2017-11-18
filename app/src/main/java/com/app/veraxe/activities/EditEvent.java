package com.app.veraxe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.veraxe.R;
import com.app.veraxe.adapter.AdapterEventEditPhotoDetail;
import com.app.veraxe.adapter.AdapterEventEditVideoDetail;
import com.app.veraxe.asyncTask.CommonAsyncTaskHashmap;
import com.app.veraxe.interfaces.ApiResponse;
import com.app.veraxe.interfaces.OnCustomItemClicListener;
import com.app.veraxe.model.ModelStudent;
import com.app.veraxe.utils.AppConstants;
import com.app.veraxe.utils.AppUtils;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class EditEvent extends AppCompatActivity implements ApiResponse, OnCustomItemClicListener, DatePickerDialog.OnDateSetListener {

    private String eventId = "";
    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private TextView text_class;
    private TextView text_fromdate, text_section, text_imagecount, text_videocount, text_todate, text_uploadvideo, text_uploadphoto;
    private EditText text_event_title, text_aboutevent;
    private Button button_create;
    private ArrayList<Integer> selectedSection;
    private ArrayList<File> selectedVideo;
    private String selectedSecionId = "", selectSections = "";
    private ArrayList<String> imagesPath = new ArrayList<>();
    private StringBuffer stringBuffer = null;
    private DatePickerDialog dpdEnddate;
    private int REQUEST_TAKE_GALLERY_VIDEO = 7;
    private File selectedVideofile;
    private String selectedimagespath = "";
    private ArrayList<String> addedImagesId;
    private ArrayList<String> addedVideosId;
    private CheckBox check_all, check_father, check_mother, check_notification;
    private RecyclerView mRecyclerView, recycler_view_video;
    private AdapterEventEditPhotoDetail adapterEventPhotoDetail;
    private AdapterEventEditVideoDetail adapterEventVideoDetail;
    ModelStudent itemList;
    ArrayList<ModelStudent> arrayList;
    ArrayList<ModelStudent> videoList;
    private int deletedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        context = this;
        init();

        Intent in = getIntent();
        eventId = in.getStringExtra("eventId");

        Calendar now = Calendar.getInstance();
        dpdEnddate = DatePickerDialog.newInstance(EditEvent.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        setListener();
        eventList();
    }

    public void eventList() {
        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("eventid", eventId);
            hm.put("authkey", AppConstants.AUTHKEY);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.event_detail);
            new CommonAsyncTaskHashmap(1, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
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

        addedImagesId = new ArrayList<>();
        arrayList = new ArrayList<>();
        videoList = new ArrayList<>();
        addedVideosId = new ArrayList<>();
        selectedVideo = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Edit Event");
        selectedSection = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view_video = (RecyclerView) findViewById(R.id.recycler_view_video);
        GridLayoutManager gridlayoutManager = new GridLayoutManager(context, 2);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridlayoutManager);

        GridLayoutManager gridlayoutManagervideo = new GridLayoutManager(context, 2);
        gridlayoutManagervideo.setOrientation(GridLayoutManager.VERTICAL);
        recycler_view_video.setLayoutManager(gridlayoutManagervideo);
        text_event_title = (EditText) findViewById(R.id.text_event_title);
        text_aboutevent = (EditText) findViewById(R.id.text_aboutevent);
        text_section = (TextView) findViewById(R.id.text_section);
        text_class = (TextView) findViewById(R.id.text_class);
        text_fromdate = (TextView) findViewById(R.id.text_fromdate);
        text_todate = (TextView) findViewById(R.id.text_todate);
        text_uploadphoto = (TextView) findViewById(R.id.text_uploadphoto);
        text_uploadvideo = (TextView) findViewById(R.id.text_uploadvideo);
        text_videocount = (TextView) findViewById(R.id.text_videocount);
        text_imagecount = (TextView) findViewById(R.id.text_imagecount);
        button_create = (Button) findViewById(R.id.button_create);
        check_all = (CheckBox) findViewById(R.id.check_all);
        check_father = (CheckBox) findViewById(R.id.check_father);
        check_mother = (CheckBox) findViewById(R.id.check_mother);
        check_notification = (CheckBox) findViewById(R.id.check_notification);

    }


    public void uploadPhoto() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            //file[1]=filename2

            ArrayList<File> imagesFilelist = new ArrayList<>();
            if (!selectedimagespath.equalsIgnoreCase("")) {

                for (int i = 0; i < imagesPath.size(); i++) {

                    int j = i + 1;
                    File file = new File(imagesPath.get(i));
                    imagesFilelist.add(file);
                    hm.put("file[" + j + "]", file);
                }
            }

            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userid", AppUtils.getUserId(context));

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.upload_eventphoto);
            new CommonAsyncTaskHashmap(5, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadVideo() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();

            //file[1]=filename2
            for (int i = 0; i < selectedVideo.size(); i++) {

                int j = i + 1;
                hm.put("file[1]", selectedVideo.get(i));
            }


            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("schoolid", AppUtils.getSchoolId(context));
            hm.put("userid", AppUtils.getUserId(context));


            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.upload_eventvideo);
            new CommonAsyncTaskHashmap(6, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    public void deletePhoto(String id) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("id", id);
            hm.put("authkey", AppConstants.AUTHKEY);
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.delete_event_photo);
            new CommonAsyncTaskHashmap(3, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteVideo(String id) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("id", id);
            hm.put("authkey", AppConstants.AUTHKEY);
            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.delete_event_video);
            new CommonAsyncTaskHashmap(4, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void createEvent() {
        if (AppUtils.isNetworkAvailable(context)) {

            String imgesId = "", videoId = "";

            for (int i = 0; i < addedImagesId.size(); i++) {
                if (imgesId.equalsIgnoreCase("")) {
                    imgesId = addedImagesId.get(i);
                } else {
                    imgesId = imgesId + "," + addedImagesId.get(i);
                }
            }
            for (int j = 0; j < arrayList.size(); j++) {
                if (imgesId.equalsIgnoreCase("")) {
                    imgesId = arrayList.get(j).getId();
                } else {
                    imgesId = imgesId + "," + arrayList.get(j).getId();
                }

                Log.e("imgesId", "*" + imgesId);
            }
            for (int i1 = 0; i1 < addedVideosId.size(); i1++) {
                if (videoId.equalsIgnoreCase("")) {
                    videoId = addedVideosId.get(i1);
                } else {
                    videoId = videoId + "," + addedVideosId.get(i1);
                }
            }

            for (int j1 = 0; j1 < videoList.size(); j1++) {
                if (videoId.equalsIgnoreCase("")) {
                    videoId = videoList.get(j1).getId();
                } else {
                    videoId = videoId + "," + videoList.get(j1).getId();
                }
            }
            String sendnotification = "0";
            if (check_notification.isChecked()) {
                sendnotification = "1";
            } else {
                sendnotification = "0";
            }

            HashMap<String, Object> hm = new HashMap<>();

            hm.put("authkey", AppConstants.AUTHKEY);
            hm.put("eventid", eventId);
            hm.put("title", text_event_title.getText().toString());
            hm.put("description", text_aboutevent.getText().toString());
            hm.put("start_date", text_fromdate.getText().toString());
            hm.put("end_date", text_todate.getText().toString());
            hm.put("videos", videoId);
            hm.put("photos", imgesId);
            hm.put("sendnotification", sendnotification);

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.editevent);
            new CommonAsyncTaskHashmap(11, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

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

                finish();

            }
        });

        text_uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);

            }
        });
        text_uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });
        text_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidLoginDetails()) {

                    createEvent();
                }

            }
        });

        text_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();

                DatePickerDialog dpd = DatePickerDialog.newInstance(EditEvent.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //   dpd.setMinDate(now);

                dpd.show(getFragmentManager(), "startdate");

            }
        });

        text_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (text_fromdate.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, getResources().getString(R.string.enterstart_date), Toast.LENGTH_SHORT).show();
                } else {

                    dpdEnddate.show(getFragmentManager(), "enddate");
                }
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = data.getData();
                selectedVideo = new ArrayList<>();
                selectedVideofile = new File(getRealPathFromURI(selectedImageUri, context));

                selectedVideo.add(selectedVideofile);

                Log.e("selectedImageUri", "**" + selectedImageUri + "*" + selectedVideofile);

                uploadVideo();
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {

                    Log.e("selectedImagePath", "**" + selectedImagePath);

                }
            }
        }
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images

            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            stringBuffer = new StringBuffer();
            selectedimagespath = "";
            imagesPath = new ArrayList<>();
            for (int i = 0, l = images.size(); i < l; i++) {

                imagesPath.add(images.get(i).path);
                stringBuffer.append(images.get(i).path + ",");
            }

            selectedimagespath = Arrays.deepToString(imagesPath.toArray());
            Log.e("selectedImagesPath", "*" + selectedimagespath);
            text_imagecount.setText(imagesPath.size() + " Images uploaded");
            uploadPhoto();
            //  text_attach_images.setText("Images attached succesfully");
        }
    }


    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private boolean isValidLoginDetails() {
        boolean isValidLoginDetails = true;

        if (!text_event_title.getText().toString().equalsIgnoreCase("") && !text_fromdate.getText().toString().equalsIgnoreCase("")
                && !text_todate.getText().toString().equalsIgnoreCase("")) {

            isValidLoginDetails = true;

        } else {
            if (text_fromdate.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enterstart_date, Toast.LENGTH_SHORT).show();
            } else if (text_todate.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enterend_date, Toast.LENGTH_SHORT).show();
            } else if (text_event_title.getText().toString().equalsIgnoreCase("")) {
                isValidLoginDetails = false;
                Toast.makeText(getApplicationContext(), R.string.enterEventTitle, Toast.LENGTH_SHORT).show();
            }
        }

        return isValidLoginDetails;
    }

    @Override
    public void getResponse(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    JSONObject result = response.getJSONObject("result");
                    text_event_title.setText(result.getString("title"));
                    text_aboutevent.setText(result.getString("description"));
                    text_fromdate.setText(result.getString("start_date"));
                    text_todate.setText(result.getString("end_date"));

                    JSONArray photos = result.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {

                        JSONObject jo = photos.getJSONObject(i);
                        itemList = new ModelStudent();
                        itemList.setId(jo.getString("id"));
                        itemList.setFilename(jo.getString("filename"));
                        itemList.setUrl(jo.getString("url"));
                        itemList.setRowType(1);
                        arrayList.add(itemList);

                    }
                    adapterEventPhotoDetail = new AdapterEventEditPhotoDetail(context, this, arrayList);
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
                        adapterEventVideoDetail = new AdapterEventEditVideoDetail(context, this, videoList);
                        recycler_view_video.setAdapter(adapterEventVideoDetail);
                    }
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 3) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    arrayList.remove(deletedPosition);
                    adapterEventPhotoDetail.notifyItemRemoved(deletedPosition);
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    videoList.remove(deletedPosition);
                    adapterEventVideoDetail.notifyItemRemoved(deletedPosition);
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 5) {
                if (response.getString("success").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("files");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        addedImagesId.add(jo.getString("id"));

                    }

                }
            } else if (method == 6) {
                if (response.getString("success").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("files");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        addedVideosId.add(jo.getString("id"));
                    }
                    text_videocount.setText(addedVideosId.size() + " Video uploaded");
                    if (addedVideosId.size() >= 1) {
                        text_uploadvideo.setText("Upload More Video");
                    }
                }
            } else if (method == 11) {
                if (response.getString("response").equalsIgnoreCase("1")) {
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

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
        if (flag == 4) {
            // delete photo
            deletedPosition = position;
            deletePhoto(arrayList.get(position).getId());

        } else if (flag == 5) {
            // delete video
            deletedPosition = position;
            deleteVideo(videoList.get(position).getId());
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int month = monthOfYear + 1;
        if (view.getTag().equalsIgnoreCase("startdate")) {
            text_fromdate.setText(year + "-" + month + "-" + dayOfMonth);
            text_todate.setText("");
            try {
                String date = dayOfMonth + "-" + month + "-" + year;
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(df.parse(date));
                dpdEnddate.setMinDate(cal);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            text_todate.setText(year + "-" + month + "-" + dayOfMonth);

        }
    }
}
