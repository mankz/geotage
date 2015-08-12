package info.androidhive.imageslider.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TouchPager extends ViewPager {

	public TouchPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			return super.onInterceptTouchEvent(arg0);
		} catch (IllegalArgumentException e) {
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return false;
	}
}
