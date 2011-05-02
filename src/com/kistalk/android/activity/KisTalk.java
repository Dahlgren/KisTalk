package com.kistalk.android.activity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import com.kistalk.android.R;
import com.kistalk.android.base.FeedItem;
import com.kistalk.android.util.AndXMLParser;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.DbAdapter;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class KisTalk extends ListActivity implements Constant {

	// TAG used in log file
	private static final String LOG_TAG = "Activity.KisTalk";

	// public directories for cache and files
	public static File cacheDir;
	public static File filesDir;
	
	private static String username = "zoger";
	private static String token = "k1igvh1xyg";

	public static String getUsername() {
		return username;
	}

	private static void setUsername(String username) {
		KisTalk.username = username;
	}

	public static String getToken() {
		return token;
	}

	private static void setToken(String token) {
		KisTalk.token = token;
	}

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

		setOnClickListeners();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub

		return super.onRetainNonConfigurationInstance();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
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
							v.findViewById(R.id.refresh_focus_bg)
									.setVisibility(View.VISIBLE);
						else
							v.findViewById(R.id.refresh_focus_bg)
									.setVisibility(View.INVISIBLE);

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
				Log.e(LOG_TAG, "Can't access cacheDir");
		if (!filesDir.mkdirs())
			if (!filesDir.exists())
				Log.e(LOG_TAG, "Can't access filesDir");
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
				showComments(itemId);

			}
		});

		/*
		 * Button that allows file uploading of picture
		 */
		findViewById(R.id.choose_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showFileChooser();
					}
				});

		findViewById(R.id.upload_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						takePhotoAction();
					}
				});

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

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
	}

	private void takePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, GET_CAMERA_PIC_REQUEST);
		// File pathToImage = null;
		//
		// try {
		// pathToImage = File.createTempFile("image", ".jpg",
		// KisTalk.cacheDir);
		// } catch (IOException e) {
		// Log.e(LOG_TAG, e.toString());
		// }

		// Uri tempUri = Uri.fromFile(pathToImage);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, pathToImage);
	}

	private void showComments(int itemId) {
		Intent commentIntent = new Intent(KisTalk.this, SingleView.class);
		commentIntent.setAction(Intent.ACTION_VIEW);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		commentIntent.putExtra(KEY_ITEM_ID, itemId);
		try {
			KisTalk.this.startActivity(commentIntent);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}
	}

	private void showUploadView(String pathToImage) {
		Intent uploadIntent = new Intent(KisTalk.this, UploadPhoto.class);
		uploadIntent.setAction(Intent.ACTION_VIEW);
		uploadIntent.putExtra(KEY_UPLOAD_IMAGE_URI, pathToImage);

		try {
			KisTalk.this.startActivity(uploadIntent);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}
	}

	protected void refreshPosts() {
		dbAdapter.open();
		dbAdapter.deleteAll();

		try {
			LinkedList<FeedItem> feedItems = AndXMLParser.fetchAndParse();
			if (feedItems == null){
				Log.e(LOG_TAG, "Problem when downloading XML file");
				return;
			}
				
			for (FeedItem feedItem : feedItems) {
				dbAdapter.insertPost(feedItem.post);
				dbAdapter.insertComments(feedItem.comments);
			}
		} catch (XmlPullParserException e) {
			Log.e(LOG_TAG, e.toString(), e);
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString(), e);
		} catch (URISyntaxException e) {
			Log.e(LOG_TAG, e.toString(), e);
		}

		Cursor cur = dbAdapter.fetchAllPosts();

		String[] displayFields = new String[] { KEY_ITEM_USER_NAME, KEY_ITEM_USER_AVATAR, 
				KEY_ITEM_URL_SMALL, KEY_ITEM_DESCRIPTION, KEY_ITEM_DATE,
				KEY_ITEM_NUM_OF_COMS };

		int[] displayViews = new int[] { R.id.user_name, R.id.profile_image, R.id.image,
				R.id.description, R.id.date, R.id.num_of_comments };

		AndSimpleCursorAdapter adapter = new AndSimpleCursorAdapter(this,
				R.layout.status_feed_item, cur, displayFields, displayViews);

		setListAdapter(adapter);

		dbAdapter.close();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {

			Uri recievedUri = intent.getData();
			if (recievedUri != null) {

				String realPath = getRealPathFromURI(recievedUri);
				if (requestCode == GET_CAMERA_PIC_REQUEST) {
					showUploadView(realPath);
				}
				if (requestCode == CHOOSE_IMAGE_REQUEST) {
					showUploadView(realPath);
				}
			}
		}
	}

	// Convert the image URI to the direct file system path of the image file
	private String getRealPathFromURI(Uri contentUri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
}
