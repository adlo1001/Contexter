package se.sensiblethings.app.chitchato.extras;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by user on 11/1/2016.
 */

public class RoundRelativeLayout extends RelativeLayout {

    private final float radius;

    public RoundRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        int [] _int_ = {10,10};
        TypedArray attrArray = context.obtainStyledAttributes(attrs, _int_);
        radius = 0.0f;//attrArray.getDimension(R.styleable.RoundRelativeLayout_radius, 0);
    }

    private boolean isPathValid;
    private final Path path = new Path();

    private Path getRoundRectPath() {
        if (isPathValid) {
            return path;
        }

        path.reset();

        int width = getWidth();
        int height = getHeight();
        RectF bounds = new RectF(0, 0, width, height);

        path.addRoundRect(bounds, radius, radius, Path.Direction.CCW);
        isPathValid = true;
        return path;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(getRoundRectPath());
        super.dispatchDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(getRoundRectPath());
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int oldWidth = getMeasuredWidth();
        int oldHeight = getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth = getMeasuredWidth();
        int newHeight = getMeasuredHeight();
        if (newWidth != oldWidth || newHeight != oldHeight) {
            isPathValid = false;
        }
    }
}