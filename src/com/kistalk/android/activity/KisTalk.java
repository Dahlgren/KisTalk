package com.kistalk.android.activity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParserException;

import com.kistalk.android.R;
import com.kistalk.android.base.FeedItem;
import com.kistalk.android.util.AndXMLParser;
import com.kistalk.android.util.AndroidTransferManager;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.DbAdapter;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class KisTalk extends ListActivity implements Constant {

	// TAG used in log file
	private static final String TAG = "Activity.KisTalk";

	// public directories for cache and files
	public static File cacheDir;
	public static File filesDir;

	// private instances of classes
	public static DbAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeVariables();
		startUpCheck();

		setContentView(R.layout.main);
		setFocusListeners();

		dbAdapter = new DbAdapter(this);

		// AndroidTransferManager atm = new AndroidTransferManager();
		// Uri uri =
		// atm.downloadImage("http://ec2.smidigit.se/img/img1_800.jpg");
		// ((ImageView) findViewById(R.id.imageView1)).setImageURI(uri);

		//refreshPosts();

		setOnClickListeners();

		// new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
		// .execute("http://ec2.smidigit.se/img/img1_800.jpg");
	}

	private void setFocusListeners() {
		findViewById(R.id.choose_button).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus)
							v.findViewById(R.id.choose_focus_bg).setVisibility(
									View.VISIBLE);
						else
							v.findViewById(R.id.choose_focus_bg).setVisibility(
									View.INVISIBLE);

					}
				});
		
		findViewById(R.id.upload_button).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus)
							v.findViewById(R.id.upload_focus_bg).setVisibility(
									View.VISIBLE);
						else
							v.findViewById(R.id.upload_focus_bg).setVisibility(
									View.INVISIBLE);

					}
				});
		
		findViewById(R.id.refresh_button).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus)
							v.findViewById(R.id.refresh_focus_bg).setVisibility(
									View.VISIBLE);
						else
							v.findViewById(R.id.refresh_focus_bg).setVisibility(
									View.INVISIBLE);

					}
				});

	}

	/*
	 * Initializes variables for this activity but also public variables
	 * available for other classes
	 */
	private void initializeVariables() {
		cacheDir = getCacheDir();
		filesDir = getFilesDir();
	}

	/*
	 * Method that checks environment and variables that's necessary for the
	 * application to run
	 */
	private void startUpCheck() {
		/*
		 * Checks whether directories exists or not and if they can be accessed
		 */
		if (!cacheDir.mkdirs())
			if (!cacheDir.exists())
				Log.e(TAG, "Can't access cacheDir");
		if (!filesDir.mkdirs())
			if (!filesDir.exists())
				Log.e(TAG, "Can't access filesDir");
	}

	/*
	 * help method thats shows a dialog window for debugging and testing
	 */
	public void dialog(String s) {
		Dialog d = new Dialog(this);
		TextView tv = new TextView(this);
		tv.setText(s);
		d.setContentView(tv);
		d.setTitle("Dialog");
		d.show();
	}

	/*
	 * test method
	 */

	private void bookmarkstest() {

		String[] projection = new String[] { Browser.BookmarkColumns._ID,
				Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };

		String[] displayFields = new String[] { Browser.BookmarkColumns.TITLE,
				Browser.BookmarkColumns.URL };

		int[] displayViews = new int[] { R.id.user_name, R.id.description };

		Cursor cur = managedQuery(android.provider.Browser.BOOKMARKS_URI,
				projection, null, null, null);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.status_feed_item, cur, displayFields, displayViews);

		setListAdapter(adapter);
	}

	private void setOnClickListeners() {
		/*
		 * Refresh button that refreshes all feed items
		 */

		findViewById(R.id.refresh_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						refreshPosts();

					}
				});

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
				Cursor cur = adapter.getCursor();
				int itemId = cur.getInt(cur.getColumnIndex(KEY_ITEM_ID));
				// Object item = adapter.getItem(position);
				// dialog(String.valueOf(itemId));

				showComments(itemId);

			}
		});

		/*
		 * Button that allows file uploading of picture
		 */

		// findViewById(R.id.choose_button).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showFileChooser();
		// }
		// });

		// findViewById(R.id.upload_button).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showTakePhotoDialog();
		// }
		// });

		// findViewById(R.id.upload_button).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Uri uri = Uri
		// .fromFile(new File(
		// "/data/data/com.kistalk.android/cache/img1_200.jpg"));
		// // dialog(getFilesDir().toString());
		// // dialog(uri.toString());
		//
		// ((ImageView) findViewById(R.id.temp_image))
		// .setImageURI(uri);
		//
		// // dbAdapter.insert(uri, "Desc3: Äpplen är goda!",
		// // "Anders", "Igår");
		// // populateList();
		// // iv.setI
		//
		// }
		// });

	}

	protected void showComments(int itemId) {
		Intent intent = new Intent(KisTalk.this, SingleView.class);
		intent.setAction(Intent.ACTION_VIEW);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_ITEM_ID, itemId);
		try {
			KisTalk.this.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	protected void showCommentzz(int itemId) {
		Intent intent = new Intent(Intent.ACTION_VIEW, null, this,
				SingleView.class);
		intent.putExtra(KEY_ITEM_ID, itemId);
		startActivity(intent);
	}

	protected void refreshPosts() {
		dbAdapter.open();
		dbAdapter.deleteAll();

		try {
			LinkedList<FeedItem> feedItems = AndXMLParser
					.parse("/android_images.xml");
			for (FeedItem feedItem : feedItems) {
				dbAdapter.insertPost(feedItem.post);
				dbAdapter.insertComments(feedItem.comments);
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
		} catch (URISyntaxException e) {
			Log.e(TAG, e.toString(), e);
		}

		Cursor cur = dbAdapter.fetchAllPosts();

		String[] displayFields = new String[] { KEY_ITEM_USER_NAME,
				KEY_ITEM_USER_ID, KEY_ITEM_URL_BIG, KEY_ITEM_DESCRIPTION,
				KEY_ITEM_DATE, KEY_ITEM_NUM_OF_COMS };

		int[] displayViews = new int[] { R.id.user_name, R.id.user_id,
				R.id.image, R.id.description, R.id.date, R.id.num_of_comments };

		AndSimpleCursorAdapter adapter = new AndSimpleCursorAdapter(this,
				R.layout.status_feed_item, cur, displayFields, displayViews);

		setListAdapter(adapter);

		dbAdapter.close();

	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_FILE_ID);
	}

	// protected void showTakePhotoDialog() {
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	//
	// // Uri tempUri = Uri.fromFile(File.createTempFile("temp", "jpg"));
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, new
	// File(getCacheDir(),"temp.jpg"));
	// startActivityForResult(intent, GET_CAMERA_PIC_ID);
	//
	// }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_CAMERA_PIC_ID) {
				// if (intent.getData() != null) {
				// ImageView iv = new ImageView(this);
				// iv.setImageURI(intent.getData());
				// ((ViewGroup) findViewById(R.id.upload_layout)).addView(iv);
				// } else {
				// TextView tv = new TextView(this);
				// tv.setText("uri är null");
				// ((ViewGroup) findViewById(R.id.upload_layout))
				// .addView(tv);
				// }
			}
			// if (requestCode == CHOOSE_FILE_ID) {
			// Uri uri = intent.getData();
			// if (uri != null) {
			// ImageView iv = new ImageView(this);
			// iv.setImageURI(uri);
			// ((ViewGroup) findViewById(R.id.upload_layout)).addView(iv);
			//
			// Cursor cursor = managedQuery(uri, null, null, null, null);
			// if (cursor != null && cursor.moveToFirst()) {
			//
			// File file = new File(cursor.getString(cursor
			// .getColumnIndexOrThrow(ImageColumns.DATA)));
			//
			// TextView tv = new TextView(this);
			// tv.setText(file.getName());
			// ((ViewGroup) findViewById(R.id.upload_layout))
			// .addView(tv);
			//
			// }
			// }
			// }
		}
	}

}