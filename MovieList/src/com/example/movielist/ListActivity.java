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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {
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
	private int tid;
	public GestureDetector gesturedetector;
	private int displayWidth;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list);
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		displayWidth = disp.getWidth();

		Intent intent = getIntent();
		tid = intent.getExtras().getInt("id");
		SQLiteOpenHelper helper = new ListSQLiteHelper(getApplicationContext(), "my.db", null, 1);
		db = helper.getWritableDatabase();
		listView = (MyList2) findViewById(R.id.listView1);
		adapter = new CustomAdapter(this, R.layout.row, data);
		listView.setAdapter(adapter);
		setData();
		modeLabel = (TextView) findViewById(R.id.mode);
		modeLabel.setText("nomal mode");
		mycontext = this;
		textForm = (EditText) findViewById(R.id.edittext_top);
		toast_del = Toast.makeText(this, "�폜���[�h�ɂȂ�܂����B", Toast.LENGTH_SHORT);
		toast_del.setGravity(Gravity.CENTER, 0, 0);
		toast_normal = Toast.makeText(this, "�ʏ탂�[�h�ɂȂ�܂����B", Toast.LENGTH_SHORT);
		toast_normal.setGravity(Gravity.CENTER, 0, 0);

		btn_open = (Button) findViewById(R.id.button_add);
		btn_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ok", "l onClick");
				// �L�[�{�[�h�̓W�J�����邾��
				if (!del_mode) {
					textForm.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		btn_del = (Button) findViewById(R.id.button_sdel);
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
				// �f���[�g���[�h�̎��폜
				// ���[�h�ύX�̒������̌�A�N���b�N���肪�����Ă��܂�����
				// �P��ڂ̔���͖�������悤�ɂ���
				if (del_mode) {
					createConfirmDel();
				}
			}
		});

		btn_add = (Button) findViewById(R.id.button_add2);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ok", "l onClick");
				// �e�L�X�g���͂̒l���f�[�^�x�[�X�ɒǉ�
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
		// ���X�g���C�x���g���󂯎��ꍇ(���؍ς�)
		listView.setGes(gesturedetector);
		// ��ʑS�̂��C�x���g���󂯎��ꍇ(������)
		// MyLayout2 mylayout=(MyLayout2)findViewById(R.id.mylayout);
		// mylayout.setGes(gesturedetector);
	}

	// �C�x���g����
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
					finish();
					overridePendingTransition(R.animator.out_right, R.animator.in_left);
				} else if (e1.getX() >= e2.getX()) {
					Intent i = new Intent(ListActivity.this, ListActivity2.class);
					i.putExtra("id", tid);
					startActivity(i);
					overridePendingTransition(R.animator.in_right, R.animator.out_left);
				}
			}
			Log.d("ok", "l onFling");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			Log.d("ok", "l onLongPress");

		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			Log.d("ok", "l onScroll");
			return true;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			Log.d("ok", "l onShowPress");

		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			Log.d("ok", "l onSingleTapUp");
			return false;
		}

	};

	// DB�f�[�^���A�_�v�^�[�ɃZ�b�g
	void setData() {
		adapter.clear();
		String[] columns = { "ID", "NAME", "CHECKED", "DEL", "LISTID" };
		String where = "LISTID=?";
		String[] whereArgs = { String.valueOf(tid) };
		Cursor cursor = db.query("LIST_TABLE", columns, where, whereArgs, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("ID"));
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			int checked = cursor.getInt(cursor.getColumnIndex("CHECKED"));
			int del = cursor.getInt(cursor.getColumnIndex("DEL"));
			int tid = cursor.getInt(cursor.getColumnIndex("LISTID"));
			adapter.add(new ListData(id, name, checked, del, tid));
		}
		cursor.close();
		adapter.notifyDataSetChanged();
	}

	// DB�Ƀf�[�^��ǉ�
	void addData(String str) {
		ContentValues values = new ContentValues();
		values.put("NAME", str);
		values.put("LISTID", tid);
		db.insert("LIST_TABLE", null, values);
	}

	// id�̃f�[�^���폜
	void deleteData(int id) {
		String where = "ID=?";
		String[] whereArgs = { String.valueOf(id) };
		db.delete("LIST_TABLE", where, whereArgs);
	}

	// id�̃f�[�^��str�ɕύX
	void editData(int id, String str) {
		ContentValues values = new ContentValues();
		values.put("NAME", str);
		String where = "ID=?";
		String[] params = { String.valueOf(id) };
		db.update("LIST_TABLE", values, where, params);
	}

	// �폜�m�F���s���Aok�̎�db����폜
	void createConfirmDel() {
		AlertDialog.Builder alert = new AlertDialog.Builder(mycontext);
		alert.setTitle("�폜���Ă�낵���ł���?");
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

	// �`�F�b�N�������Ă���f�[�^���폜
	// �`�F�b�N�������Ă���=watched�̒l��3�̎�������B
	void delete() {
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

	// �폜�f�[�^�̑I�����I�t�ɂ���
	void initDel() {
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

	// ���A�N�e�B�r�e�B���o�Ė߂��Ă����Ƃ����s
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

	// ���X�g�̃J�X�^���A�_�v�^�[�N���X
	public class CustomAdapter extends ArrayAdapter<ListData> {
		Context mycontext;
		private LayoutInflater layoutInflater;
		int viewResourceId;
		ArrayList<ListData> dataList;
		boolean isCheck = false;

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
			final TextView mainText = (TextView) view.findViewById(R.id.item_title);
			mainText.setText(selectedData.getName());
			final View color = (View) view.findViewById(R.id.color);
			changeColor(selectedData, color);
			changeColor2(selectedData, mainText);

			// ���X�g�A�C�e���̉E�̃{�^��
			// ���C�A�E�g�̃N���b�N������g���A
			// ���C�A�E�g���̂��{�^���Ƃ��Ă���B
			LinearLayout btn_right = (LinearLayout) view.findViewById(R.id.layout_btn);
			btn_right.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// �f���[�ƃ��[�h�łȂ��Ƃ��A���̐F��ς���
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
					// �f���[�ƃ��[�h�̂Ƃ��A���X�g�A�C�e���̐F��ς���
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
						Intent intent = new Intent(ListActivity.this, DetailActivity.class);
						intent.putExtra("DATA_ID", selectedData.id);
						startActivity(intent);
						Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(30);
					}
					// true��Ԃ��Ȃ���onClick������
					return true;
				}
			});

			return view;
		}

		void changeWatchedData(ListData selectData) {
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

		void changeDeleteData(ListData selectData) {
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

		void changeColor(ListData selectedData, View color) {
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
