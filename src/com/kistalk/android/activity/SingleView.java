package com.kistalk.android.activity;

import com.kistalk.android.R;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.DbAdapter;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SingleView extends ListActivity implements Constant {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int itemId = getIntent().getIntExtra(KEY_ITEM_ID, 0);
		
		setContentView(R.layout.main);
		
		KisTalk.dbAdapter.open();
		Cursor cur = KisTalk.dbAdapter.fetchComments(itemId);

		String[] displayFields = new String[] {KEY_COM_USER_NAME, KEY_COM_CONTENT};

		int[] displayViews = new int[] {R.id.user_name, R.id.description};

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.status_feed_item, cur, displayFields, displayViews);

		setListAdapter(adapter);
		
		KisTalk.dbAdapter.close();
		
	}
	
}
