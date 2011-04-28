package com.kistalk.android.util;

import java.util.LinkedList;

import com.kistalk.android.base.FeedItem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	}

	public void addViews(ImageView imageView) {
		imageViews.add(imageView);
	}

	/**
	 * Override this method if you need custom handler logic. Note that this
	 * method can actually be called directly for performance reasons, in which
	 * case the message will be null
	 * 
	 * @param bitmap
	 *            the bitmap returned from the image loader
	 * @param msg
	 *            the handler message; can be null
	 * @return true if the view was updated with the new image, false if it was
	 *         discarded
	 */

}
