package mam.gprg.ourrocks.view;

import mam.gprg.ourrocks.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Bitmap bitmap;
	private Path path;

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		setBackgroundResource(R.drawable.circle);
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.circle);
		path = new Path();
		path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2,
				Path.Direction.CW);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.clipPath(path);
	}

}
