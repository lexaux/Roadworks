package com.augmentari.roadworks.sensorlogger.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.augmentari.roadworks.sensorlogger.service.SensorLoggerService;
import com.augmentari.roadworks.sensorlogger.util.Log;

/**
 * View showing line chart/graph view of the accelerometer readings.
 */
public class AccelerometerGraphView extends SurfaceView implements SurfaceHolder.Callback, SensorLoggerService.AccelChangedListener {

    public static final float GRAVITY_FT_SEC = 9.8f;
    // max possible size of the circularbuffer = sizeof(float) * 3 * max(deviceX, deviceY). So, should not be more than
    // 20Kb
    private static CircularBuffer buffer = null;
    final Object changedDataLock = new Object();
    DrawingThread thread;
    private Paint paint1, paint2, paint3, whitePaint;
    private int width;
    private int height;
    private float offset;
    private float ftSecToPx;

    public AccelerometerGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(Color.RED);
        paint1.setStyle(Paint.Style.STROKE);

        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.STROKE);

        paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(Color.YELLOW);
        paint3.setStyle(Paint.Style.STROKE);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.STROKE);
    }

    protected void doDrawOnSeparateThread(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);

        if (buffer == null) {
            // nothing to draw, quiting.
            return;
        }

        float topG = offset + ftSecToPx * GRAVITY_FT_SEC;
        float bottomG = offset - ftSecToPx * GRAVITY_FT_SEC;

        canvas.drawLine(0, topG, width, topG, whitePaint);
        canvas.drawLine(0, offset, width, offset, whitePaint);
        canvas.drawLine(0, bottomG, width, bottomG, whitePaint);

        float lastX1 = width;
        float lastX2 = width;
        float lastX3 = width;

        float lastY1 = offset + buffer.getA(0) * ftSecToPx;
        float lastY2 = offset + buffer.getB(0) * ftSecToPx;
        float lastY3 = offset + buffer.getC(0) * ftSecToPx;

        for (int i = 1; i < buffer.getActualSize(); i++) {
            float toX = width - i;
            float toY = offset + buffer.getA(i) * ftSecToPx;
            canvas.drawLine(lastX1, lastY1, toX, toY, paint1);
            lastX1 = toX;
            lastY1 = toY;

            toY = offset + buffer.getB(i) * ftSecToPx;
            canvas.drawLine(lastX2, lastY2, toX, toY, paint2);
            lastX2 = toX;
            lastY2 = toY;

            toY = offset + buffer.getC(i) * ftSecToPx;
            canvas.drawLine(lastX3, lastY3, toX, toY, paint3);

            lastX3 = toX;
            lastY3 = toY;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawingThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (buffer == null) {
            WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point p = new Point();
            manager.getDefaultDisplay().getSize(p);
            buffer = new CircularBuffer(Math.max(p.x, p.y));
        }
        this.height = height;
        this.width = width;

        offset = height / 2;
        ftSecToPx = height / (2.5f * GRAVITY_FT_SEC);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                synchronized (changedDataLock) {
                    changedDataLock.notifyAll();
                }
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.i("Interrupted stopping drawing thread of the surface");
            }
        }
    }

    @Override
    public void onAccelChanged(float a, float b, float c) {
        synchronized (changedDataLock) {
            changedDataLock.notifyAll();
        }
        if (buffer == null) return;
        buffer.append(a, b, c);
    }

    public CircularBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(CircularBuffer buffer) {
        this.buffer = buffer;
    }

    class DrawingThread extends Thread {
        private final SurfaceHolder holder;
        private final AccelerometerGraphView graphView;
        private boolean running;

        public DrawingThread(SurfaceHolder holder, AccelerometerGraphView context) {
            super("Accel drawingn thread");
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
                    synchronized (changedDataLock) {
                        changedDataLock.wait();
                    }
                    c = holder.lockCanvas(null);
                    if (c != null) {
                        synchronized (holder) {
                            graphView.doDrawOnSeparateThread(c);
                            // drawn, wait for new arrivals.
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}