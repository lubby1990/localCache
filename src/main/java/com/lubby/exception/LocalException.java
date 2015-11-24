package com.lubby.exception;

/**
 * Created by liubin on 2015-11-23.
 */
public class LocalException extends Exception {
    private static final long serialVersionUID = 1280506189356887418L;

    public LocalException() {
    }

    public LocalException(String message) {
        super(message);
    }
}
