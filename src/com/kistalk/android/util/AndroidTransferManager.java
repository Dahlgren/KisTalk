package com.kistalk.android.util;

import java.io.ByteArrayOutputStream;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.kistalk.android.activity.KisTalk;
import com.kistalk.android.base.UserMessage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class AndroidTransferManager implements Constant {

	private final static String LOG_TAG = "util.KisTalk.AndroidTransferManager";

	final private int DOWNLOAD_IMAGE_QUALITY = 95;
	final private int UPLOAD_IMAGE_QUALITY = 75;
	private DefaultHttpClient client;
	private URL urlObject; // Creates a URL instance

	/* Default constructor */
	public AndroidTransferManager() {
		client = new DefaultHttpClient();
		try {
			urlObject = new URL(WEBSERVER_URL);
		} catch (MalformedURLException e) {
			Log.e("Bad URL", e.toString());
		}
	}

	public Uri downloadImage(String fileUrl) {

		String delimiter = "://";
		String[] splittedString = fileUrl.split(delimiter);
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

		Log.i(LOG_TAG, "Downloading image at: " + url);
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inStream = httpConnection.getInputStream();
				Bitmap image = BitmapFactory.decodeStream(inStream);

				File pathToImage = File.createTempFile("image", ".jpg",
						KisTalk.cacheDir);

				Log.i(LOG_TAG, pathToImage.getPath() + " filesize is: "
						+ pathToImage.length());

				FileOutputStream fos = new FileOutputStream(pathToImage);
				image.compress(Bitmap.CompressFormat.JPEG,
						DOWNLOAD_IMAGE_QUALITY, fos);

				/* Clean up */
				inStream.close();
				fos.close();
				httpConnection.disconnect();

				return Uri.fromFile(pathToImage);
			} else
				Log.w(LOG_TAG, "Connection couldn't be established");
		} catch (IOException e) {
			Log.e(LOG_TAG, "ERROR in downloading", e);
		}

		return null;
	}

	/*
	 * Upload an specified image with a description to server
	 * 
	 * @param message
	 */
	public void uploadMessage(UserMessage message) {

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

			ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
			Bitmap imageSend = readImageFromLocation(message.getImagePath());

			/* Error check */
			if (imageSend == null) {
				Log.e(LOG_TAG, "Unable to read image");
				throw new NullPointerException();
			}

			/*
			 * Compresses the image of format JPEG with specified image quality
			 * to an output stream
			 */
			imageSend.compress(Bitmap.CompressFormat.JPEG,
					UPLOAD_IMAGE_QUALITY, byteArrayOutStream);
			byte[] data = byteArrayOutStream.toByteArray(); // Converts the
															// stream's
															// contents
															// to a byte
															// array

			ByteArrayBody imageDataArray = new ByteArrayBody(data, null);
			StringBody imageDescription = null;
			try {
				imageDescription = new StringBody(message.getComment());
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			}

			MultipartEntity outerMultipartEntity = new MultipartEntity();

			MultipartEntity innerMultipartEntity = new MultipartEntity();

			try {
				StringBody username = new StringBody("zoger");
				StringBody token = new StringBody("k1igvh1xyg");

				outerMultipartEntity.addPart(UPLOAD_ARG_USERNAME, username);
				outerMultipartEntity.addPart(UPLOAD_ARG_TOKEN, token);
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			innerMultipartEntity.addPart(UPLOAD_ARG_IMAGE, imageDataArray);
			innerMultipartEntity.addPart(UPLOAD_ARG_DESCRIPTION,
					imageDescription);

			InputStreamBody inputStreamBody = null;
			try {
				inputStreamBody = new InputStreamBody(
						innerMultipartEntity.getContent(), UPLOAD_ARG_PICTURE);
			} catch (UnsupportedOperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			outerMultipartEntity.addPart(UPLOAD_ARG_PICTURE, inputStreamBody);

			HttpPost httpPost = new HttpPost(WEBSERVER_URL + "/api/images/new");
			httpPost.setEntity(outerMultipartEntity);

			// Add HttpResponse response for response handling
			try {
				client.execute(httpPost);
			} catch (ClientProtocolException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
				e.printStackTrace();
			}

			/* Clean up */
			try {
				byteArrayOutStream.close();
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
				.appendQueryParameter(UPLOAD_ARG_USERNAME, KisTalk.getUsername())
				.appendQueryParameter(UPLOAD_ARG_TOKEN, KisTalk.getToken()).build();

		HttpGet method = new HttpGet(new URI(uri.toString()));

		HttpResponse res = client.execute(method);
		return res.getEntity().getContent();
	}
}
