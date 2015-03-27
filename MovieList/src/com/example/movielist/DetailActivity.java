package com.example.movielist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private SQLiteDatabase db;
	private EditText text_name;
	private EditText text_detail;
	private TextView text_color;
	private Button btn_edit_name;
	private Button btn_edit_detail;
	private Button btn_back;
	private int ID = 1;
	private String name;
	private int checked;
	private String detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		text_name = (EditText) findViewById(R.id.d_name);
		text_detail = (EditText) findViewById(R.id.d_detail);
		text_color = (TextView) findViewById(R.id.d_color);
		SQLiteOpenHelper helper = new ListSQLiteHelper(getApplicationContext(), "my.db", null, 2);
		db = helper.getWritableDatabase();

		Intent intent = getIntent();
		ID = intent.getExtras().getInt("DATA_ID", 1);
		initData(ID);

		btn_edit_name = (Button) findViewById(R.id.button_edit_name);
		btn_edit_name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = text_name.getText().toString();
				if (!str.equals("")) {
					editData(ID, "NAME", str);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		btn_edit_detail = (Button) findViewById(R.id.button_edit_detail);
		btn_edit_detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = text_detail.getText().toString();
				if (!str.equals("")) {
					editData(ID, "DETAIL", str);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		btn_back = (Button) findViewById(R.id.button_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = text_name.getText().toString();
				if (!str.equals("")) {
					editData(ID, "NAME", str);
				}
				String str2 = text_detail.getText().toString();
				if (!str.equals("")) {
					editData(ID, "DETAIL", str2);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
			}
		});
	}

	// 選択データをオブジェクト化
	private void initData(int id) {
		String[] columns = { "ID", "NAME", "CHECKED", "DETAIL" };
		String where = "ID=?";
		String[] params = { String.valueOf(id) };
		Cursor cursor = db.query("LIST_TABLE", columns, where, params, null, null, null);
		if (cursor.moveToNext()) {
			name = cursor.getString(cursor.getColumnIndex("NAME"));
			checked = cursor.getInt(cursor.getColumnIndex("CHECKED"));
			detail = cursor.getString(cursor.getColumnIndex("DETAIL"));
		}
		text_name.setText(name);
		text_detail.setText(detail);
		changeColor(checked);
	}

	// データの編集。
	private void editData(int id, String column, String str) {
		ContentValues values = new ContentValues();
		values.put(column, str);
		String where = "ID=?";
		String[] params = { String.valueOf(id) };
		db.update("LIST_TABLE", values, where, params);
	}

	private void changeColor(int checked) {
		if (checked == 0) {
			text_color.setBackgroundColor(Color.rgb(70, 80, 90));
		} else if (checked == 1) {
			text_color.setBackgroundColor(Color.rgb(0, 255, 0));
		} else if (checked == 2) {
			text_color.setBackgroundColor(Color.rgb(255, 215, 0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}
