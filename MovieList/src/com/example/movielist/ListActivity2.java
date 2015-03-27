package com.example.movielist;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity2 extends Activity {
	private SQLiteDatabase db;
	private CustomAdapter adapter;
	private ArrayList<ListData> data = new ArrayList<ListData>();
	private MyList2 listView;
	private EditText textForm;
	private Button btn_open;
	private Button btn_add;
	private Button btn_del;
	private TextView modeLabel;
	private ArrayList<Integer> delData = new ArrayList<Integer>();
	public Context mycontext;
	private boolean del_mode = false;
	Toast toast_del;
	Toast toast_normal;
	public int tid;
	public GestureDetector gesturedetector;
	private int displayWidth;

	@SuppressWarnings("deprecation")
        @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list2);
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		displayWidth = disp.getWidth();

		Intent intent = getIntent();
		tid = intent.getExtras().getInt("id");
		SQLiteOpenHelper helper = new ListSQLiteHelper(getApplicationContext(), "my.db", null, 1);
		db = helper.getWritableDatabase();
		listView = (MyList2) findViewById(R.id.listView12);
		adapter = new CustomAdapter(this, R.layout.row3, data);
		listView.setAdapter(adapter);
		setData();
		modeLabel = (TextView) findViewById(R.id.mode2);
		modeLabel.setText("nomal mode");
		mycontext = this;
		textForm = (EditText) findViewById(R.id.edittext_top2);
		toast_del = Toast.makeText(this, "削除モードになりました。", Toast.LENGTH_SHORT);
		toast_del.setGravity(Gravity.CENTER, 0, 0);
		toast_normal = Toast.makeText(this, "通常モードになりました。", Toast.LENGTH_SHORT);
		toast_normal.setGravity(Gravity.CENTER, 0, 0);

		btn_open = (Button) findViewById(R.id.button_add2);
		btn_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ok", "l onClick");
				// キーボードの展開をするだけ
				if (!del_mode) {
					textForm.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		btn_del = (Button) findViewById(R.id.button_sdel2);
		btn_del.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				modeLabel.setText("clicked");
				if (del_mode) {
					initDel();
					adapter.notifyDataSetChanged();

					textForm.setEnabled(true);
					btn_open.setText("keyboard");
					btn_add.setText("+");
					btn_del.setText("change mode");
					modeLabel.setText("nomal mode");
					modeLabel.setBackgroundResource(R.drawable.mode_title);
					modeLabel.setTextColor(Color.rgb(255, 255, 255));
					del_mode = false;
					toast_normal.show();
				} else {
					textForm.setEnabled(false);
					btn_open.setText("");
					btn_add.setText("");
					btn_del.setText("delete");
					modeLabel.setText("delete mode");
					modeLabel.setBackgroundResource(R.drawable.mode_title2);
					modeLabel.setTextColor(Color.rgb(245, 0, 0));
					del_mode = true;
					toast_del.show();
				}
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(30);
				return true;
			}
		});

		btn_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ok", "l onClickItem");
				// デリートモードの時削除
				// モード変更の長押しの後、クリック判定が入ってしまうため
				// １回目の判定は無視するようにする
				if (del_mode) {
					createConfirmDel();
				}
			}
		});

		btn_add = (Button) findViewById(R.id.button_add22);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ok", "l onClick");
				// テキスト入力の値をデータベースに追加
				if (!del_mode) {
					String str = textForm.getText().toString();
					if (!str.equals("")) {
						addData(str);
						setData();
					}
					textForm.setText("");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		gesturedetector = new GestureDetector(this, onGestureListener);
		// リストがイベントを受け取る場合(検証済み)
		listView.setGes(gesturedetector);
		// 画面全体がイベントを受け取る場合(未検証)
		// MyLayout2 mylayout=(MyLayout2)findViewById(R.id.mylayout);
		// mylayout.setGes(gesturedetector);
	}

	// イベント処理
	private final OnGestureListener onGestureListener = new OnGestureListener() {
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d("ok", "l onDown");
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y) {
			float lx = e1.getX() - e2.getX();
			float ly = e1.getY() - e2.getY();
			float mlx = Math.abs(lx);
			float mly = Math.abs(ly);
			if (mlx > displayWidth / 3 && mly < 100) {
				if (e1.getX() < e2.getX()) {
					Intent i = new Intent(ListActivity2.this, ListActivity.class);
					i.putExtra("id", tid);
					startActivity(i);
					overridePendingTransition(R.animator.out_right, R.animator.in_left);
				} else if (e1.getX() >= e2.getX()) {
					
				}
			}
			Log.d("ok", "l onFling");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d("ok", "l onLongPress");

		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			// TODO Auto-generated method stub
			Log.d("ok", "l onScroll");
			return true;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d("ok", "l onShowPress");

		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d("ok", "l onSingleTapUp");
			return false;
		}

	};

	// DBデータをアダプターにセット
	private void setData() {
		adapter.clear();
		String[] columns = { "ID", "NAME", "DETAIL", "CHECKED", "DEL", "LISTID" };
		String where = "LISTID=?";
		String[] whereArgs = { String.valueOf(tid) };
		Cursor cursor = db.query("LIST_TABLE", columns, where, whereArgs, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("ID"));
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			int checked = cursor.getInt(cursor.getColumnIndex("CHECKED"));
			int del = cursor.getInt(cursor.getColumnIndex("DEL"));
			int tid = cursor.getInt(cursor.getColumnIndex("LISTID"));
			ListData ldata = new ListData(id, name, checked, del, tid);
			ldata.setDetail(cursor.getString(cursor.getColumnIndex("DETAIL")));
			adapter.add(ldata);
		}
		cursor.close();
		adapter.notifyDataSetChanged();
	}

	// DBにデータを追加
	private void addData(String str) {
		ContentValues values = new ContentValues();
		values.put("NAME", str);
		values.put("LISTID", tid);
		db.insert("LIST_TABLE", null, values);
	}

	// idのデータを削除
	private void deleteData(int id) {
		String where = "ID=?";
		String[] whereArgs = { String.valueOf(id) };
		db.delete("LIST_TABLE", where, whereArgs);
	}

	// idのデータをstrに変更
	@SuppressWarnings("unused")
        private void editData(int id, String str) {
		ContentValues values = new ContentValues();
		values.put("NAME", str);
		String where = "ID=?";
		String[] params = { String.valueOf(id) };
		db.update("LIST_TABLE", values, where, params);
	}

	// 削除確認を行い、okの時dbから削除
	private void createConfirmDel() {
		AlertDialog.Builder alert = new AlertDialog.Builder(mycontext);
		alert.setTitle("削除してよろしいですか?");
		alert.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				delete();
				setData();
			}
		});
		alert.show();
	}

	// チェックが入っているデータを削除
	// チェックが入っている=watchedの値が3の時をする。
	private void delete() {
		for (int i = 0; i < adapter.getCount(); i++) {
			ListData item = adapter.getItem(i);
			if (item.del == 1) {
				delData.add(item.getId());
			}
		}
		for (int i = 0; i < delData.size(); i++) {
			deleteData(delData.get(i));
		}
		delData.clear();
	}

	// 削除データの選択をオフにする
	private void initDel() {
		ContentValues values;
		for (int i = 0; i < adapter.getCount(); i++) {
			ListData item = adapter.getItem(i);
			values = new ContentValues();
			String where = "ID=?";
			String[] params = { String.valueOf(item.getId()) };
			values.put("DEL", 0);
			db.update("LIST_TABLE", values, where, params);
			item.del = 0;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		db.close();
	}

	// 他アクティビティが出て戻ってきたとき実行
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
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	// リストのカスタムアダプタークラス
	public class CustomAdapter extends ArrayAdapter<ListData> {
		public Context mycontext;
		private LayoutInflater layoutInflater;
		private int viewResourceId;
		private ArrayList<ListData> dataList;

		public CustomAdapter(Context context, int viewResourceId, ArrayList<ListData> list) {
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

			final ListData selectedData = dataList.get(position);
			final TextView mainText = (TextView) view.findViewById(R.id.item_title2);
			TextView subText = (TextView) view.findViewById(R.id.item_main);
			mainText.setText(selectedData.getName());
			subText.setText(selectedData.getDetail());
			final View color = (View) view.findViewById(R.id.color2);
			changeColor(selectedData, color);
			changeColor2(selectedData, mainText);

			// リストアイテムの右のボタン
			// レイアウトのクリック判定を使い、
			// レイアウト自体をボタンとしている。
			LinearLayout btn_right = (LinearLayout) view.findViewById(R.id.layout_btn2);
			btn_right.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// デリーとモードでないとき、左の色を変える
					if (!del_mode) {
						changeWatchedData(selectedData);
						changeColor(selectedData, color);
						Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(30);
					}
				}
			});

			mainText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// デリーとモードのとき、リストアイテムの色を変える
					if (del_mode) {
						changeDeleteData(selectedData);
						changeColor2(selectedData, mainText);
						Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(30);
					}
				}
			});

			mainText.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					if (!del_mode) {
						Intent intent = new Intent(ListActivity2.this, DetailActivity.class);
						intent.putExtra("DATA_ID", selectedData.id);
						startActivity(intent);
						Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(30);
					}
					// trueを返さないとonClickが発生
					return true;
				}
			});

			return view;
		}

		private void changeWatchedData(ListData selectData) {
			SQLiteOpenHelper helper = new ListSQLiteHelper(mycontext.getApplicationContext(), "my.db", null, 1);
			SQLiteDatabase db2 = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String where = "ID=?";
			String[] params = { String.valueOf(selectData.getId()) };
			if (selectData.getChecked() == 0) {
				values.put("CHECKED", 1);
				db2.update("LIST_TABLE", values, where, params);
				selectData.checked = 1;
			} else if (selectData.getChecked() == 1) {
				values.put("CHECKED", 2);
				db2.update("LIST_TABLE", values, where, params);
				selectData.checked = 2;
			} else if (selectData.getChecked() == 2) {
				values.put("CHECKED", 0);
				db2.update("LIST_TABLE", values, where, params);
				selectData.checked = 0;
			}
			db2.close();
		}

		private void changeDeleteData(ListData selectData) {
			SQLiteOpenHelper helper = new ListSQLiteHelper(mycontext.getApplicationContext(), "my.db", null, 1);
			SQLiteDatabase db2 = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String where = "ID=?";
			String[] params = { String.valueOf(selectData.getId()) };
			if (selectData.getDel() == 0) {
				values.put("DEL", 1);
				db2.update("LIST_TABLE", values, where, params);
				selectData.del = 1;
			} else if (selectData.getDel() == 1) {
				values.put("DEL", 0);
				db2.update("LIST_TABLE", values, where, params);
				selectData.del = 0;
			}
			db2.close();
		}

		private void changeColor(ListData selectedData, View color) {
			if (selectedData.getChecked() == 0) {
				color.setBackgroundColor(Color.rgb(70, 80, 90));
			} else if (selectedData.getChecked() == 1) {
				color.setBackgroundColor(Color.rgb(0, 255, 0));
			} else if (selectedData.getChecked() == 2) {
				color.setBackgroundColor(Color.rgb(255, 215, 0));
			}
		}

		void changeColor2(ListData selectedData, TextView t) {
			if (selectedData.getDel() == 1) {
				t.setBackgroundColor(Color.rgb(205, 92, 92));
			} else if (selectedData.getDel() == 0) {
				t.setBackgroundColor(Color.rgb(245, 245, 245));
			}
		}
	}

}
