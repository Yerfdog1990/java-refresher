package org.infinispan.security;

import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public final class Security {

    private static final ThreadLocal<Subject> THREAD_SUBJECT = new ThreadLocal<>();

    private Security() {
    }

    public static <T> T doAs(final Subject subject, final PrivilegedAction<T> action) {
        Subject previous = THREAD_SUBJECT.get();
        THREAD_SUBJECT.set(subject);
        try {
            return action.run();
        } finally {
            THREAD_SUBJECT.set(previous);
        }
    }

    public static <T> T doAs(final Subject subject, final PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        Subject previous = THREAD_SUBJECT.get();
        THREAD_SUBJECT.set(subject);
        try {
            return action.run();
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        } finally {
            THREAD_SUBJECT.set(previous);
        }
    }

    public static Subject getSubject() {
        return THREAD_SUBJECT.get();
    }

    public static <T> T doPrivileged(final PrivilegedAction<T> action) {
        return AccessController.doPrivileged(action);
    }

    public static <T> T doPrivileged(final PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        return AccessController.doPrivileged(action);
    }
}
