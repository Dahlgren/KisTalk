
package com.kistalk.android.activity;

import com.kistalk.android.R;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.ImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class AndSimpleCursorAdapter extends SimpleCursorAdapter implements
		Constant {

	public AndSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	@Override
	public void setViewImage(ImageView v, String value) {
		v.setImageResource(R.drawable.image_placeholder);
		ImageLoader.start(value, v);
	}
}

