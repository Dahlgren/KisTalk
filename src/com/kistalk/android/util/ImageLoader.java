package com.kistalk.android.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

/**
 * An background image loader. If the image to be loaded is present in the cache, 
 * it is set immediately on the given view. Otherwise, a thread from a thread pool
 * will be used to download the image in the background and set the image on the view
 * as soon as it completes.
 * 
 */
public class ImageLoader implements Runnable {

	public static final int HANDLER_MESSAGE_ID = 0;
	public static final String BITMAP_IMAGE = "bitmap_image";
	public static final String IMAGE_URL = "image_url";

	private static final String LOG_TAG = "ImageLoader";
	// the default thread pool size
	private static final int DEFAULT_POOL_SIZE = 3;
	// expire images after a day
	// TODO: this currently only affects the in-memory cache, so it's quite
	// pointless
	private static final int DEFAULT_RETRY_SLEEP_TIME = 1000;
	private static final int DEFAULT_NUM_RETRIES = 3;

	private static ThreadPoolExecutor executor;
	private static ImageCache imageCache;
	private static int numRetries = DEFAULT_NUM_RETRIES;

	/**
	 * @param numThreads
	 *            the maximum number of threads that will be started to download
	 *            images in parallel
	 */
	public static void setThreadPoolSize(int numThreads) {
		executor.setMaximumPoolSize(numThreads);
	}

	/**
	 * @param numAttempts
	 *            how often the image loader should retry the image download if
	 *            network connection fails
	 */
	public static void setMaxDownloadAttempts(int numAttempts) {
		ImageLoader.numRetries = numAttempts;
	}

	/**
	 * This method must be called before any other method is invoked on this
	 * class. Please note that when using ImageLoader as part of
	 * {@link WebImageView} or {@link WebGalleryAdapter}, then there is no need
	 * to call this method, since those classes will already do that for you.
	 * This method is idempotent. You may call it multiple times without any
	 * side effects.
	 * 
	 * @param context
	 *            the current context
	 */
	public static synchronized void initialize() {
		if (executor == null) {
			executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(DEFAULT_POOL_SIZE);
		}
		if (imageCache == null) {
			imageCache = new ImageCache(25);
		}
	}

	private String imageUrl;

	private ImageLoaderHandler handler;

	private ImageLoader(String imageUrl, ImageLoaderHandler handler) {
		this.imageUrl = imageUrl;
		this.handler = handler;
	}

	/**
	 * Triggers the image loader for the given image and view. The image loading
	 * will be performed concurrently to the UI main thread, using a fixed size
	 * thread pool. The loaded image will be posted back to the given ImageView
	 * upon completion.
	 * 
	 * @param imageUrl
	 *            the URL of the image to download
	 * @param imageView
	 *            the ImageView which should be updated with the new image
	 */
	public static void start(String imageUrl, ImageView imageView) {
		start(imageUrl, imageView, new ImageLoaderHandler(imageView, imageUrl),
				null, null);
	}

	/**
	 * Triggers the image loader for the given image and view and sets a dummy
	 * image while waiting for the download to finish. The image loading will be
	 * performed concurrently to the UI main thread, using a fixed size thread
	 * pool. The loaded image will be posted back to the given ImageView upon
	 * completion.
	 * 
	 * @param imageUrl
	 *            the URL of the image to download
	 * @param imageView
	 *            the ImageView which should be updated with the new image
	 * @param dummyDrawable
	 *            the Drawable set to the ImageView while waiting for the image
	 *            to be downloaded
	 * @param errorDrawable
	 *            the Drawable set to the ImageView if a download error occurs
	 */
	public static void start(String imageUrl, ImageView imageView,
			Drawable dummyDrawable, Drawable errorDrawable) {
		start(imageUrl, imageView, new ImageLoaderHandler(imageView, imageUrl,
				errorDrawable), dummyDrawable, errorDrawable);
	}

	/**
	 * Triggers the image loader for the given image and handler. The image
	 * loading will be performed concurrently to the UI main thread, using a
	 * fixed size thread pool. The loaded image will not be automatically posted
	 * to an ImageView; instead, you can pass a custom
	 * {@link ImageLoaderHandler} and handle the loaded image yourself (e.g.
	 * cache it for later use).
	 * 
	 * @param imageUrl
	 *            the URL of the image to download
	 * @param handler
	 *            the handler which is used to handle the downloaded image
	 */
	public static void start(String imageUrl, ImageLoaderHandler handler) {
		start(imageUrl, handler.getImageView(), handler, null, null);
	}

	private static void start(String imageUrl, ImageView imageView,
			ImageLoaderHandler handler, Drawable dummyDrawable,
			Drawable errorDrawable) {

		/* If the programmer have forgotten to use the initialize method first */
		if (executor == null || imageCache == null)
			initialize(); // To initialize the class
		
		if (imageView != null) {
			if (imageUrl == null) {
				// In a ListView views are reused, so we must be sure to remove
				// the tag that could
				// have been set to the ImageView to prevent that the wrong
				// image is set.
				imageView.setTag(null);
				imageView.setImageDrawable(dummyDrawable);
				return;
			}
			String oldImageUrl = (String) imageView.getTag();
			if (imageUrl.equals(oldImageUrl)) {
				// nothing to do
				return;
			} else {
				// Set the dummy image while waiting for the actual image to be
				// downloaded.
				imageView.setImageDrawable(dummyDrawable);
				imageView.setTag(imageUrl);
			}
		}

		if (imageCache.containsInCache(imageUrl)) {
			// do not go through message passing, handle directly instead
			handler.handleImageLoaded(imageCache.getBitmap(imageUrl), null);
		} else {
			executor.execute(new ImageLoader(imageUrl, handler));
		}
	}

	/**
	 * Clears the cache. Perhaps should be called in the event of
	 * {@link android.app.Application#onLowMemory()}.
	 */
	public static void clearCache() {
		imageCache.clear();
	}

	/**
	 * Returns the image cache whichs backs this image loader.
	 * 
	 * @return the {@link ImageCache}
	 */
	public static ImageCache getImageCache() {
		return imageCache;
	}

	/**
	 * The run method for a worker thread. It will first query the image cache
	 * and if it's a miss, then the worker thread will download the image from
	 * the Web.
	 */
	public void run() {
		Bitmap bitmap = imageCache.getBitmap(imageUrl);

		if (bitmap == null) {
			bitmap = downloadImage();
		}

		bitmap = imageCache.getBitmap(imageUrl);
		notifyImageLoaded(imageUrl, bitmap);
	}

	private Bitmap downloadImage() {
		int timesTried = 0;

		while (timesTried < numRetries) {
			try {
				Bitmap imageData = retrieveImageData();

				if (imageData != null) {
					imageCache.putInCache(imageUrl, imageData);
				} else {
					break;
				}

				return imageData;

			} catch (Throwable e) {
				Log.w(LOG_TAG, "download for " + imageUrl + " failed (attempt "
						+ timesTried + ")");
				e.printStackTrace();
				SystemClock.sleep(DEFAULT_RETRY_SLEEP_TIME);
				timesTried++;
			}
		}

		return null;
	}

	private Bitmap retrieveImageData() throws IOException {
		URL url = new URL(imageUrl);

		/* Sets up and bidirectional transfer */
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		/* Decodes the stream to a bitmap */
		BufferedInputStream istream = new BufferedInputStream(
				connection.getInputStream());

		Bitmap image = BitmapFactory.decodeStream(istream);

		/* Clean up */
		istream.close();
		connection.disconnect();

		return image;
	}

	public void notifyImageLoaded(String url, Bitmap bitmap) {
		Message message = new Message();
		message.what = HANDLER_MESSAGE_ID;
		Bundle data = new Bundle();
		data.putString(IMAGE_URL, url);
		Bitmap image = bitmap;
		data.putParcelable(BITMAP_IMAGE, image);
		message.setData(data);

		handler.sendMessage(message);
	}
}
