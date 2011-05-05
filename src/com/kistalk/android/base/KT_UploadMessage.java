package com.kistalk.android.base;

public class KT_UploadMessage {
	
	private final short messageTag;
	private int itemId;
	private String imagePath;
	private String comment;
	
	/* Default constructor
	 * @param imagePath - Path to image
	 * @param comment - user's comment */
	public KT_UploadMessage(String imagePath, String comment, int itemId, short messageTag) {
		this.setImagePath(imagePath);
		this.setComment(comment);
		this.setItemId(itemId);
		this.messageTag = messageTag;
	}

	/**
	 * @return the messageTag
	 */
	public short getMessageTag() {
		return messageTag;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
}
