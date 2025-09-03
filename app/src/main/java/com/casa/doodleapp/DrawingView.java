package com.casa.doodleapp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.mlkit.vision.digitalink.Ink;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint;
    private Path path;

    private List<Ink.Point> currentStroke;
    private List<List<Ink.Point>> allStrokes;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(8f);

        path = new Path();
        allStrokes = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long t = System.currentTimeMillis();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                currentStroke = new ArrayList<>();
                currentStroke.add(Ink.Point.create(x, y, t));
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                currentStroke.add(Ink.Point.create(x, y, t));
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                currentStroke.add(Ink.Point.create(x, y, t));
                allStrokes.add(currentStroke);
                break;
        }
        invalidate();
        return true;
    }

    public Ink getInk() {
        Ink.Builder inkBuilder = Ink.builder();
        for (List<Ink.Point> stroke : allStrokes) {
            Ink.Stroke.Builder strokeBuilder = Ink.Stroke.builder();
            for (Ink.Point p : stroke) {
                strokeBuilder.addPoint(p);
            }
            inkBuilder.addStroke(strokeBuilder.build());
        }
        return inkBuilder.build();
    }

    public void clear() {
        path.reset();
        allStrokes.clear();
        invalidate();
    }
}