package com.example.movielist;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private static final int RESULTCODE = 1;
	private SQLiteDatabase db;
	private Button btn_add;
	private ListView listView;
	private CustomAdapter2 adapter;
	private ArrayList<ListNameData> data = new ArrayList<ListNameData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		SQLiteOpenHelper helper = new ListSQLiteHelper(getApplicationContext(), "my.db", null, 1);
		db = helper.getWritableDatabase();
		listView = (ListView) findViewById(R.id.listView1);
		adapter = new CustomAdapter2(this, R.layout.row2, data);
		listView.setAdapter(adapter);

		setData();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(HomeActivity.this, ListActivity.class);
				intent.putExtra("id", adapter.getItem(position).getId());
				startActivityForResult(intent, RESULTCODE);
				overridePendingTransition(R.animator.in_right, R.animator.out_left);
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final ListNameData lnd = adapter.getItem(arg2);
				new AlertDialog.Builder(HomeActivity.this).setTitle("çÌèúÇµÇ‹Ç∑Ç©ÅH").setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						deleteData(lnd.getId());
						setData();
					}
				}).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
				}).show();
				return true;
			}
		});

		btn_add = (Button) findViewById(R.id.h_btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final EditText editView = new EditText(HomeActivity.this);
				new AlertDialog.Builder(HomeActivity.this).setView(editView).setPositiveButton("add", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String str = editView.getText().toString();
						addData(str);
						setData();
					}
				}).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
				}).show();
			}
		});
	}

	void setData() {
		adapter.clear();
		String[] columns = { "ID", "NAME", "DEL" };
		Cursor cursor = db.query("LIST_NAME_TABLE", columns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("ID"));
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			int del = cursor.getInt(cursor.getColumnIndex("DEL"));
			adapter.add(new ListNameData(id, name, del));
		}
		cursor.close();
		adapter.notifyDataSetChanged();
	}

	void addData(String str) {
		ContentValues values = new ContentValues();
		values.put("NAME", str);
		db.insert("LIST_NAME_TABLE", null, values);
	}

	void deleteData(int id) {
		String where = "ID=?";
		String[] whereArgs = { String.valueOf(id) };
		db.delete("LIST_NAME_TABLE", where, whereArgs);
		String where2 = "LISTID=?";
		db.delete("LIST_TABLE", where2, whereArgs);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SQLiteOpenHelper helper = new ListSQLiteHelper(getApplicationContext(), "my.db", null, 1);
		db = helper.getWritableDatabase();
		setData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULTCODE) {
			if (resultCode == RESULT_OK) {
			}
		}
	}

	public class CustomAdapter2 extends ArrayAdapter<ListNameData> {
		Context mycontext;
		private LayoutInflater layoutInflater;
		int viewResourceId;
		ArrayList<ListNameData> dataList;
		boolean isCheck = false;

		public CustomAdapter2(Context context, int viewResourceId, ArrayList<ListNameData> list) {
			super(context, viewResourceId, list);
			mycontext = context;
			this.viewResourceId = viewResourceId;
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dataList = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = layoutInflater.inflate(viewResourceId, null);
			}
			ListNameData l = dataList.get(position);
			TextView t = (TextView) view.findViewById(R.id.item_title2);
			t.setText(l.getName());
			return view;
		}
	}
}
