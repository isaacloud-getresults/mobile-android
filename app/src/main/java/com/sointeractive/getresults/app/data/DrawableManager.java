package com.sointeractive.getresults.app.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class DrawableManager {
    private final Map<String, Drawable> drawableMap;

    public DrawableManager() {
        drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(final String urlString) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        Log.d(getClass().getSimpleName(), "image url:" + urlString);
        try {
            final InputStream is = fetch(urlString);
            final Drawable drawable = Drawable.createFromStream(is, "src");


            if (drawable != null) {

                Log.d(getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
                Log.w(getClass().getSimpleName(), "could not get thumbnail");
            }

            return drawable;
        } catch (final MalformedURLException e) {
            Log.e(getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (final IOException e) {
            Log.e(getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView, final boolean process) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message message) {
                if (process) {
                    final BitmapFactory.Options bf = new BitmapFactory.Options();
                    bf.inSampleSize = 4;
                    final BitmapDrawable bd = (BitmapDrawable) message.obj;
                    final Bitmap imageBitmap = bd.getBitmap();
                    Log.d("Bitmap", "width: " + imageBitmap.getWidth() + " " + "height: " + imageBitmap.getHeight());
//            new SendToServerTask().execute(picturePath);
                    imageView.setImageBitmap(ImageHelper.getAvatar(imageBitmap, null));
                    drawableMap.put(urlString, imageView.getDrawable());
                } else
                    imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                final Drawable drawable = fetchDrawable(urlString);
                final Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private InputStream fetch(final String urlString) throws MalformedURLException, IOException {
        Log.d("InputStream", "Fetch");
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpGet request = new HttpGet(urlString);
        final HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}