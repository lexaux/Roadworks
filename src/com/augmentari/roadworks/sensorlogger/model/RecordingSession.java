package com.augmentari.roadworks.sensorlogger.model;

import java.util.Date;

/**
 * Instances of RecordingSession represent a single recording, from starting to log the track to the finishing. One
 * RecordingSession corresponds to a single data file in the application's data folder.
 * <p/>
 * The session goes through a lifecycle:
 * LOGGING - LOGGED - PROCESSING - PROCESSED - UPLOADING - UPLOADED
 * These phases may be spread across time and dependent on some external conditions (for example, PROCESSING phase
 * may be delayed till the terminal is attached to AC power to save battery.
 */
public class RecordingSession {
    public enum State {
        LOGGING,
        LOGGED,
        PROCESSING,
        PROCESSED,
        UPLOADING,
        UPLOADED,
        FAILED
    }
    private long id;

    private Date startTime;
    private Date endTime;
    private long eventsLogged;
    private String dataFileFullPath;
    private State state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getEventsLogged() {
        return eventsLogged;
    }

    public void setEventsLogged(long eventsLogged) {
        this.eventsLogged = eventsLogged;
    }

    public String getDataFileFullPath() {
        return dataFileFullPath;
    }

    public void setDataFileFullPath(String dataFileFullPath) {
        this.dataFileFullPath = dataFileFullPath;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
