package com.kistalk.android.activity;


import com.kistalk.android.R;
import com.kistalk.android.base.UserMessage;
import com.kistalk.android.util.Constant;
import com.kistalk.android.util.UploadPhotoTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UploadPhoto extends Activity implements Constant {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_image_view);
		
		final String path = this.getIntent().getStringExtra(KEY_UPLOAD_IMAGE_URI);
		Bitmap storedImage = BitmapFactory.decodeFile(path);
		
		ImageView uploadImage = (ImageView) findViewById(R.id.upload_image);
		uploadImage.setImageBitmap(storedImage);
		
		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.send_button) {
					UserMessage message = new UserMessage(path, (String) ((EditText) findViewById(R.id.inputbox)).getText().toString());
					new UploadPhotoTask(UploadPhoto.this).execute(message);
				}
			}
		});
	}
}
