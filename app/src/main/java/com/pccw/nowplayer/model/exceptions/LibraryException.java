package com.pccw.nowplayer.model.exceptions;

/**
 * Created by kriz on 4/7/2016.
 */
public class LibraryException extends Exception {

    protected int errorCode;

    public LibraryException(int errorCode) {
        super("Server side error: " + errorCode);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
