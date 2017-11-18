package com.app.veraxe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.app.veraxe.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZoomImageAcivity extends AppCompatActivity {

    SubsamplingScaleImageView image_zoom;
    ImageView image_cross;
    private Context context;
    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_acivity);
        context = this;
        image_zoom = (SubsamplingScaleImageView) findViewById(R.id.image_zoom);
        image_cross = (ImageView) findViewById(R.id.image_cross);

        image_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent1 = getIntent();
        String filename = intent1.getStringExtra("filename");
        String imageurl = intent1.getStringExtra("imageurl");

        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/Veraxe/" + filename);

        if (myFile.exists()) {
            image_zoom.setImage(ImageSource.uri(extStore.getAbsolutePath() + "/Veraxe/" + filename));
        } else {
            loadImage(imageurl);
            Intent intent = new Intent(context, DownLoadFile.class);
            intent.putExtra(DownLoadFile.FILENAME, filename);
            intent.putExtra(DownLoadFile.URL,
                    imageurl);
            context.startService(intent);
            //  new ImageLoad().execute(imageurl);
        }
    }

    private void loadImage(String url) {
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                image_zoom.setImage(ImageSource.bitmap(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(context)
                .load(url)
                .into(mTarget);
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
