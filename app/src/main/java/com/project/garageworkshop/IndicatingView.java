package com.project.garageworkshop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class IndicatingView extends View {
    public static final int NOTEXECUTED = 0;
    public static final int SUCCESS =1;
    public static final int FAILED = 2;
    public static final int INPROGRESS = 3;

    int state = NOTEXECUTED;

    public IndicatingView (Context context) {
        super(context);
    }

    public IndicatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint;
        switch(state) {
            case SUCCESS:
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.FILL);
                Rect r = new Rect(10,10,200,100);
                paint.setStrokeWidth(5f);
                canvas.drawRect(r, paint);


                //canvas.drawLine(0, 0, width/2, height, paint);
                //canvas.drawLine(width/2, height, width, height/2, paint);
                break;
            case FAILED:
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(5f);
                canvas.drawLine(0, 0, width, height, paint);
                canvas.drawLine(0, height, width, 0, paint);
                break;
            case INPROGRESS:
                paint =new Paint();
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(5f);
                canvas.drawLine(0, height-2, width/2, 0, paint);
                canvas.drawLine(0, height-2, width, height-2, paint );
                canvas.drawLine(width/2, 0, width, height-2, paint);
                break;
            default:
                // kol kas nieko
                break;
        }
    }
}
