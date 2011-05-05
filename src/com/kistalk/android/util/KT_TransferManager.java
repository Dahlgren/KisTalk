package com.kistalk.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.kistalk.android.activity.FeedActivity;
import com.kistalk.android.base.KT_UploadMessage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.util.Log;

public class KT_TransferManager implements Constant {

	private DefaultHttpClient client;
	private URL urlObject; // Creates a URL instance

	/* Default constructor */
	public KT_TransferManager() {
		client = new DefaultHttpClient();
		try {
			urlObject = new URL(WEBSERVER_URL);
		} catch (MalformedURLException e) {
			Log.e("Bad URL", e.toString());
		}
	}

	/**
	 * 
	 * @param imageUrl
	 *            url for image
	 * 
	 * @return path to image
	 */
	public String downloadImage(String imageUrl) {

		String delimiter = "://";
		String[] splittedString = imageUrl.split(delimiter);
		String scheme = splittedString[0];
		String hostAndPath = splittedString[1];

		String host = hostAndPath.split("[/].*")[0];
		String path = hostAndPath.split(host)[1];

		URL url;
		try {
			URI uri = new URI(scheme, host, path, null);
			url = uri.toURL();
		} catch (URISyntaxException e1) {
			Log.e(LOG_TAG, "Bad url");
			e1.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Bad url");
			e.printStackTrace();
			return null;
		}

		Log.i(LOG_TAG, KT_TransferManager.class + ": Downloading image at: "
				+ url);
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String type = httpConnection.getContentType();
				if (!(type.equals("image/jpeg") || type.equals("image/png") || type
						.equals("image/gif")))
					return null;

				BufferedInputStream buffInStream = new BufferedInputStream(
						httpConnection.getInputStream());

				byte[] imageData = new byte[httpConnection.getContentLength()];

				int filesize = httpConnection.getContentLength();
				int bytesRead = 0;
				int offset = 0;
				while (bytesRead != -1 && offset < filesize) {
					bytesRead = buffInStream.read(imageData, offset, filesize
							- offset);
					offset += bytesRead;
				}

				File pathToImage = File.createTempFile("image", ".jpg",
						FeedActivity.cacheDir);

				BufferedOutputStream buffOutStream = new BufferedOutputStream(
						new FileOutputStream(pathToImage));

				buffOutStream.write(imageData);

				Log.i(LOG_TAG, KT_TransferManager.class
						+ ": Downloaded image to: " + pathToImage);

				/* Clean up */
				buffInStream.close();
				buffOutStream.close();
				httpConnection.disconnect();

				return pathToImage.toString();
			} else
				Log.w(LOG_TAG, "Connection couldn't be established for " + url);
		} catch (IOException e) {
			Log.e(LOG_TAG, "ERROR when downloading/reading/writing " + url, e);
		}

		return null;
	}

	/*
	 * Upload an specified image with a description to server
	 * 
	 * @param message
	 */
	public void uploadPhotoMessage(KT_UploadMessage message) {

		/* Error check */
		if (message == null) {
			Log.e(LOG_TAG, "Bad upload message");
			throw new NullPointerException();
		}

		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) urlObject.openConnection(); // Opens
																				// a
																				// bi-directional
																				// connection
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}
		httpConnection.setChunkedStreamingMode(0); // All bytes must
													// be transmitted as a whole
													// package

		httpConnection.setRequestProperty("METHOD", "POST"); // Sets property
																// to
																// header field

		/* Return code from HTTP server */
		int responseCode = 0;
		try {
			responseCode = httpConnection.getResponseCode();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}

		if (responseCode == HttpURLConnection.HTTP_OK) {

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			// MultipartEntity multipartEntity = new MultipartEntity();

			FileBody fileBody = new FileBody(new File(message.getImagePath()));

			StringBody imageDescription = null;
			try {
				imageDescription = new StringBody(message.getComment(),
						Charset.forName(HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			}

			try {
				StringBody username = new StringBody(FeedActivity.getUsername());
				multipartEntity.addPart(ARG_USERNAME, username);
				StringBody token = new StringBody(FeedActivity.getToken());
				multipartEntity.addPart(ARG_TOKEN, token);
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG, "StringBody failure", e);
				e.printStackTrace();
			}

			multipartEntity.addPart(ARG_UPLOAD_IMAGE, fileBody);

			multipartEntity.addPart(ARG_UPLOAD_DESCRIPTION, imageDescription);

			HttpPost httpPost = new HttpPost(WEBSERVER_URL + UPLOAD_IMAGE_PATH);

			httpPost.setEntity(multipartEntity);

			// TODO: Add HttpResponse response for response handling
			try {
				client.execute(httpPost);

			} catch (ClientProtocolException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			}
		}
		/* Clean up */
		httpConnection.disconnect();
	}

	public Bitmap readImageFromLocation(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/*
	 * Returns an inputstream from a specified URL link
	 * 
	 * @param url
	 * 
	 * @return Url data in a InputStream
	 */
	public static InputStream getXMLFile() throws URISyntaxException,
			ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();

		Uri uri = new Uri.Builder().scheme(SCHEME).authority(HOST)
				.path(XML_FILE_PATH)
				.appendQueryParameter(ARG_USERNAME, FeedActivity.getUsername())
				.appendQueryParameter(ARG_TOKEN, FeedActivity.getToken())
				.build();

		HttpGet method = new HttpGet(new URI(uri.toString()));

		HttpResponse res = client.execute(method);
		return res.getEntity().getContent();
	}

	public void uploadComment(KT_UploadMessage message) {
		/* Error check */
		if (message == null) {
			Log.e(LOG_TAG, "Bad comment message");
			throw new NullPointerException();
		}

		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) urlObject.openConnection();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}
		// All bytes must be transmitted as a whole package
		httpConnection.setChunkedStreamingMode(0);

		// Sets property to header field
		httpConnection.setRequestProperty("METHOD", "POST");

		/* Return code from HTTP server */
		int responseCode = 0;
		try {
			responseCode = httpConnection.getResponseCode();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}

		if (responseCode == HttpURLConnection.HTTP_OK) {

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			// MultipartEntity multipartEntity = new MultipartEntity();

			try {
				StringBody image_id = new StringBody(Integer.toString(message
						.getItemId()));
				multipartEntity.addPart(ARG_COMMENT_ITEMID, image_id);

				StringBody content = new StringBody(message.getComment(),
						Charset.forName(HTTP.UTF_8));
				multipartEntity.addPart(ARG_COMMENT_CONTENT, content);

				StringBody username = new StringBody(FeedActivity.getUsername());
				multipartEntity.addPart(ARG_USERNAME, username);

				StringBody token = new StringBody(FeedActivity.getToken());
				multipartEntity.addPart(ARG_TOKEN, token);

			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG, "StringBody failure", e);
				e.printStackTrace();
			}

			HttpPost httpPost = new HttpPost(WEBSERVER_URL + POST_COMMENT_PATH);

			httpPost.setEntity(multipartEntity);

			// TODO: Add HttpResponse response for response handling
			try {
				client.execute(httpPost);

			} catch (ClientProtocolException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			}
		}
		/* Clean up */
		httpConnection.disconnect();

	}

	public boolean validate(String username, String token) {
		if (testConnection()) {

			Uri uri = new Uri.Builder().scheme(SCHEME).authority(HOST)
					.path(VALIDATE_CREDENTIAL_PATH)
					.appendQueryParameter(ARG_USERNAME, username)
					.appendQueryParameter(ARG_TOKEN, token).build();

			try {
				HttpPost method = new HttpPost(new URI(uri.toString()));
				HttpResponse res = client.execute(method);

				if (res.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
					return true;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean testConnection() {

		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) urlObject.openConnection();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}
		// All bytes must be transmitted as a whole package
		httpConnection.setChunkedStreamingMode(0);

		// Sets property to header field
		httpConnection.setRequestProperty("METHOD", "POST");

		/* Return code from HTTP server */
		int responseCode = 0;
		try {
			responseCode = httpConnection.getResponseCode();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			e.printStackTrace();
		}

		if (responseCode == HttpURLConnection.HTTP_OK)
			return true;
		else {
			httpConnection.disconnect();
			return false;
		}
	}
}
