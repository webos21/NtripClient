package com.gmail.webos21.android.ild;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    public static final boolean DEBUG = true;

    private static final String TAG = ImageLoader.class.getSimpleName();
    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();
    // FileCache member
    FileCache fileCache;
    ExecutorService executorService;
    // handler to display images in UI thread
    Handler handler = new Handler();
    // Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private ImageLoadCompleteListener imageLdCL;
    // default image show in list (Before online image download)
    private int stub_id = -1;

    public ImageLoader(Context context, int stubId) {
        fileCache = new FileCache(context);
        this.stub_id = stubId;

        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);

    }

    public void setImageLoadCompleteListener(ImageLoadCompleteListener l) {
        this.imageLdCL = l;
    }

    public void DisplayImage(String url, ImageView imageView) {
        // Store image and url in Map
        imageViews.put(imageView, url);

        // Check image is stored in MemoryCache Map or not (see
        // MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        } else {
            // queue Photo to download from url
            queuePhoto(url, imageView);

            // Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // from SD cache
        // CHECK : if trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        }

        // Download image file from web
        try {

            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(f);

            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            final int buffer_size = 1024;
            try {
                byte[] bytes = new byte[buffer_size];
                for (; ; ) {
                    // Read byte from input stream
                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1) {
                        break;
                    }
                    // Write byte from output stream
                    os.write(bytes, 0, count);
                }
            } catch (Exception ex) {
                if (DEBUG) {
                    // ex.printStackTrace();
                    Log.i(TAG, "Cannot write image : " + url);
                }
            }

            os.close();
            conn.disconnect();

            // Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(f);

            return bitmap;

        } catch (Throwable ex) {
            if (DEBUG) {
                // ex.printStackTrace();
                Log.i(TAG, "Cannot load image : " + url);
            }
            if (ex instanceof OutOfMemoryError) {
                memoryCache.clear();
            }
            return null;
        }
    }

    private int getScale(File f) {
        int scale = 1;
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            final int REQUIRED_SIZE = 540;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            return scale;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scale;
    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        if (!f.exists()) {
            if (DEBUG) {
                Log.d(TAG, "File not found : " + f.toString());
            }
            return null;
        }
        if (f.length() == 0) {
            if (DEBUG) {
                Log.d(TAG, "File Length is 0 : " + f.toString());
            }
            return null;
        }
        try {
            int scale = getScale(f);
            if (DEBUG) {
                Log.d(TAG, "!!!!!!!!!!!! Scale : " + scale);
            }
            // decode with current scale values
            // BitmapFactory.Options o2 = new BitmapFactory.Options();
            // o2.inSampleSize = scale;

            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, null);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                Log.d(TAG, "File not found : " + f.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        // Check url is already exist in imageViews MAP
        if (tag == null || !tag.equals(photoToLoad.url)) {
            return true;
        }
        return false;
    }

    public void clearCache() {
        // Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

    public interface ImageLoadCompleteListener {
        public void onImageLoaded(ImageView v, Bitmap b);
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                // Check if image already downloaded
                if (imageViewReused(photoToLoad)) {
                    return;
                }

                // download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);

                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);

                if (imageViewReused(photoToLoad)) {
                    return;
                }

                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

                // Causes the Runnable bd (BitmapDisplayer) to be added to the
                // message queue.
                // The runnable will be run on the thread to which this handler
                // is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }

            // Show bitmap on UI
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                if (imageLdCL != null) {
                    imageLdCL.onImageLoaded(photoToLoad.imageView, bitmap);
                }
            } else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

}
