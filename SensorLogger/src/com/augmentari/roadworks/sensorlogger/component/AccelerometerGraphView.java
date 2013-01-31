package com.augmentari.roadworks.sensorlogger.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.augmentari.roadworks.sensorlogger.service.SensorLoggerService;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.util.Random;

/**
 * View showing line chart/graph view of the accelerometer readings.
 */
public class AccelerometerGraphView extends SurfaceView implements SurfaceHolder.Callback, SensorLoggerService.AccelChangedListener {

    public static final long REFRESH_RATE_MSEC = 200;
    public static final float GRAVITY_FT_SEC = 9.8f;
    DrawingThread thread;

    private CircularBuffer buffer = null;
    private Paint paint1, paint2, paint3, whitePaint;
    private int width;
    private int height;

    public AccelerometerGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setStyle(Paint.Style.STROKE);

        paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.STROKE);

        paint3 = new Paint();
        paint3.setColor(Color.YELLOW);
        paint3.setStyle(Paint.Style.STROKE);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);

        float offset = height / 2;
        float ftSecToPx = height / (2.5f * GRAVITY_FT_SEC);

        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();

        for (int i = 0; i < buffer.getActualSize(); i++) {
            float toX = width - i;
            float toY = offset + buffer.getA(i) * ftSecToPx;
            if (i == 0) {
                path1.moveTo(toX, toY);
            } else {
                path1.lineTo(toX, toY);
            }

            toX = width - i;
            toY = offset + buffer.getB(i) * ftSecToPx;
            if (i == 0) {
                path2.moveTo(toX, toY);
            } else {
                path2.lineTo(toX, toY);
            }


            toX = width - i;
            toY = offset + buffer.getC(i) * ftSecToPx;
            if (i == 0) {
                path3.moveTo(toX, toY);
            } else {
                path3.lineTo(toX, toY);
            }
        }
        float center = offset;
        float topG = offset + ftSecToPx * GRAVITY_FT_SEC;
        float bottomG = offset - ftSecToPx * GRAVITY_FT_SEC;

        canvas.drawLine(0, topG, width, topG, whitePaint);
        canvas.drawLine(0, center, width, center, whitePaint);
        canvas.drawLine(0, bottomG, width, bottomG, whitePaint);

        canvas.drawPath(path1, paint1);
        canvas.drawPath(path2, paint2);
        canvas.drawPath(path3, paint3);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawingThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (buffer == null || buffer.getMaxSize() != width) {
            buffer = new CircularBuffer(width);
        }
        this.height = height;
        this.width = width;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.i("Interrupted stopping drawing thread of the surface");
            }
        }
        buffer = null;
    }

    private static int threadNum = 0;

    @Override
    public void onAccelChanged(float a, float b, float c) {
        if (buffer == null) return;
        buffer.append(a, b, c);
    }

    class DrawingThread extends Thread {
        private final SurfaceHolder holder;
        private final AccelerometerGraphView graphView;
        private boolean running;

        public DrawingThread(SurfaceHolder holder, AccelerometerGraphView context) {
            super("DRAWING_THREAD " + threadNum++);
            this.holder = holder;
            this.graphView = context;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = holder.lockCanvas(null);
                    if (c != null) {
                        synchronized (holder) {
                            graphView.onDraw(c);
                        }
                    }
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                    try {
                        sleep(REFRESH_RATE_MSEC);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public CircularBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(CircularBuffer buffer) {
        this.buffer = buffer;
    }
}