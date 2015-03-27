package com.example.movielist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLayout2 extends LinearLayout {
	public GestureDetector ges;

	public MyLayout2(Context context) {
		super(context);
	}

	public MyLayout2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLayout2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onInterceptTouchEvent(MotionEvent e) {
		Log.d("ok", "mylayout onItercept");
		return false;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d("ok", "mylayout dispatch");
		if (ges != null) {
			ges.onTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void setGes(GestureDetector g) {
		this.ges = g;
	}

}