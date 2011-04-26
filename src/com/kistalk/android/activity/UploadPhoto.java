package com.kistalk.android.activity;


import com.kistalk.android.util.Constant;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;

public class UploadPhoto extends Activity implements Constant {
	
	
	
	void sendPhoto(){
		Uri uri = Uri.parse("hej");
		String description = "hej";
		
		ContentValues content = new ContentValues();
		content.put(KEY_UPLOAD_IMAGE_URI, uri.toString());
		content.put(KEY_UPLOAD_IMAGE_DESCRIPTION, description);
		
	}
	

}
