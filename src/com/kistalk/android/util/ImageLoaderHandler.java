package com.kistalk.android.util;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoaderHandler extends Handler implements Constant {

	private LinkedList<ImageView> imageViews;

	public ImageLoaderHandler(ImageView imageView) {
		imageViews = new LinkedList<ImageView>();
		imageViews.add(imageView);
	}

	public void handleMessage(Message msg) {
		Bundle data = msg.getData();
		
		if (data.containsKey(KEY_BITMAP)) {
			Bitmap bitmap = msg.getData().getParcelable(KEY_BITMAP);
			for (ImageView imageView : imageViews)
				imageView.setImageBitmap(bitmap);
		} else if (data.containsKey(KEY_URI)) {
			Uri uri = Uri.parse(msg.getData().getString(KEY_URI));
			for (ImageView imageView : imageViews)
				imageView.setImageURI(uri);
		} else if (data.containsKey(KEY_RESOURCE)) {
			int resource = msg.getData().getInt(KEY_RESOURCE);
			for (ImageView imageView : imageViews)
				imageView.setImageResource(resource);
		}
		else
			Log.e(LOG_TAG, ImageLoaderHandler.class.toString() + ": bad message");
	}

	public void addViews(ImageView imageView) {
		imageViews.add(imageView);
	}
}
