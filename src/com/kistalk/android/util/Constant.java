package com.kistalk.android.util;

public interface Constant {

	/*
	 * Intent constants
	 */
	public static final int CHOOSE_IMAGE_REQUEST = 1337;
	public static final int GET_CAMERA_PIC_REQUEST = 1338;

	/*
	 * Key constants for ContentValues
	 */

	// Key constants for feed items
	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
	public static final String KEY_ITEM_URL_BIG = "KEY_ITEM_URL_BIG";
	public static final String KEY_ITEM_URL_SMALL = "KEY_ITEM_URL_SMALL";
	public static final String KEY_ITEM_USER_ID = "KEY_ITEM_USER_ID";
	public static final String KEY_ITEM_USER_NAME = "KEY_ITEM_USER_NAME";
	public static final String KEY_ITEM_USER_AVATAR = "KEY_ITEM_USER_AVATAR";
	public static final String KEY_ITEM_DESCRIPTION = "KEY_ITEM_DESCRIPTION";
	public static final String KEY_ITEM_DATE = "KEY_ITEM_DATE";
	public static final String KEY_ITEM_NUM_OF_COMS = "hej";

	// Key constants for feed item comments
	public static final String KEY_COM_ID = "KEY_COM_ID";
	public static final String KEY_COM_USER_ID = "KEY_COM_USER_ID";
	public static final String KEY_COM_USER_NAME = "KEY_COM_USER_NAME";
	public final static String KEY_COM_USER_AVATAR = "KEY_COM_USER_AVATAR";
	public static final String KEY_COM_CONTENT = "KEY_COM_CONTENT";
	public static final String KEY_COM_DATE = "KEY_COM_DATE";

	// Key constants for
	public static final String KEY_UPLOAD_IMAGE_URI = "KEY_UPLOAD_IMAGE_URI";
	public static final String KEY_UPLOAD_IMAGE_DESCRIPTION = "KEY_UPLOAD_IMAGE_DESCRIPTION";

	// Key constants for ImageLoader, ImageLoaderHandler and ImageCache
	public static final String KEY_URI = "KEY_URI";
	public static final String KEY_BITMAP = "KEY_BITMAP";
	public static final String KEY_RESOURCE = "KEY_RESOURCE";

	// public static final String KEY_DB_ADAPTER = "KEY_DB_ADAPTER";

	// Constants for webserver and more
	public static final String SCHEME = "http";
	public static final String HOST = "www.kistalk.com";
	public static final String XML_FILE_PATH = "/api/feed/android.xml";
	
	public static final String WEBSERVER_URL = SCHEME + "://" + HOST;
	public static final String XML_FILE_FULL_URL = WEBSERVER_URL + XML_FILE_PATH;

	// Constants for uploading pictures to webserver (MIME)
	public static final String UPLOAD_ARG_USERNAME = "username";
	public static final String UPLOAD_ARG_TOKEN = "token";
	public static final String UPLOAD_ARG_PICTURE = "picture";
	// picture argument should be of type MIME (yes, this will be nested) and
	// have to args, image and description

	public static final String UPLOAD_ARG_IMAGE = "image";
	public static final String UPLOAD_ARG_DESCRIPTION = "description";

}
