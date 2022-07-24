package com.kbindiedev.verse.io.net.socket;

import com.kbindiedev.verse.io.comms.ICommunicationChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/** A connection over TCP. */
public class TCPSocket implements ICommunicationChannel {

    private String location;
    private int port;

    private Socket socket;

    private InputStream readingStream;
    private OutputStream writingStream;

    public TCPSocket(String location, int port) {
        this.location = location;
        this.port = port;
        socket = null;
        readingStream = null;
        writingStream = null;
    }

    public boolean isConnected() { return socket != null && socket.isConnected(); }

    /** Connect to the previously established location and port. */
    public void connect() throws IOException { connect(0); }
    /** Connect to the previously established location and port. */
    public void connect(int timeout) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(location, port), timeout);
    }

    /** Disconnect if we are connected, otherwise do nothing. */
    public void disconnect() throws IOException {
        if (isConnected()) socket.close();
        socket = null;
        readingStream = null;
        writingStream = null;
    }

    /**
     * Force a disconnect.
     * @return true if connection was closed successfully, or false if an IOException occurred. if we are not connected, this method returns true.
     */
    public boolean terminate() { try { disconnect(); return true; } catch (IOException e) { return false; } }

    @Override
    public InputStream getReadingStream() throws IOException {
        if (!isConnected()) throw new IOException("socket not connected");
        if (readingStream == null) synchronized(socket) { readingStream = socket.getInputStream(); }
        return readingStream;
    }

    @Override
    public OutputStream getWritingStream() throws IOException {
        if (!isConnected()) throw new IOException("socket not connected");
        if (writingStream == null) synchronized(socket) { writingStream = socket.getOutputStream(); }
        return writingStream;
    }

    @Override
    public void close() throws IOException { disconnect(); }

}