package com.augmentari.roadworks.sensorlogger.model;

import java.util.Date;

/**
 * Instances of RecordingSession represent a single recording, from starting to log the track to the finishing. One
 * RecordingSession corresponds to a single data file in the application's data folder.
 * <p/>
 * The session goes through a lifecycle:
 * CREATED - LOGGING - LOGGED - PROCESSING - PROCESSED - UPLOADING - UPLOADED
 * These phases may be spread across time and dependent on some external conditions (for example, PROCESSING phase
 * may be delayed till the terminal is attached to AC power to save battery.
 */
public class RecordingSession {
    public enum State {
        CREATED,
        LOGGING,
        LOGGED,
        PROCESSING,
        PROCESSED,
        UPLOADING,
        UPLOADED
    }

    private Date startTime;
    private Date endTime;
    private long eventsLogged;
    private String dataFileFullPath;
    private State state;
}
