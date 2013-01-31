package com.augmentari.roadworks.sensorlogger.component;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: lexaux
 * Date: 1/31/13
 * Time: 7:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CircularBufferTest {

    @Test
    public void shoudSizeWorkCorrectly() {
        CircularBuffer buffer = new CircularBuffer(3);
        Assert.assertEquals(0, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(1, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(2, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
    }

    @Test
    public void shouldCircularWorkCorrectly() {
        CircularBuffer buffer = new CircularBuffer(3);

        buffer.append(0, 0, 0);
        buffer.append(1, 0, 0);
        buffer.append(2, 0, 0);

        Assert.assertEquals(2, buffer.getA(0), 0.00001);
        Assert.assertEquals(1, buffer.getA(1), 0.00001);
        Assert.assertEquals(0, buffer.getA(2), 0.00001);

        for (int i = 0; i < 10; i++) {
            buffer.append(3 + i, 0, 0);

            Assert.assertEquals(3 + i, buffer.getA(0), 0.00001);
            Assert.assertEquals(3 + i - 1, buffer.getA(1), 0.00001);
            Assert.assertEquals(3 + i - 2, buffer.getA(2), 0.00001);
        }

        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
        buffer.append(0, 0, 0);
        Assert.assertEquals(3, buffer.getActualSize());
    }
}
