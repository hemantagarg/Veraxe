package com.app.veraxe.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.app.veraxe.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZoomImageAcivity extends AppCompatActivity {


    SubsamplingScaleImageView image_zoom;
    ImageView image_cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_acivity);

        image_zoom = (SubsamplingScaleImageView) findViewById(R.id.image_zoom);
        image_cross = (ImageView) findViewById(R.id.image_cross);

        image_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new ImageLoad().execute(getIntent().getStringExtra("imageurl"));
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
