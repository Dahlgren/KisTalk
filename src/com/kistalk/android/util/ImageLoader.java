package com.kistalk.android.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.kistalk.android.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * An background image loader. If the image to be loaded is present in the
 * cache, it is set immediately on the given view. Otherwise, a thread from a
 * thread pool will be used to download the image in the background and set the
 * image on the view as soon as it completes.
 * 
 */
public class ImageLoader implements Runnable, Constant {

	private static final String LOG_TAG = "util.KisTalk.ImageLoader";
	// the default thread pool size
	private static final int DEFAULT_POOL_SIZE = 3;

	private static ThreadPoolExecutor executor;
	private static ImageCache imageCache;
	private static HashMap<String, ImageLoaderHandler> activeHandlers;

	private static boolean initialized = false;

	/**
	 * @param numThreads
	 *            the maximum number of threads that will be started to download
	 *            images in parallel
	 */
	public static void setThreadPoolSize(int numThreads) {
		executor.setMaximumPoolSize(numThreads);
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
	private static synchronized void initialize() {
		if (executor == null)
			executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(DEFAULT_POOL_SIZE);
		if (imageCache == null)
			imageCache = new ImageCache();
		if (activeHandlers == null)
			activeHandlers = new HashMap<String, ImageLoaderHandler>();
		initialized = true;
	}

	/**
	 * Triggers the image loader for the given image and ImageView. The image
	 * loading will be performed concurrently to the UI main thread, using a
	 * fixed size thread pool. The loaded image will be posted upon completion.
	 * 
	 * @param imageUrl
	 *            the URL of the image to download
	 * @param feedItem
	 *            the feedItem which should be updated with the new image
	 */

	public static void start(String imageUrl, final ImageView imageView) {

		if (imageView == null || imageUrl == null) {
			Log.w(LOG_TAG, "parameter imageView and/or imageUrl are null");
			return;
		}

		if (!initialized)
			initialize();

		if (activeHandlers.containsKey(imageUrl))
			activeHandlers.get(imageUrl).addViews(imageView);
		else
			executor.execute(new ImageLoader(imageUrl, new ImageLoaderHandler(
					imageView)));
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

	private String imageUrl;
	private ImageLoaderHandler handler;

	private ImageLoader(String imageUrl, ImageLoaderHandler handler) {
		this.imageUrl = imageUrl;
		this.handler = handler;
		activeHandlers.put(imageUrl, handler);
	}

	/**
	 * The run method for a worker thread. It will first query the image cache
	 * and if it's a miss, then the worker thread will download the image from
	 * the Web.
	 */
	public void run() {
		Bundle bundle = new Bundle();
		if (!imageCache.contains(imageUrl)) {
			if (downloadImage())
				bundle.putParcelable(KEY_BITMAP, imageCache.getBitmap(imageUrl));
			else
				bundle.putInt(KEY_RESOURCE, R.drawable.failed_to_download);
		} else
			bundle.putParcelable(KEY_BITMAP, imageCache.getBitmap(imageUrl));
		Message msg = new Message();
		msg.setData(bundle);

		activeHandlers.remove(imageUrl);
		handler.sendMessage(msg);
	}

	private boolean downloadImage() {

		AndroidTransferManager atm = new AndroidTransferManager();
		Uri uri = atm.downloadImage(imageUrl);
		try {
			if (uri != null) {
				imageCache.put(imageUrl, uri.toString());
				return true;
			} else
				return false;
		} catch (IOException e) {
			Log.e(LOG_TAG, "download for " + imageUrl + " failed");
			e.printStackTrace();
		}
		return false;
	}
}
