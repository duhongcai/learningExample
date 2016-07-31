package com.yile.learning.cassandra.locking;

public class LockException extends Exception {
    public LockException() {
        super();
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable throwable) {
        super(throwable);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

}
