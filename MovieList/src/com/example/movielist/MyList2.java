package com.example.movielist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class MyList2 extends ListView {
	public GestureDetector g;

	public MyList2(Context context) {
		super(context);
	}

	public MyList2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyList2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onInterceptTouchEvent(MotionEvent e) {
		Log.d("ok", "mylist onItercept");
		return super.onInterceptTouchEvent(e);
	}

	public boolean onTouch(View v, MotionEvent event) {
		Log.d("ok", "mylist onTouch");
		return false;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d("ok", "mylist dispatch");
		g.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public void setGes(GestureDetector g) {
		this.g = g;
	}

}