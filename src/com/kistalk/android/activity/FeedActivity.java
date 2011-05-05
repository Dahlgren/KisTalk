package com.kistalk.android.activity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.kistalk.android.R;
import com.kistalk.android.activity.kt_extensions.KT_SimpleCursorAdapter;
import com.kistalk.android.base.FeedItem;
import com.kistalk.android.image_management.ImageController;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.DbAdapter;
import com.kistalk.android.util.KT_TransferManager;
import com.kistalk.android.util.KT_XMLParser;

public class FeedActivity extends ListActivity implements Constant {

	// public directories for cache and files
	public static File cacheDir;
	public static File filesDir;

	private static String username;
	private static String token;

	// private instances of classes
	public static DbAdapter dbAdapter;
	public static ImageController imageController = new ImageController();

	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeVariables();
		startUpCheck();

		setContentView(R.layout.feed_view_layout);

		setFocusListeners();
		setOnClickListeners();

		dbAdapter.open();
		restoreImageCache(savedInstanceState);

		sp = getPreferences(MODE_PRIVATE);

		username = sp.getString(ARG_USERNAME, null);
		token = sp.getString(ARG_TOKEN, null);

		validateCredentials();
	}

	private void validateCredentials() {
		if (token == null || username == null)
			startLoginActivityForResult();
		else {
			KT_TransferManager transferManager = new KT_TransferManager();
			if (!transferManager.validate(username, token))
				startLoginActivityForResult();
			else {
				populateList();
				refreshPosts();
			}
		}

	}

	private void restoreImageCache(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			HashMap<String, String> imageCacheHashMap = (HashMap<String, String>) savedInstanceState
					.getSerializable(KEY_IMAGE_CACHE_HASHMAP);
			if (imageCacheHashMap != null)
				imageController.setCacheHashMap(imageCacheHashMap);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}

	public void populateList() {
		Cursor cur = dbAdapter.fetchAllPosts();

		String[] displayFields = new String[] { KEY_ITEM_USER_NAME,
				KEY_ITEM_USER_AVATAR, KEY_ITEM_URL_SMALL, KEY_ITEM_DESCRIPTION,
				KEY_ITEM_DATE, KEY_ITEM_NUM_OF_COMS };

		int[] displayViews = new int[] { R.id.user_name, R.id.avatar,
				R.id.image, R.id.description, R.id.date, R.id.num_of_comments };

		KT_SimpleCursorAdapter adapter = new KT_SimpleCursorAdapter(this,
				R.layout.feed_item_layout, cur, displayFields, displayViews);

		setListAdapter(adapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Called when configuration changes because
		// android:configChanges="orientation" in XML file
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("ImageCache",
				imageController.getCacheHashMap());
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	private void setFocusListeners() {
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
		dbAdapter = new DbAdapter(this);
		imageController = new ImageController();
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
		findViewById(R.id.upload_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showDialog(DIALOG_CHOOSE_OPTION_ID);
					}
				});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_CHOOSE_OPTION_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick an option").setCancelable(true)
					.setItems(OPTIONS, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if (0 == id) {
								showFileChooser();
							} else if (1 == id) {
								takePhotoAction();
							}
						}
					});
			return builder.create();

		default:
			dialog = null;
		}
		return dialog;
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
	}

	private void takePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, GET_CAMERA_PIC_REQUEST);
	}

	private void showComments(int itemId) {
		Intent commentIntent = new Intent(FeedActivity.this,
				ThreadActivity.class);
		commentIntent.setAction(Intent.ACTION_VIEW);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		commentIntent.putExtra(KEY_ITEM_ID, itemId);
		try {
			FeedActivity.this.startActivity(commentIntent);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}
	}

	private void showUploadView(String pathToImage) {
		Intent uploadIntent = new Intent(this, UploadActivity.class);
		uploadIntent.setAction(Intent.ACTION_VIEW);
		uploadIntent.putExtra(KEY_UPLOAD_IMAGE_URI, pathToImage);

		try {
			this.startActivity(uploadIntent);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}
	}

	private void refreshPosts() {

		// dbLoader.start(dbAdapter);

		try {
			LinkedList<FeedItem> feedItems = KT_XMLParser.fetchAndParse();
			if (feedItems == null) {
				Log.e(LOG_TAG, "Problem when downloading XML file");
			}

			dbAdapter.deleteAll();

			for (FeedItem feedItem : feedItems) {
				dbAdapter.insertPost(feedItem.post);
				dbAdapter.insertComments(feedItem.comments);
			}
		} catch (XmlPullParserException e) {
			Log.e(LOG_TAG, "" + e, e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "" + e, e);
		} catch (URISyntaxException e) {
			Log.e(LOG_TAG, "" + e, e);
		}

		populateList();

		// DBLoader.start(this, this.dbAdapter);
		/*
		 * dbSerialExecutor = new DBSerialExecutor(this); Thread dbThread = new
		 * Thread(new DBThread(dbAdapter, dbSerialExecutor));
		 * dbSerialExecutor.addTask(dbThread); dbSerialExecutor.start();
		 */
		/*
		 * new AsyncTask<Void, Void, Void>() {
		 * 
		 * @Override protected Void doInBackground(Void... params) { try {
		 * LinkedList<FeedItem> feedItems = KT_XMLParser .fetchAndParse(); if
		 * (feedItems == null) { Log.e(LOG_TAG,
		 * "Problem when downloading XML file"); return null; }
		 * 
		 * dbAdapter.deleteAll();
		 * 
		 * for (FeedItem feedItem : feedItems) {
		 * dbAdapter.insertPost(feedItem.post);
		 * dbAdapter.insertComments(feedItem.comments); } } catch
		 * (XmlPullParserException e) { Log.e(LOG_TAG, "" + e, e); } catch
		 * (IOException e) { Log.e(LOG_TAG, "" + e, e); } catch
		 * (URISyntaxException e) { Log.e(LOG_TAG, "" + e, e); }
		 * 
		 * return null; }
		 * 
		 * @Override protected void onPostExecute(Void result) { populateList();
		 * } }.execute((Void[]) null);
		 */
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Uri recievedUri = intent.getData();
			switch (requestCode) {
			case LOGIN_REQUEST:
				if (resultCode == RESULT_OK) {
					username = intent.getStringExtra(ARG_USERNAME);
					token = intent.getStringExtra(ARG_TOKEN);

					sp.edit().putString(ARG_USERNAME, username)
							.putString(ARG_TOKEN, token).commit();

					imageController.clearCache();
					refreshPosts();

				} else {
					finish();
				}
				break;

			case GET_CAMERA_PIC_REQUEST:
				if (recievedUri != null) {
					String realPath = getRealPathFromURI(recievedUri);
					showUploadView(realPath);
				}
				break;
			case CHOOSE_IMAGE_REQUEST:
				if (recievedUri != null) {
					String realPath = getRealPathFromURI(recievedUri);
					showUploadView(realPath);
				}
				break;
			default:
				break;
			}
		}

	}

	// Convert the image URI to the direct file system path of the image file
	private String getRealPathFromURI(Uri contentUri) {

		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public static String getUsername() {
		return username;
	}

	private static void setUsername(String username) {
		FeedActivity.username = username;
	}

	public static String getToken() {
		return token;
	}

	private static void setToken(String token) {
		FeedActivity.token = token;
	}

	private void startLoginActivityForResult() {
		Intent loginIntent = new Intent(this, LoginActivity.class);
		startActivityForResult(loginIntent, LOGIN_REQUEST);
	}

}
