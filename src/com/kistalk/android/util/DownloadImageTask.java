package com.kistalk.android.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

/*
 * The three generic types are:
 * Params, the type of the parameters sent to the task upon execution.
 * Progress, the type of the progress units published during the background computation.
 * Result, the type of the result of the background computation.
 * 
 * NEVER call one of the methods manually.
 * Create a new thread and invoke the method "execute(params)" where params
 * is a list of parameters.
 * Each thread can only run once.
 * 
 * All the methods in this class are thread safe.
 * 
 * A task can be cancelled at any time by invoking cancel(boolean).
 * Invoking this method will cause subsequent calls to isCancelled() to return true.
 * To ensure that a task is cancelled as quickly as possible, 
 * you should always check the return value of isCancelled() periodically 
 * from doInBackground(Object[]), if possible (inside a loop for instance.)
 * */

/* The parameters are of the type ContentValues and the result is of type String */
public class DownloadImageTask extends AsyncTask<String, Void, Uri> {
	
	private ImageView view;
	
	public DownloadImageTask(ImageView view) {
		//super(); // ta bort
		this.view = view;
	}
	
	@Override
	protected Uri doInBackground(String... downloadLinks) {
		AndroidTransferManager transferManager = new AndroidTransferManager();
		int count = downloadLinks.length;
		int index = 0;
		Uri locationToFile = null;
		/* If not cancelled or not gone through all items - do work */
		while (!isCancelled() || index < count) {
			locationToFile = transferManager.downloadImage(downloadLinks[index]);
			count++;
		}
		return locationToFile;
	}
	
	@Override
	protected void onPostExecute(Uri result) {
		view.setImageURI(result);
	}
	
}
