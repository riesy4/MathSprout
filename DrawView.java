package com.example.mathsprout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View {

    private Paint paint = new Paint();
    private Path path = new Path();
    private int currentColor = Color.BLACK;
    private ArrayList<PathWithColor> paths = new ArrayList<>();

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(currentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (PathWithColor p : paths) {
            paint.setColor(p.color);
            canvas.drawPath(p.path, paint);
        }
        paint.setColor(currentColor);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                paths.add(new PathWithColor(new Path(path), currentColor));
                path.reset();
                break;
        }

        invalidate();
        return true;
    }

    public void setCurrentColor(int color) {
        currentColor = color;
    }

    public void clearCanvas() {
        paths.clear();
        path.reset();
        invalidate();
    }

    private static class PathWithColor {
        Path path;
        int color;

        PathWithColor(Path path, int color) {
            this.path = path;
            this.color = color;
        }
    }
}
