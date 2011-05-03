package com.kistalk.android.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.kistalk.android.base.FeedItem;

import android.content.ContentValues;

public class KT_XMLParser implements Constant {

	/* Constant strings */

	// Tag constants for feed items
	public static final String TAG_DOCUMENT = "all_image_info";
	public static final String TAG_FEED_ITEM = "image";

	public static final String TAG_ITEM_ID = "image-id";
	public static final String TAG_ITEM_URL_BIG = "url-big";
	public static final String TAG_ITEM_URL_SMALL = "url-thumb";
	public static final String TAG_ITEM_USER_ID = "image-user-id";
	public static final String TAG_ITEM_USER_NAME = "image-user-name";
	public static final String TAG_ITEM_USER_AVATAR = "image-user-avatar";
	public static final String TAG_ITEM_DESCRIPTION = "image-description";
	public static final String TAG_ITEM_DATE = "image-created_at";
	public static final String TAG_ITEM_COMMENTS = "comments";

	// Tag constants for feed item comments
	public static final String TAG_COMMENT = "comment";

	public static final String TAG_COM_ID = "comment-id";
	public static final String TAG_COM_USER_ID = "comment-user-id";
	public static final String TAG_COM_USER_NAME = "comment-user-name";
	public static final String TAG_COM_USER_AVATAR = "comment-user-avatar";
	public static final String TAG_COM_CONTENT = "comment-content";
	public static final String TAG_COM_DATE = "comment-created_at";

	// private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm:ss z"); // Example: 2011-04-06 07:48:53 UTC

	/**
	 * Parse method. The main method of the class. Takes a url string as an
	 * input parameter and construct FeedItem objects. The FeedItem objects all
	 * the various attributes between the <image> tags
	 * 
	 * @param url
	 * 
	 * @return list of FeedItem objects
	 */

	public static LinkedList<FeedItem> fetchAndParse()
			throws XmlPullParserException, ClientProtocolException,
			URISyntaxException, IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlpp = factory.newPullParser();

		// Put XML file into the parser
		xmlpp.setInput(new InputStreamReader(KT_TransferManager
				.getXMLFile()));

		if (findStartTag(TAG_DOCUMENT, xmlpp, TAG_DOCUMENT)) {
			return stepIntoDocument(xmlpp);
		} else
			return null;
	}

	private static LinkedList<FeedItem> stepIntoDocument(XmlPullParser xmlpp)
			throws XmlPullParserException, IOException {
		LinkedList<FeedItem> feedItems = new LinkedList<FeedItem>();
		while (findStartTag(TAG_FEED_ITEM, xmlpp, TAG_DOCUMENT))
			feedItems.add(stepIntoFeedItem(xmlpp));
		return feedItems;
	}

	private static FeedItem stepIntoFeedItem(XmlPullParser xmlpp)
			throws XmlPullParserException, IOException {
		FeedItem feedItem = new FeedItem();
		while (findStartTag(null, xmlpp, TAG_FEED_ITEM)) {
			String currentTag = xmlpp.getName();

			if (currentTag.equalsIgnoreCase(TAG_ITEM_ID))
				feedItem.post.put(KEY_ITEM_ID, Integer.valueOf(xmlpp.nextText().trim()));

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_URL_BIG))
				feedItem.post.put(KEY_ITEM_URL_BIG, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_URL_SMALL))
				feedItem.post.put(KEY_ITEM_URL_SMALL, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_USER_ID))
				feedItem.post.put(KEY_ITEM_USER_ID, Integer.valueOf(xmlpp.nextText().trim()));

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_USER_NAME))
				feedItem.post.put(KEY_ITEM_USER_NAME, xmlpp.nextText().trim());
			
			else if (currentTag.equalsIgnoreCase(TAG_ITEM_USER_AVATAR))
				feedItem.post.put(KEY_ITEM_USER_AVATAR, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_DESCRIPTION))
				feedItem.post.put(KEY_ITEM_DESCRIPTION, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_DATE))
				feedItem.post.put(KEY_ITEM_DATE, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_ITEM_COMMENTS))
				stepIntoComments(xmlpp, feedItem.comments);
		}

		feedItem.post.put(KEY_ITEM_NUM_OF_COMS, feedItem.comments.size());

		int itemID = feedItem.post.getAsInteger(KEY_ITEM_ID);
		for (ContentValues comment : feedItem.comments)
			comment.put(KEY_ITEM_ID, itemID);

		return feedItem;
	}

	private static void stepIntoComments(XmlPullParser xmlpp,
			LinkedList<ContentValues> comments) throws XmlPullParserException,
			IOException {
		while (findStartTag(TAG_COMMENT, xmlpp, TAG_ITEM_COMMENTS))
			comments.add(stepIntoComment(xmlpp));
	}

	private static ContentValues stepIntoComment(XmlPullParser xmlpp)
			throws XmlPullParserException, IOException {
		ContentValues comment = new ContentValues();
		while (findStartTag(null, xmlpp, TAG_COMMENT)) {
			String currentTag = xmlpp.getName();

			if (currentTag.equalsIgnoreCase(TAG_COM_ID))
				comment.put(KEY_COM_ID, Integer.valueOf(xmlpp.nextText().trim()));

			else if (currentTag.equalsIgnoreCase(TAG_COM_USER_ID))
				comment.put(KEY_COM_USER_ID, Integer.valueOf(xmlpp.nextText().trim()));

			else if (currentTag.equalsIgnoreCase(TAG_COM_USER_NAME))
				comment.put(KEY_COM_USER_NAME, xmlpp.nextText().trim());
			
			else if (currentTag.equalsIgnoreCase(TAG_COM_USER_AVATAR))
				comment.put(KEY_COM_USER_AVATAR, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_COM_CONTENT))
				comment.put(KEY_COM_CONTENT, xmlpp.nextText().trim());

			else if (currentTag.equalsIgnoreCase(TAG_COM_DATE))
				comment.put(KEY_COM_DATE, xmlpp.nextText().trim());
		}
		return comment;
	}

	/**
	 * Will continue search for the specified search tag. If not found if will
	 * stop at the specified halt end tag. The tag to find could be null and the
	 * method will then search for any start tag. If start tag is found, parser
	 * will be placed on that start tag. If not found parser will be placed at
	 * specified end tag.
	 * 
	 * @param tagToFind
	 *            Start tag to find. Could be null and then this function will
	 *            search for any start tag.
	 * @param xmlpp
	 *            Parser to use
	 * @param haltEndTag
	 *            The tag which end tag this method should stop searching at
	 */

	private static boolean findStartTag(String tagToFind, XmlPullParser xmlpp,
			String haltEndTag) throws XmlPullParserException, IOException {

		while (xmlpp.getEventType() != XmlPullParser.END_DOCUMENT) {
			xmlpp.next();
			if (xmlpp.getEventType() == XmlPullParser.END_TAG
					&& xmlpp.getName().equals(haltEndTag))
				return false;
			if (xmlpp.getEventType() == XmlPullParser.START_TAG) {
				if (tagToFind != null) {
					if (xmlpp.getName().equalsIgnoreCase(tagToFind))
						return true;
				} else
					return true;
			}
		}
		return false;
	}
}
