package com.kistalk.android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Implements a cache capable of caching image files. It exposes helper methods
 * to immediately access binary image data as {@link Bitmap} objects.
 * 
 */
public class ImageCache implements Constant {

	private HashMap<String, String> cacheInfo;

	/* Default constructor */
	public ImageCache(int numberOfElements) {
		cacheInfo = new HashMap<String, String>();
	}

	public ImageCache() {
		this(25);
	}

	/* Retrieves the bitmap image otherwise returns nothing */
	public synchronized Bitmap getBitmap(String imageUrl) {
		if (contains(imageUrl)) {
			URI uri;
			try {
				uri = new URI(cacheInfo.get(imageUrl));
				return retrieveImageFromStorage(new File(uri));
			} catch (URISyntaxException e) {
				Log.e(LOG_TAG, "Bad uri: " + cacheInfo.get(imageUrl), e);
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.e(LOG_TAG, "ERROR when reading file", e);
			}
		}
		return null;
	}

	/*
	 * Puts neccessary lookup information for the download image along writing
	 * it to a storage location
	 */
	public void put(String url, String uri) throws IOException {

		/* Puts url to the image as the key and uri for the location of the file */
		if (url != null && uri != null)
			cacheInfo.put(url, uri);
	}

	/* Optional method to retrieve the image location */
	public String getUri(String url) {
		if (contains(url))
			return cacheInfo.get(url);
		else
			return null;
	}

	/* Read the data at a location */
	private Bitmap retrieveImageFromStorage(File file) throws IOException {
		BufferedInputStream istream = new BufferedInputStream(
				new FileInputStream(file));

		Bitmap retrievedBitmap = BitmapFactory.decodeStream(istream);

		/* Clean up */
		istream.close();

		return retrievedBitmap;
	}

	/* Returns a boolean if the image url is in the cache */
	public boolean contains(String imageUrl) {
		return cacheInfo.containsKey(imageUrl);
	}

	/*
	 * Clear the cache of all refences TODO: Implement a way to delete files
	 */
	public void clear() {
		for (String uri : cacheInfo.values()) {
			File cachedImageFile = new File(URI.create(uri));
			cachedImageFile.delete();
		}
		cacheInfo.clear();
	}

}
