package com.augmentari.roadworks.sensorlogger.component;

import java.util.Arrays;

/**
 * //TODO replace me with something normal
 */
public class CircularBuffer {

    int endIndex = -1; // will be incremented after the startup


    private int size;
    public
    boolean isOverflown = false;
    private float[] dataArray;

    CircularBuffer(int size) {
        dataArray = new float[size * 3];
        Arrays.fill(dataArray, 0);

        this.size = size;
    }

    public synchronized void append(float a, float b, float c) {
        if (endIndex == size - 1) {
            isOverflown = true;
        }
        endIndex = ++endIndex % size;
        dataArray[endIndex] = a;
        dataArray[endIndex + size] = b;
        dataArray[endIndex + size * 2] = c;
    }

    public int getActualSize() {
        if (isOverflown) {
            return size;
        }
        return endIndex + 1;
    }

    public synchronized float getA(int i) {
        int index = endIndex - i;

        while (index < 0) {
            index += size;
        }

        return dataArray[index];
    }

    public float getB(int i) {
        int index = endIndex - i;

        while (index < 0) {
            index += size;
        }

        return dataArray[index + size];
    }

    public float getC(int i) {
        int index = endIndex - i;

        while (index < 0) {
            index += size;
        }

        return dataArray[index + size * 2];
    }

    public int getMaxSize() {
        return size;
    }
}