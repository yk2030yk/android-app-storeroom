package com.example.movielist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ListSQLiteHelper extends SQLiteOpenHelper {
	public ListSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE LIST_TABLE(" + "ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME TEXT," + "CHECKED INTEGER DEFAULT 0," + "DETAIL TEXT," + "DEL INTEGER DEFAULT 0," + "LISTID INTEGER DEFAULT 0)";
		db.execSQL(sql);

		String sql2 = "CREATE TABLE LIST_NAME_TABLE(" + "ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME TEXT," + "DEL INTEGER DEFAULT 0)";
		db.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
