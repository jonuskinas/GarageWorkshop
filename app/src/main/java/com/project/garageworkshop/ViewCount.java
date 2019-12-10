package com.project.garageworkshop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ViewCount extends View {

    int count = 0;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public ViewCount(Context context) {
        super(context);
    }

    public ViewCount(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewCount(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(65);
        if (count > 0) {
            canvas.drawText(Integer.toString(count) + " repairs found",250, 75, paint);
        }
    }

}
