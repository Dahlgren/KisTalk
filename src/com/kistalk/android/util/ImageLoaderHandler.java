package com.kistalk.android.util;

import com.kistalk.android.base.FeedItem;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageLoaderHandler extends Handler implements Constant {

	private ImageView imageView;
	private Drawable errorDrawable;

	private ImageCache imageCache;
	private String imageUrl;
	private FeedItem feedItem;


	public ImageLoaderHandler(ImageView imageView, String imageUrl) {
		this.imageView = imageView;
		this.imageUrl = imageUrl;
	}

	public ImageLoaderHandler(ImageView imageView, String imageUrl,
			Drawable errorDrawable) {
		this(imageView, imageUrl);
		this.errorDrawable = errorDrawable;
	}

	@Override
	public final void handleMessage(Message msg) {
		if (msg.what == ImageLoader.HANDLER_MESSAGE_ID) {
			handleImageLoadedMessage(msg);
		}
	}

	protected final void handleImageLoadedMessage(Message msg) {
		Bundle data = msg.getData();
		String imageUrl = data.getString(ImageLoader.IMAGE_URL);
		handleImageLoaded(imageUrl);
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
	protected boolean handleImageLoaded(String imageUrl) {
		// If this handler is used for loading images in a ListAdapter,
		// the thread will set the image only if it's the right position,
		// otherwise it won't do anything.
		if (feedItem != null && imageCache.containsInCache(imageUrl)) {
			feedItem.post.put(KEY_ITEM_URL_BIG, imageUrl);
			return true;
		} else
			return false;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
}
