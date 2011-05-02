package com.kistalk.android.activity;

import com.kistalk.android.R;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.ImageLoader;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SingleView extends ListActivity implements Constant {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int itemId = getIntent().getIntExtra(KEY_ITEM_ID, 0);

		setContentView(R.layout.image_view);

		View imageItem = getLayoutInflater().inflate(R.layout.big_image_view,
				null);
		// View imageItem = View.inflate(this, R.layout.big_image_view,
		// getListView());

		KisTalk.dbAdapter.open();
		Cursor cur1 = KisTalk.dbAdapter.fetchPostFromId(itemId);
		String url = cur1.getString(cur1.getColumnIndex(KEY_ITEM_URL_BIG));
		String userName = cur1.getString(cur1
				.getColumnIndex(KEY_ITEM_USER_NAME));
		String avatarUrl = cur1.getString(cur1
				.getColumnIndex(KEY_ITEM_USER_AVATAR));
		String description = cur1.getString(cur1
				.getColumnIndex(KEY_ITEM_DESCRIPTION));
		String date = cur1.getString(cur1.getColumnIndex(KEY_ITEM_DATE));

		// View imageItem = findViewById(R.id.image_view_image);

		// View imageItem = findViewById(R.id.big_image_item);

		ImageLoader.start(url,
				(ImageView) imageItem.findViewById(R.id.big_image));
		ImageLoader.start(avatarUrl,
				(ImageView) imageItem.findViewById(R.id.profile_image));
		((TextView) imageItem.findViewById(R.id.user_name)).setText(userName);
		((TextView) imageItem.findViewById(R.id.description))
				.setText(description);
		((TextView) imageItem.findViewById(R.id.date)).setText(date);

		getListView().addHeaderView(imageItem);

		Cursor cur = KisTalk.dbAdapter.fetchComments(itemId);
		String[] displayFields = new String[] { KEY_COM_USER_NAME,
				KEY_COM_USER_AVATAR, KEY_COM_CONTENT, KEY_COM_DATE };

		int[] displayViews = new int[] { R.id.user_name, R.id.profile_image,
				R.id.comment, R.id.date };

		AndSimpleCursorAdapter adapter = new AndSimpleCursorAdapter(this,
				R.layout.comment_item, cur, displayFields, displayViews);

		setListAdapter(adapter);

		KisTalk.dbAdapter.close();
	}
}