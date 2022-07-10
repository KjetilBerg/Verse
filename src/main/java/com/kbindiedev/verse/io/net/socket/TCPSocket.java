package com.kbindiedev.verse.io.net.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/** A connection over TCP. */
public class TCPSocket implements IPayloadOffice {

    private String location;
    private int port;

    private Socket socket;

    public TCPSocket(String location, int port) {
        this.location = location;
        this.port = port;
        socket = null;
    }

    public boolean isConnected() { return socket != null; }

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
    }

    /**
     * Force a disconnect.
     * @return true if connection was closed successfully, or false if an IOException occurred. if we are not connected, this method returns true.
     */
    public boolean terminate() { try { disconnect(); return true; } catch (IOException e) { return false; } }

    @Override
    public void send(byte[] data) throws IOException {
        if (!isConnected()) throw new IOException("not connected");
        socket.getOutputStream().write(data);
    }

    @Override
    public byte[] retrieve(boolean wait) throws IOException {
        return receiveMessage(); // TODO wait is not considered: always blocking
    }

    @Override
    public void close() throws IOException { disconnect(); }

    public byte[] receive(int numBytes) throws IOException {
        if (!isConnected()) throw new IOException("not connected");
        byte[] data = new byte[numBytes];
        int numReceived = socket.getInputStream().read(data);
        if (numReceived < numBytes) disconnect(); // we have disconnected
        return data;
    }

    public void sendMessage(byte[] data) throws IOException {
        byte[] header = ByteBuffer.allocate(4).putInt(data.length).array();
        send(header);
        send(data);
    }

    public byte[] receiveMessage() throws IOException {
        byte[] header = receive(4);
        int size = ByteBuffer.wrap(header).getInt();
        return receive(size);
    }

}