package com.app.veraxe.activities;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.app.veraxe.BuildConfig;
import com.app.veraxe.R;
import com.app.veraxe.utils.AppConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by admin on 13-05-2016.
 */
public class DownLoadVideoFile extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    Context context;
    static String urlPath = "", filename = "";
    public static final String NOTIFICATION = "service receiver";

    public DownLoadVideoFile() {

        super("DownLoadFile");
        context = this;

    }

    // Will be called asynchronously by OS.
    @Override
    protected void onHandleIntent(Intent intent) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        this.urlPath = urlPath;
        this.filename = filename;
        Log.e("File path", urlPath);
        // new AsyncDownloadFile(context, urlPath, fileName);
        new DownloadFileFromURL(context, urlPath, fileName);
    }

    private void publishResults(String outputPath, int result) {
        showNotification(outputPath);
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public void showNotification(String FILEPATH) {

        Log.e("Local file path", FILEPATH);
        File file = new File(FILEPATH);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null)
            type = "*/*";

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);

        Uri data = FileProvider.getUriForFile(DownLoadVideoFile.this, BuildConfig.APPLICATION_ID + ".provider", file);

        notificationIntent.setDataAndType(data, type);
        notificationIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
        String CHANNEL_ID = "channel_veraxe";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence name = "Veraxe";// The user-visible name of the channel.
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Veraxe Notification")
                .setContentText("Download completed")
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pIntent);
        Random r = new Random();
        int when = r.nextInt(1000);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(when, notification.build());
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        String urlPath = "";
        String fileName = "";

        public DownloadFileFromURL(Context context, String url, String fileName) {
            // TODO Auto-generated constructor stub
            this.urlPath = url;
            this.fileName = fileName;
            this.execute(new String[]{});
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            OutputStream output;
            try {
                URL url = new URL(urlPath);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath()
                        + AppConstants.VERAXE_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                    Log.d("directory", "veraxe created for first time");
                }
                File file = new File(dir, fileName);

                output = new FileOutputStream(file);
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                addVideo(file.getAbsolutePath(), context);
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            File filepath = Environment.getExternalStorageDirectory();
            String imagePath = filepath.getAbsolutePath()
                    + AppConstants.VERAXE_PATH + fileName;
            Log.e("imagePath", "onPostExecute: " + imagePath);
            publishResults(imagePath, Activity.RESULT_OK);
            // setting downloaded into image view
        }
    }

    public static void addVideo(final String filePath, final Context context) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

}