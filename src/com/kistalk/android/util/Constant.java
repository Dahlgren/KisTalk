package com.kistalk.android.util;

public interface Constant {
	
	public static final String LOG_TAG = "KisTalk";
	/*
	 * Intent constants
	 */
	public static final int CHOOSE_IMAGE_REQUEST = 1337;
	public static final int GET_CAMERA_PIC_REQUEST = 1338;	
	public static final int LOGIN_REQUEST = 1339;
	public static final int REQUEST_QR_READER = 1340;

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
	public static final String XML_FILE_PATH = "/api/feed/android";
	public static final String VALIDATE_CREDENTIAL_PATH = "api/validate_token";
	
	public static final String UPLOAD_IMAGE_PATH = "/api/images/create";
	public static final String POST_COMMENT_PATH = "/api/comment/create";
	
	public static final String WEBSERVER_URL = SCHEME + "://" + HOST;
	public static final String XML_FILE_FULL_URL = WEBSERVER_URL + XML_FILE_PATH;

	// Argument names for webserver
	public static final String ARG_USERNAME = "username";
	public static final String ARG_TOKEN = "token";
	
	public static final String ARG_UPLOAD_IMAGE = "image";
	public static final String ARG_UPLOAD_DESCRIPTION = "comment";
	
	public static final String ARG_COMMENT_ITEMID = "image_id";
	public static final String ARG_COMMENT_CONTENT = "content";

}
