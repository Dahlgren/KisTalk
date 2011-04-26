package com.kistalk.android.activity;


import com.kistalk.android.util.Constant;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class AndSimpleCursorAdapter extends SimpleCursorAdapter implements Constant {


	
public AndSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

}
