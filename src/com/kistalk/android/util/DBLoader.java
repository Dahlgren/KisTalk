package com.kistalk.android.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.kistalk.android.activity.FeedActivity;

public class DBLoader implements Constant {

	// the default thread pool size
	private final int DEFAULT_POOL_SIZE = 1;

	private ThreadPoolExecutor tpExecutor;
	private final FeedActivity feedActivity;
	
	public DBLoader(FeedActivity feedActivity) {
		this.tpExecutor = (ThreadPoolExecutor) Executors
		.newFixedThreadPool(DEFAULT_POOL_SIZE);
		this.feedActivity = feedActivity;
	}
	
	public void start(DbAdapter dbAdapter) {
		tpExecutor.execute(new DBThread(dbAdapter, this));
	}
	
	public synchronized void callBackUIThread() {
		feedActivity.populateList();
	}

}
