package com.kbindiedev.verse.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class CallbackInputStream extends InputStream {

    private InputStream in;

    public enum Event {
        PRE_SKIP, PRE_CLOSE, PRE_RESET, PRE_MARK, PRE_QUERY_MARK,
        POST_SKIP, POST_CLOSE, POST_RESET, POST_MARK, POST_QUERY_MARK
    }
    private HashMap<Event, Runnable> map;


    public CallbackInputStream(InputStream in) {
        this.in = in;
        map = new HashMap<>();
    }

    public void setCallback(Event event, Runnable callback) { map.put(event, callback); }

    //not tracked
    @Override public int read() throws IOException { return in.read(); }
    @Override public int read(byte b[]) throws IOException { return in.read(b); }
    @Override public int read(byte b[], int off, int len) throws IOException { return in.read(b, off, len); }
    @Override public int available() throws IOException { return in.available(); }

    //tracked below

    @Override
    public long skip(long n) throws IOException {
        try {
            event(Event.PRE_SKIP);
            return in.skip(n);
        } finally {
            event(Event.POST_SKIP);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            event(Event.PRE_CLOSE);
            in.close();
        } finally {
            event(Event.POST_CLOSE);
        }
    }

    @Override
    public void reset() throws IOException {
        try {
            event(Event.PRE_SKIP);
            in.reset();
        } finally {
            event(Event.POST_SKIP);
        }
    }

    @Override
    public void mark(int readlimit) {
        try {
            event(Event.PRE_MARK);
            in.mark(readlimit);
        } finally {
            event(Event.POST_MARK);
        }
    }

    @Override
    public boolean markSupported() {
        try {
            event(Event.PRE_QUERY_MARK);
            return in.markSupported();
        } finally {
            event(Event.POST_QUERY_MARK);
        }
    }

    /** dispatch an event / run a callback if it exists */
    private void event(Event event) {
        Runnable callback = map.get(event);
        if (callback != null) callback.run();
    }
}
