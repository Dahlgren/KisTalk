package com.kistalk.android.base;

public class KT_UploadCommentMessage {
	
	private int itemId;
	private String comment;

	public KT_UploadCommentMessage(int itemId, String comment) {
		this.setItemId(itemId);
		this.comment = comment;
	}
	
	public KT_UploadCommentMessage() {
		this(-1, null);
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

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemId() {
		return itemId;
	}

	
}
