package com.augmentari.roadworks.sensorlogger.net.ssl;

/**
 * Marker exception for everything related to the netowkring communication.
 */
public class NetworkingException extends Exception {
    public NetworkingException() {
    }

    public NetworkingException(String detailMessage) {
        super(detailMessage);
    }

    public NetworkingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NetworkingException(Throwable throwable) {
        super(throwable);
    }
}
