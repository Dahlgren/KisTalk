package com.kistalk.android.image_management;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.kistalk.android.util.Constant;

import android.util.Log;
import android.widget.ImageView;

public class ImageController implements Constant {

	private ImageCache imageCache;
	private HashMap<String, ImageLoaderHandler> activeHandlers;
	private ThreadPoolExecutor executor;

	// the default thread pool size
	private static final int DEFAULT_POOL_SIZE = 3;

	public ImageController() {
		imageCache = new ImageCache();
		executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(DEFAULT_POOL_SIZE);
		activeHandlers = new HashMap<String, ImageLoaderHandler>();
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

	public synchronized void start(String imageUrl, final ImageView imageView) {

		if (imageView == null || imageUrl == null) {
			Log.w(LOG_TAG, "parameter imageView and/or imageUrl are null");
			return;
		}

		if (activeHandlers.containsKey(imageUrl))
			activeHandlers.get(imageUrl).addViews(imageView);
		else {
			ImageLoaderHandler handler = new ImageLoaderHandler(imageView,
					this, imageUrl);
			executor.execute(new ImageLoader(imageUrl, handler, imageCache));
			activeHandlers.put(imageUrl, handler);
		}

	}

	/**
	 * Clears the cache. Perhaps should be called in the event of
	 * {@link android.app.Application#onLowMemory()}.
	 */

	public void clearCache() {
		imageCache.clear();
	}

	/**
	 * Returns the image cache whichs backs this image loader.
	 * 
	 * @return the {@link ImageCache}
	 */
	public ImageCache getImageCache() {
		return imageCache;
	}

	/**
	 * @param numThreads
	 *            the maximum number of threads that will be started to download
	 *            images in parallel
	 */
	public void setThreadPoolSize(int numThreads) {
		executor.setMaximumPoolSize(numThreads);
	}

	public synchronized void removeHandler(String imageUrl) {
		activeHandlers.remove(imageUrl);
	}

	public HashMap<String, String> getCacheHashMap() {
		return imageCache.getHashMap();
	}

	public void setCacheHashMap(HashMap<String, String> hashMap) {
		imageCache.setHashMap(hashMap);
	}
}
