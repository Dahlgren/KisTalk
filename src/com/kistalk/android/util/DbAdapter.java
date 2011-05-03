// See NotesDbAdapter
package com.kistalk.android.util;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter implements Constant {

	public static final String KEY_ROWID = "_id";
	// Other keys are in AndXMLParser.java

	private static final String DB_NAME = "kistalk_db";
	private static final String DB_TABLE_POSTS = "posts";
	private static final String DB_TABLE_COMMENTS = "comments";
	private static final int DATABASE_VERSION = 16;

	private static final String DB_CREATE_TABLE_POSTS = "create table "
			+ DB_TABLE_POSTS + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_ITEM_ID
			+ " integer not null, " + KEY_ITEM_URL_BIG + " text, "
			+ KEY_ITEM_URL_SMALL + " text, " + KEY_ITEM_USER_ID + " integer, "
			+ KEY_ITEM_USER_NAME + " text, " + KEY_ITEM_USER_AVATAR + " text, "
			+ KEY_ITEM_DESCRIPTION + " text, " + KEY_ITEM_DATE + " text, "
			+ KEY_ITEM_NUM_OF_COMS + " integer);";

	private static final String DB_CREATE_TABLE_COMMENTS = "create table "
			+ DB_TABLE_COMMENTS + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_ITEM_ID
			+ " integer not null, " + KEY_COM_ID + " integer not null, "
			+ KEY_COM_USER_ID + " integer, " + KEY_COM_USER_NAME + " text, "
			+ KEY_COM_USER_AVATAR + " text, "
			+ KEY_COM_CONTENT + " text, " + KEY_COM_DATE + " text);";

	/*
	 * Instance variables
	 */
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Context mCtx;

	/*
	 * Private static class DatabaseHelper which manages creation and upgrading
	 * of database
	 */

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_TABLE_POSTS);
			db.execSQL(DB_CREATE_TABLE_COMMENTS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			dropDbTables(db);
			onCreate(db);
		}

		public void dropDbTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_POSTS);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_COMMENTS);
		}
	} // end of private class DatabaseHelper

	public DbAdapter(Context ctx) {
		mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void insertComments(LinkedList<ContentValues> comments) {
		for (ContentValues comment : comments)
			mDb.insert(DB_TABLE_COMMENTS, null, comment);
	}

	public void insertPost(ContentValues post) {
		if (mDb.insert(DB_TABLE_POSTS, null, post) == -1)
			Log.e(LOG_TAG, "Error while inserting post to db");
	}

	public boolean deleteNote(long rowId) {
		return mDb.delete(DB_TABLE_POSTS, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void deleteAll() {
		mDb.delete(DB_TABLE_POSTS, null, null);
		mDb.delete(DB_TABLE_COMMENTS, null, null);
	}

	public Cursor fetchAllPosts() {
		return mDb.query(DB_TABLE_POSTS, null, null, null, null, null, null);
	}

	public Cursor fetchComments(int itemId) {
		return mDb.query(DB_TABLE_COMMENTS, null, KEY_ITEM_ID + "=" + itemId,
				null, null, null, null);
	}

	public Cursor fetchPost(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, DB_TABLE_POSTS, null, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchPostFromId(long itemId) throws SQLException {

		Cursor mCursor = mDb.query(true, DB_TABLE_POSTS, null, KEY_ITEM_ID
				+ "=" + itemId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}