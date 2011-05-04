package com.kistalk.android.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.kistalk.android.util.DbAdapter;
import com.kistalk.android.base.FeedItem;

public class DBThread implements Runnable, Constant {

	private final DbAdapter dbAdapter;
	private final DBLoader dbExecutor;
	
	public DBThread(DbAdapter dbAdapter, DBLoader dbExecutor) {
		this.dbAdapter = dbAdapter;
		this.dbExecutor = dbExecutor;
	}
	
	@Override
	public void run() {
		try {
			LinkedList<FeedItem> feedItems = KT_XMLParser
					.fetchAndParse();
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
		
		dbExecutor.callBackUIThread();
	}
}
