package com.casa.doodleapp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.mlkit.vision.digitalink.Ink;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private Paint paint;
    private Path currentPath;
    private List<Path> paths;
    private List<Path> undonePaths;

    private List<PointF> currentStroke;
    private List<List<PointF>> strokes;
    private List<List<PointF>> undoneStrokes;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(12f);

        paths = new ArrayList<>();
        undonePaths = new ArrayList<>();
        strokes = new ArrayList<>();
        undoneStrokes = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        if (currentPath != null) {
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                currentStroke = new ArrayList<>();
                currentStroke.add(new PointF(x, y));
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                }
                if (currentStroke != null) {
                    currentStroke.add(new PointF(x, y));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    paths.add(currentPath);
                    currentPath = null;
                }
                if (currentStroke != null) {
                    strokes.add(currentStroke);
                    currentStroke = null;
                }
                // clear redo stacks
                undonePaths.clear();
                undoneStrokes.clear();
                break;
        }
        invalidate();
        return true;
    }

    public void clear() {
        paths.clear();
        undonePaths.clear();
        strokes.clear();
        undoneStrokes.clear();
        invalidate();
    }

    public void undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
        }
        if (strokes.size() > 0) {
            undoneStrokes.add(strokes.remove(strokes.size() - 1));
        }
        invalidate();
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
        }
        if (undoneStrokes.size() > 0) {
            strokes.add(undoneStrokes.remove(undoneStrokes.size() - 1));
        }
        invalidate();
    }

    public Ink getInk() {
        Ink.Builder inkBuilder = Ink.builder();
        for (List<PointF> stroke : strokes) {
            Ink.Stroke.Builder strokeBuilder = Ink.Stroke.builder();
            for (PointF p : stroke) {
                strokeBuilder.addPoint(Ink.Point.create(p.x, p.y, System.currentTimeMillis()));
            }
            inkBuilder.addStroke(strokeBuilder.build());
        }
        return inkBuilder.build();
    }

    public void setPenColor(int color) {
        paint.setColor(color);
    }
}