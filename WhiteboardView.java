package com.example.mathsprout;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardView extends View {

    public enum Tool { PEN, ERASER }

    private final List<PathData> paths = new ArrayList<>();
    private Path currentPath;
    private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Tool currentTool = Tool.PEN;

    private int currentColor = Color.BLACK;
    private float currentStrokeWidth = 5f;

    private StrokeListener strokeListener;
    private Bitmap backgroundBitmap;

    private float lastX, lastY;

    public WhiteboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    private void setupPaint() {
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setColor(currentColor);
        currentPaint.setStrokeWidth(currentStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background if exists
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);
        }

        // Draw all saved paths
        synchronized (paths) {
            for (PathData p : paths) {
                canvas.drawPath(p.path, p.paint);
            }
        }

        // Draw the path currently being drawn by the user
        if (currentPath != null) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Strict check: if the teacher disabled drawing, ignore all touches
        if (!isEnabled()) {
            return false;
        }

        // Configure paint based on current tool
        if (currentTool == Tool.ERASER) {
            // Eraser mimics removing marks by drawing in White
            currentPaint.setColor(Color.WHITE);
            currentPaint.setStrokeWidth(currentStrokeWidth * 2); // Erasers are usually wider
        } else {
            currentPaint.setColor(currentColor);
            currentPaint.setStrokeWidth(currentStrokeWidth);
        }

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    // Quadratic Bézier curve for smoother lines
                    float midX = (lastX + x) / 2f;
                    float midY = (lastY + y) / 2f;
                    currentPath.quadTo(lastX, lastY, midX, midY);
                    lastX = x;
                    lastY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    // Finalize the path
                    Paint p = new Paint(currentPaint);
                    Path pathCopy = new Path(currentPath);

                    synchronized (paths) {
                        paths.add(new PathData(pathCopy, p));
                    }

                    // Notify Firebase listener
                    if (strokeListener != null) {
                        strokeListener.onStroke(pathCopy, p);
                    }
                }
                currentPath = null;
                break;
        }

        invalidate(); // Redraw the view
        return true;
    }

    public void setStrokeListener(StrokeListener listener) {
        this.strokeListener = listener;
    }

    public void undo() {
        synchronized (paths) {
            if (!paths.isEmpty()) {
                paths.remove(paths.size() - 1);
                invalidate();
            }
        }
    }

    public void clearBoard() {
        synchronized (paths) {
            paths.clear();
            invalidate();
        }
    }

    /**
     * Adds a stroke received from the Educator's Firebase session
     */
    public void addRemoteStroke(Path path, Paint paint) {
        if (path == null || paint == null) return;
        synchronized (paths) {
            paths.add(new PathData(new Path(path), new Paint(paint)));
        }
        postInvalidate();
    }

    // Setters for Tool properties
    public void setColor(int color) {
        this.currentColor = color;
        this.currentTool = Tool.PEN;
    }

    public void setStrokeWidth(float width) { this.currentStrokeWidth = width; }

    public void setTool(Tool tool) { this.currentTool = tool; }

    /**
     * Data wrapper for paths and their specific paint styles
     */
    public static class PathData {
        public Path path;
        public Paint paint;
        public PathData(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }

    public interface StrokeListener {
        void onStroke(Path path, Paint paint);
    }
}