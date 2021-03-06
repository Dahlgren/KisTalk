package com.kistalk.android.activity;

import com.kistalk.android.R;
import com.kistalk.android.base.KT_UploadMessage;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.UploadTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UploadActivity extends Activity implements Constant {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_view_layout);

		final String path = this.getIntent().getStringExtra(
				KEY_UPLOAD_IMAGE_PATH);
		Bitmap storedImage = BitmapFactory.decodeFile(path);

		/*
		 * Create an OnClickListener
		 * 
		 * TODO: Improve the OnClickListener
		 */
		OnClickListener onCL = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.send_button) {
					KT_UploadMessage message = new KT_UploadMessage(path,
							((EditText) findViewById(R.id.inputbox)).getText()
									.toString(), -1, UPLOAD_PHOTO_MESSAGE_TAG);
					new UploadTask(UploadActivity.this).execute(message);
				} else if (v.getId() == R.id.upload_image) {
					showDialog(DIALOG_CHOOSE_OPTION_ID);
				}
			}
		};

		ImageView uploadImage = (ImageView) findViewById(R.id.upload_image);
		uploadImage.setImageBitmap(storedImage);
		uploadImage.setOnClickListener(onCL);

		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(onCL);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_CHOOSE_OPTION_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick an option").setCancelable(true)
					.setItems(OPTIONS, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if (0 == id) {
								showFileChooser();
							} else if (1 == id) {
								takePhotoAction();
							}
						}
					});
			return builder.create();

		default:
			dialog = null;
		}
		return dialog;
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
	}

	private void takePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_GET_CAMERA_PIC);
	}

	/*
	 * TODO: Add the possibility to modify the path variable in the original
	 * OnClickListener
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {

			Uri recievedUri = intent.getData();
			if (recievedUri != null) {

				String realPath = getRealPathFromURI(recievedUri);
				if (requestCode == REQUEST_GET_CAMERA_PIC) {
					((ImageView) findViewById(R.id.upload_image))
							.setImageBitmap(BitmapFactory.decodeFile(realPath));
				}
				if (requestCode == REQUEST_CHOOSE_IMAGE) {
					((ImageView) findViewById(R.id.upload_image))
							.setImageBitmap(BitmapFactory.decodeFile(realPath));
				}
			}
		}
	}

	// Convert the image URI to the direct file system path of the image file
	private String getRealPathFromURI(Uri contentUri) {

		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
}
