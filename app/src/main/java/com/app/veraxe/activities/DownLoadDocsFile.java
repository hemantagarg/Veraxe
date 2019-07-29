package com.app.veraxe.activities;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
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
public class DownLoadDocsFile extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String FILETYPE = "filetype";
    Context context;
    public static final String NOTIFICATION = "service receiver";
    private String fileType = "";

    public DownLoadDocsFile() {
        super("DownLoadFile");
        context = this;
    }

    // Will be called asynchronously by OS.
    @Override
    protected void onHandleIntent(Intent intent) {

        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        fileType = intent.getStringExtra(FILETYPE);
        Log.e("File path", urlPath);
        // new AsyncDownloadFile(context, urlPath, fileName);
        new DownloadFileFromURL(context, urlPath, fileName);
    }

    private void publishResults(String outputPath, int result) {
        showNotification(outputPath);
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        // intent.setDataAndType(Uri.fromFile(new File(outputPath)),fileType);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public void showNotification(String FILEPATH) {
        try {
            Log.e("Local file path", FILEPATH);
            File file = new File(FILEPATH);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = "";
            if (fileType != null)
                type = fileType;
            else {
                type = map.getMimeTypeFromExtension(ext);
                if (type == null)
                    type = "*/*";
            }
            Log.e("file type", type);
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = FileProvider.getUriForFile(DownLoadDocsFile.this, BuildConfig.APPLICATION_ID + ".provider", file);

            notificationIntent.setDataAndType(data, type);
            notificationIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(notificationIntent);*/

            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
         /*   PendingIntent pIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );*/
            String title = context.getString(R.string.app_name);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        String urlPath = "";
        String fileName = "";

        public DownloadFileFromURL(Context context, String url, String fileName) {
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
            String filePath = "";
            try {
                java.net.URL url = new URL(urlPath);
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

                OutputStream output = new FileOutputStream(file);
                // OutputStream output = new FileOutputStream(getFilesDir().toString() + "/" + fileName);

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
                filePath = file.getPath();
                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return filePath;
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
            //  String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + fileName;
            publishResults(file_url, Activity.RESULT_OK);
            // setting downloaded into image view
        }
    }
}