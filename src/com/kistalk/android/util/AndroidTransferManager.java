package com.kistalk.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kistalk.android.activity.KisTalk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.content.ContentValues;

public class AndroidTransferManager implements Constant {

	private final static String TAG = "AndroidTransferManager";

	final private int DOWNLOAD_IMAGE_QUALITY = 95;
	final private int UPLOAD_IMAGE_QUALITY = 75;
	private DefaultHttpClient client;
	private URL urlObject; // Creates a URL instance

	/* Default constructor */
	public AndroidTransferManager() {
		client = new DefaultHttpClient();
		try {
			urlObject = new URL(WEBSERVER);
		} catch (MalformedURLException e) {
			Log.e("Bad URL", e.toString());
		}
	}

	public Uri downloadImage(String fileUrl) {

		String delimiter = "://";
		String[] splittedString = fileUrl.split(delimiter);
		if (splittedString.length == 2) {
			String encodedAdress = URLEncoder.encode(splittedString[1])
					.replace("%2F", "/");
			String encodedUrl = splittedString[0] + delimiter + encodedAdress;
			
			Log.i(TAG, "Downloading image at: " + encodedUrl);
			try {
				URL url = new URL(encodedUrl);
				HttpURLConnection httpConnection = (HttpURLConnection) url
						.openConnection();
				int responseCode = httpConnection.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inStream = httpConnection.getInputStream();
					Bitmap image = BitmapFactory.decodeStream(inStream);

					File pathToImage = File.createTempFile("image", ".jpg",
							KisTalk.cacheDir);

					Log.i(TAG, pathToImage.getPath() + " filesize is: " + pathToImage.length());
					
					FileOutputStream fos = new FileOutputStream(pathToImage);
					image.compress(Bitmap.CompressFormat.JPEG,
							DOWNLOAD_IMAGE_QUALITY, fos);
					
					/* Clean up */
					inStream.close();
					fos.close();
					httpConnection.disconnect();
					
					return Uri.fromFile(pathToImage);
				} else
					Log.w(TAG, "Connection couldn't be established");
			} catch (IOException e) {
				Log.e(TAG, "ERROR in downloading", e);
			}
		} else
			Log.e(TAG, "Bad url");
		return null;
	}
	
	/*
	 * Upload an specified image with a description to server
	 * 
	 * @param message
	 */
	public void uploadMessage(ContentValues message) throws URISyntaxException,
			ClientProtocolException, IOException {
		HttpURLConnection httpConnection = (HttpURLConnection) urlObject
				.openConnection(); // Opens a bi-directional connection
		httpConnection.setDoOutput(true); // Allow output
		httpConnection.setChunkedStreamingMode(0); // All bytes must
													// be transmitted as a whole
													// package

		httpConnection.setRequestProperty("METHOD", "POST"); // Sets property
																// to
																// header field
		
		int responseCode = httpConnection.getResponseCode(); // Return code from
																// remote HTTP
																// server

		if (responseCode == HttpURLConnection.HTTP_OK) {

			ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
			Bitmap imageToSend = readImageFromLocation(message
					.getAsString(KEY_UPLOAD_IMAGE_URI));

			/* Error check */
			if (imageToSend == null) {
				Log.e(TAG, "Unable to read file to upload");
				throw new NullPointerException();
			}

			/*
			 * Compresses the image of format JPEG with specified image quality
			 * to an output stream
			 */
			imageToSend.compress(Bitmap.CompressFormat.JPEG,
					UPLOAD_IMAGE_QUALITY, byteArrayOutStream);
			byte[] data = byteArrayOutStream.toByteArray(); // Converts the
																	// stream's
																	// contents
																	// to a byte
																	// array

			ByteArrayBody imageDataArray = new ByteArrayBody(data, message.getAsString(KEY_UPLOAD_IMAGE_URI));
			StringBody imageDescription = new StringBody(message.getAsString(KEY_UPLOAD_IMAGE_DESCRIPTION));
			
			MultipartEntity multipartEntity = new MultipartEntity();
			multipartEntity.addPart("", imageDataArray);
			multipartEntity.addPart("", imageDescription);
			
			HttpPost httpPost = new HttpPost(WEBSERVER);
			httpPost.setEntity(multipartEntity);

			// Add HttpResponse response for response handling
			client.execute(httpPost);
			
			/* Clean up */
			byteArrayOutStream.close();
		}
		httpConnection.disconnect();
		
	}

	public Bitmap readImageFromLocation(String uri) {
		return BitmapFactory.decodeFile(uri.toString());
	}

	/*
	 * Returns an inputstream from a specified URL link
	 * 
	 * @param url
	 * 
	 * @return Url data in a InputStream
	 */
	public static InputStream getXMLFile()
			throws URISyntaxException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(new URI(ANDROID_XML_FILE));
		HttpResponse res = client.execute(method);
		return res.getEntity().getContent();
	}
}
