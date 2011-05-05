package com.kistalk.android.image_management;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kistalk.android.util.Constant;

/**
 * Implements a cache capable of caching image files. It exposes helper methods
 * to immediately access binary image data as {@link Bitmap} objects.
 * 
 */
public class ImageCache implements Constant {

	private HashMap<String, String> cacheInfo;

	/* Default constructor */
	public ImageCache() {
		cacheInfo = new HashMap<String, String>();
	}

	/* Retrieves the bitmap image otherwise returns nothing */
	public Bitmap getBitmap(String imageUrl) {
		return BitmapFactory.decodeFile(cacheInfo.get(imageUrl));
	}

	/*
	 * Puts neccessary lookup information for the download image along writing
	 * it to a storage location
	 */
	public void put(String url, String path) throws IOException {
		/* Puts url to the image as the key and uri for the location of the file */
		if (url != null && path != null)
			cacheInfo.put(url, path);
	}

	/* Optional method to retrieve the image location */
	public String getPath(String url) {
		if (contains(url))
			return cacheInfo.get(url);
		else
			return null;
	}

	/* Returns a boolean if the image url is in the cache */
	public boolean contains(String imageUrl) {
		return cacheInfo.containsKey(imageUrl);
	}

	/*
	 * Clear the cache of all refences TODO: Implement a way to delete files
	 */
	public void clear() {
		for (String path : cacheInfo.values()) {
			File cachedImageFile = new File(path);
			cachedImageFile.delete();
		}
		cacheInfo.clear();
	}

	HashMap<String, String> getHashMap() {
		return cacheInfo;
	}

	void setHashMap(HashMap<String, String> cacheInfo) {
		this.cacheInfo = cacheInfo;
	}

}
