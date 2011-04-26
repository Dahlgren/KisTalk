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
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import com.kistalk.android.activity.KisTalk;
import com.kistalk.android.base.Base64;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.content.ContentValues;

public class AndroidTransferManager implements Constant {

	final private int IMAGE_JPEG_QUALITY = 95;
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
		Uri uri = null;
		try {
			
			URL url = new URL(fileUrl.replace(" ", "%20"));
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.connect();
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = httpConnection.getInputStream();
				Bitmap image = BitmapFactory.decodeStream(is);

				File pathToImage = File.createTempFile("image", ".jpg",
						KisTalk.cacheDir);
				
				FileOutputStream fos = new FileOutputStream(pathToImage);
				image.compress(Bitmap.CompressFormat.JPEG, IMAGE_JPEG_QUALITY,
						fos);
				uri = Uri.fromFile(pathToImage);
			}
		} catch (IOException e) {
			Log.e("ERROR in downloading", e.toString());
		}
		return uri;
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

			/*
			 * Compresses the image of format JPEG with specified image quality
			 * to an output stream
			 */
			Bitmap imageToSend = readImageFromLocation(message
					.getAsString(KEY_UPLOAD_IMAGE_URI));

			/* Error check */
			if (imageToSend == null) {
				throw new NullPointerException();
			}

			imageToSend.compress(Bitmap.CompressFormat.JPEG,
					IMAGE_JPEG_QUALITY, byteArrayOutStream);
			byte[] byteArray = byteArrayOutStream.toByteArray(); // Converts the
																	// stream's
																	// contents
																	// to a byte
																	// array
			//String compressedImageString = Base64.encodeBytes(byteArray); // Converts
																			// byte
																			// array
																			// to
																			// Base64
																			// encoding
			MultipartEntity multipartEntity = new MultipartEntity();
			multipartEntity.addPart("multipart/form-data", new ByteArrayBody(byteArray, KEY_UPLOAD_IMAGE_URI));
			multipartEntity.addPart("multipart/form-data", new StringBody(KEY_UPLOAD_IMAGE_DESCRIPTION));
			
			HttpPost httpPost = new HttpPost(WEBSERVER);
			httpPost.setEntity(multipartEntity);

			client.execute(httpPost);
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
	public static InputStream getUrlData(String file)
			throws URISyntaxException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(new URI(WEBSERVER + file));
		HttpResponse res = client.execute(method);
		return res.getEntity().getContent();
	}
}
