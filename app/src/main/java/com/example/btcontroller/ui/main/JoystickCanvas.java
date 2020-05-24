package com.example.btcontroller.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.btcontroller.R;

/**
 * TODO: document your custom view class.
 */
public class JoystickCanvas extends View {
    private Point center, point;
    Paint primaryPaint;
    Paint secondaryPaint;

    /**
     * Event for updating canvas saved width and height
     */
    private OnLayoutChangeListener layoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            center.x = (right - left)/2;
            center.y = (bottom - top)/2;
        }
    };

    public JoystickCanvas(Context context) {
        super(context);
        this.center = new Point();
        this.point = new Point();

        // Configure primary color paint
        primaryPaint = new Paint();
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(getResources().getColor(R.color.primaryColor, null));

        // Configure secondary color paint
        secondaryPaint = new Paint();
        secondaryPaint.setAntiAlias(true);
        secondaryPaint.setStyle(Paint.Style.FILL);
        secondaryPaint.setColor(getResources().getColor(R.color.secondaryColor, null));

        this.addOnLayoutChangeListener(layoutChangeListener);
    }

    public JoystickCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.center = new Point();
        this.point = new Point();

        // Configure primary color paint
        primaryPaint = new Paint();
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(getResources().getColor(R.color.primaryColor, null));

        // Configure secondary color paint
        secondaryPaint = new Paint();
        secondaryPaint.setAntiAlias(true);
        secondaryPaint.setStyle(Paint.Style.FILL);
        secondaryPaint.setColor(getResources().getColor(R.color.secondaryColor, null));

        this.addOnLayoutChangeListener(layoutChangeListener);
    }


    /**
     * Sets x and y coordinates relative to the center of the screen.
     * @param x horizontal coordinate
     * @param y veritical coordinate
     */
    public void setCoordinates(int x, int y){
        point.x = x;
        point.y = y;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(center.x + point.x, center.y + point.y, 20, primaryPaint);
    }


}
