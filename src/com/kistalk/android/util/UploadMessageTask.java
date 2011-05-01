package com.kistalk.android.util;

import com.kistalk.android.base.UserMessage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

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
public class UploadMessageTask extends AsyncTask<UserMessage, Void, String> {

	private Context context;
	private ProgressDialog progDialog;

	/*
	 * Default constructor. Calls the super class constructor and then takes an
	 * user specified context argument
	 * 
	 * @param context
	 */
	public UploadMessageTask(Context context) {
		super();
		this.context = context;
		progDialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		progDialog.setCancelable(true);
		progDialog.setMessage("Uploading. Please wait...");
		progDialog.setButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Are you sure you want to cancel?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										cancel(true); // Kill the running thread
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				/* Create and show dialog */
				(builder.create()).show();
			}
		});
		progDialog.show();
	}

	@Override
	protected String doInBackground(UserMessage... messages) {
		AndroidTransferManager transferManager = new AndroidTransferManager();
		int count = messages.length;
		int index = 0;
		/* If not cancelled or not gone through all items - do work */
		while (!isCancelled() && index < count) {
			transferManager.uploadMessage(messages[index]);
			index++;
		}
		return "Upload complete!";
	}

	@Override
	protected void onPostExecute(String result) {
		progDialog.dismiss(); //Removes the progress dialog
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Upload complete!")
				.setCancelable(true)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
		/* Create and show dialog */
		(builder.create()).show();
	}
}
