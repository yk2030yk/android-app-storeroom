package com.example.dayplan;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private FrameLayout planBaseLayout;
	private LayoutInflater layoutInflater;
	private Vibrator vibrator;
	public MyScrollView scrollView;
	private Button addButton;
	private ArrayList<Plan> planList = new ArrayList<Plan>();
	private final int MAX_PLAN_COUNT = 24;
	private int displayHeight;
	private int h = 50;

	private String planColor1 = "b0c4de";
	private String planColor2 = "e6e6fa";
	private String planColor3 = "ff7f50";
	private boolean isEdit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main2);

		WindowManager wm = getWindowManager();
		Display disp = wm.getDefaultDisplay();
		Point size = new Point();
		disp.getSize(size);
		displayHeight = size.y;

		scrollView = (MyScrollView) findViewById(R.id.myScrollView1);
		planBaseLayout = (FrameLayout) findViewById(R.id.flayout);
		layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		LinearLayout ll = (LinearLayout) findViewById(R.id.linelayout);
		for (int i = 0; i < 24; i++) {
			View view = layoutInflater.inflate(R.layout.item, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getPx(40), getPx(h));
			lp.gravity = Gravity.CENTER;
			view.setLayoutParams(lp);

			if (i % 2 == 0) {
				view.setBackgroundColor(Color.WHITE);
			} else {
				view.setBackgroundColor(Color.GRAY);
			}

			TextView t = (TextView) view.findViewById(R.id.textView);
			t.setText(i + "時");
			t.setTextColor(Color.BLACK);
			t.setGravity(Gravity.CENTER);

			ll.addView(view);
		}

		for (int i = 0; i < 1; i++) {
			String title = "Paln" + i;
			int time = i;
			planList.add(new Plan(title, time));
		}

		init();

		addButton = (Button) findViewById(R.id.button1);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (planList.size() < MAX_PLAN_COUNT) {
					// ダイアログで詳細入力

					String title = "Plan" + planList.size();
					int time = 0;
					Plan p = new Plan(title, time);
					planList.add(p);
					init();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	void addPlanView(Plan data) {
		View view = layoutInflater.inflate(R.layout.item, null);
		view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getPx(h)));

		TextView t = (TextView) view.findViewById(R.id.textView);
		t.setText(data.title + "\nTIME " + data.time + " : 00 ~");

		if (data.time % 2 == 0) {
			view.setBackgroundColor(Color.parseColor("#" + planColor1));
		} else {
			view.setBackgroundColor(Color.parseColor("#" + planColor2));
		}

		data.view = view;
		planBaseLayout.addView(view);

		// set Plan Layout Position
		MarginLayoutParams lparams = (MarginLayoutParams) view.getLayoutParams();
		lparams.topMargin = data.position;
		view.setLayoutParams(lparams);

		// set listener
		DragViewListener listener = new DragViewListener(this, view, data);
		view.setOnTouchListener(listener);

		view.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// ダイアログで編集
				if (isEdit) {
					vibrator.vibrate(30);
				}
				return false;
			}

		});
	}

	// INIT PLAN UI
	private void init() {
		planBaseLayout.removeAllViews();
		for (int i = 0; i < planList.size(); i++) {
			addPlanView(planList.get(i));
		}
	}

	private void setPlanViewAlpha(View dragView) {
		for (int i = 0; i < planList.size(); i++) {
			Plan plan = planList.get(i);
			if (plan.view != dragView) {
				if (plan.time % 2 == 0) {
					plan.view.setBackgroundColor(Color.parseColor("#aa" + planColor1));
				} else {
					plan.view.setBackgroundColor(Color.parseColor("#aa" + planColor2));
				}
			} else {
				plan.view.setBackgroundColor(Color.parseColor("#aa" + planColor3));
			}
		}
	}

	private int getPx(int dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int pxValue = (int) (metrics.density * dp);
		return pxValue;
	}

	private class Plan {
		public String title;
		public int time;
		public int position = 0;
		public View view = null;

		public Plan(String title, int time) {
			this.title = title;
			setTime(time);
		}

		// 同じ時間にデータがあったら、後ろの空いてるところへ
		public void setTime(int time) {
			if (time < MAX_PLAN_COUNT) {
				if (checkSamePlan(time) == null) {
					this.time = time;
					this.position = this.time * getPx(h);
				} else {
					if (time != (MAX_PLAN_COUNT - 1)) {
						setTime(time + 1);
					}
				}
			}
		}

		// 同じ時間にデータがあったら、交換
		public void setTimeExchange(int time) {
			Plan samePlan = checkSamePlan(time);
			if (samePlan == null) {
				this.time = time;
			} else {
				samePlan.time = this.time;
				samePlan.position = samePlan.time * getPx(h);
				this.time = time;
			}
			this.position = this.time * getPx(h);
		}

		private Plan checkSamePlan(int newTime) {
			Plan result = null;
			int i = 0;
			while (i < planList.size()) {
				Plan p = planList.get(i);
				if (newTime == p.time && p != this) {
					result = p;
					break;
				}
				i++;
			}
			return result;
		}
	}

	public class DragViewListener implements OnTouchListener {
		private Activity activity;
		private View dragView;
		private Plan data;
		private int oldX;
		private int oldY;
		private int top;
		private int downY;

		public DragViewListener(Activity activity, View layout, Plan data) {
			this.activity = activity;
			this.dragView = layout;
			this.data = data;
			MarginLayoutParams lp = (MarginLayoutParams) layout.getLayoutParams();
			oldX = dragView.getLeft() + lp.leftMargin;
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			downY = (int) event.getRawY();
			if (!isEdit) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d("event", "down");
					setPlanViewAlpha(dragView);
					break;
				case MotionEvent.ACTION_MOVE:
					Log.d("event", "move");
					top = dragView.getTop() + (downY - oldY);
					new Thread(new Runnable() {
						@Override
						public void run() {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dragView.layout(oldX, top, oldX + dragView.getWidth(), top + dragView.getHeight());
									dragView.invalidate();
									
									int viewEnd = top + dragView.getHeight() * 2;
									int scrollEnd = scrollView.getScrollY() + displayHeight;
									if (viewEnd >= scrollEnd) { 
										scrollView.scrollTo(0, viewEnd - displayHeight + dragView.getHeight());
									}
									
									int viewStart = top;
									int scrollStart = scrollView.getScrollY();
									if (viewStart < scrollStart) {
										scrollView.scrollTo(0, viewStart);
									}
								}
							});
						}

					}).start();
					break;
				case MotionEvent.ACTION_UP:
					Log.d("event", "up");
					int time = (int) (top / getPx(h));

					if (planList.size() < MAX_PLAN_COUNT) {
						data.setTime(time);
					} else {
						data.setTimeExchange(time);
					}

					init();
					break;
				}

				oldY = downY;
			}

			return false;
		}
	}

}
