package com.pccw.nowplayer.model.exceptions;

/**
 * Created by kriz on 4/7/2016.
 */
public class CancelledException extends Exception {
    public CancelledException() {
    }

    public CancelledException(String detailMessage) {
        super(detailMessage);
    }

    public CancelledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CancelledException(Throwable throwable) {
        super(throwable);
    }
}
