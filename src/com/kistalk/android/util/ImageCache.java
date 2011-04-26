package com.kistalk.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.kistalk.android.activity.*;

/**
 * Implements a cache capable of caching image files. It exposes helper methods
 * to immediately access binary image data as {@link Bitmap} objects.
 * 
 */
public class ImageCache {

	private ContentValues cacheInfo;
	private File saveLocation;

	/* Default constructor */
	public ImageCache(int numberOfElements) {
		cacheInfo = new ContentValues(numberOfElements);
		saveLocation = KisTalk.cacheDir;
	}

	public ImageCache() {
		this(25);
	}

	/* Retrieves the bitmap image otherwise returns nothing */
	public synchronized Bitmap getBitmap(String imageUrl) {
		if (containsInCache(imageUrl)) {
			Bitmap image = null;
			try {
				image = retrieveImageFromStorage(saveLocation);
			} catch (IOException e) {
				Log.e("ERROR of reading file", e.toString());
			}
			return image;
		}
		return null;
	}

	/*
	 * Puts neccessary lookup information for the download image along writing
	 * it to a storage location
	 */
	public void putInCache(String imageUrl, Bitmap image) throws IOException {
		File pathToSave = saveLocation.createTempFile("image", ".jpg");
		writeImageToStorage(pathToSave, image);

		/* Puts url to the image as the key and uri for the location of the file */
		cacheInfo.put(imageUrl, pathToSave.getAbsolutePath());
	}

	/* Optional method to retrieve the image location */
	public String getImageUri(String imageUrl) {
		if (containsInCache(imageUrl))
			return cacheInfo.getAsString(imageUrl);
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

	/* Writes the data at a location */
	private void writeImageToStorage(File pathToFile, Bitmap imageData)
			throws IOException {
		BufferedOutputStream ostream = new BufferedOutputStream(
				new FileOutputStream(pathToFile));

		imageData.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

		/* Clean up */
		ostream.close();
	}

	/* Returns a boolean if the image url is in the cache */
	public boolean containsInCache(String imageUrl) {
		return cacheInfo.containsKey(imageUrl);
	}

	/*
	 * Clear the cache of all refences TODO: Implement a way to delete files
	 */
	public void clear() {
		cacheInfo.clear();
	}

}
