package dev.redvx.loader.util;

public class NoStackTrace extends RuntimeException {
    public NoStackTrace(String msg) {
        super(msg);
        setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}