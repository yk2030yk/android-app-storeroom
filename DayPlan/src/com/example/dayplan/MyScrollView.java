package com.example.dayplan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	
	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	//�@�h���b�O���ɃC�x���g���󂯎��Ȃ��悤�� false��Ԃ�
	@Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
	        return false;
        }
}
