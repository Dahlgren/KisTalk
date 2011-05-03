package com.kistalk.android.base;

public class KT_UploadPhotoMessage {
	
	private String imagePath;
	private String comment;
	
	/* Default constructor
	 * @param imagePath - Path to image
	 * @param comment - user's comment */
	public KT_UploadPhotoMessage(String imagePath, String comment) {
		this.imagePath = imagePath;
		this.comment = comment;
	}
	
	public KT_UploadPhotoMessage() {
		this(null, null);
	}
	
	/* Sets the message's path to image
	 * @param imagePath */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	/* Sets the message's user's comment 
	 * @param comment */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/* Gets the message's comment 
	 * @return comment */
	public String getComment() {
		return this.comment;
	}

	/* Gets the message's path to image 
	 * @return imagePath */
	public String getImagePath() {
		return this.imagePath;
	}
	
}
