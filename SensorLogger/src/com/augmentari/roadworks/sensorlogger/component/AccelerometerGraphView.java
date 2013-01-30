package com.augmentari.roadworks.sensorlogger.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.util.Random;

/**
 * View showing line chart/graph view of the accelerometer readings.
 */
public class AccelerometerGraphView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint p;

    public AccelerometerGraphView(Context context) {
        super(context);
    }

    public AccelerometerGraphView(Context context, AttributeSet attrs) {
        this(context);
        getHolder().addCallback(this);
        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.FILL);
    }

    int width = 0;
    int height = 0;
    private final int radius = 30;
    private final int delta = 10;
    int x = 0;
    int y = 0;
    public static final long REFRESH_RATE_MSEC = 20;
    private final Random random = new Random(System.currentTimeMillis());
    DrawingThread thread;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        boolean fits = false;
        while (!fits) {
            int multiplier = random.nextBoolean() ? 1 : -1;
            if (random.nextBoolean()) {
                x += multiplier * delta;
            } else {
                y += multiplier * delta;
            }
            fits = (x > radius / 2 && x < width - radius / 2) && (y > radius / 2 && y < height - radius / 2);
        }
        canvas.drawCircle(x, y, radius, p);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawingThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        x = width / 2 - radius / 2;
        y = height / 2 - radius / 2;
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
    }

    private static int threadNum = 0;


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
}