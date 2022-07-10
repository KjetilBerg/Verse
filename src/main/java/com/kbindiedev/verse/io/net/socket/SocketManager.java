package com.kbindiedev.verse.io.net.socket;

// TODO: multiple connections ?

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/** Manages a single Socket connection. */
public class SocketManager implements IPayloadOffice {

    private TCPSocket socket;

    private List<Byte[]> toSend;
    private List<Byte[]> received;

    private Thread thread;

    /** Create a new Socket connection (connect immediately). timeout is in milliseconds, or 0 for "indefinite" */
    public SocketManager(String location, int port, int timeout) {
        socket = new TCPSocket(location, port);
        toSend = new LinkedList<>();
        received = new LinkedList<>();

        thread = new Thread(() -> {
            try {
                socket.connect(timeout);
                while (toSend.size() > 0) sendCachedMessage();
                if (shouldDisconnect) socket.disconnect();
                while (socket.isConnected()) stackMessage();
            } catch (IOException e) {
                e.printStackTrace(); // TODO: if disconnected
            }
        });
        thread.start();
    }

    private void stackMessage() throws IOException {
        System.out.println("STACKING");
        byte[] message = socket.receiveMessage();
        received.add(pack(message));
    }

    private void sendCachedMessage() throws IOException {
        Byte[] message = toSend.remove(0);
        socket.sendMessage(unpack(message));
    }

    public void send(byte[] data) throws IOException {
        if (!socket.isConnected()) toSend.add(pack(data));
        else socket.sendMessage(data); // TODO: abstract this into socket ?
    }

    @Override
    public byte[] retrieve(boolean wait) throws IOException {
        while (!hasMessage()) {
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); } // TODO: very bad
        }
        return read();
    }

    @Override
    public void close() throws IOException {
        disconnect();
    }

    public boolean hasMessage() { return received.size() > 0; }
    public byte[] read() {
        Byte[] message = received.remove(0);
        return unpack(message);
    }

    boolean shouldDisconnect = false; // temp workaround
    public void disconnect() throws IOException { shouldDisconnect = true; socket.disconnect(); } // TODO: if called disconnect before connected

    private Byte[] pack(byte[] data) {
        Byte[] ret = new Byte[data.length];
        for (int i = 0; i < data.length; ++i) ret[i] = data[i];
        return ret;
    }

    private byte[] unpack(Byte[] data) {
        byte[] ret = new byte[data.length];
        for (int i = 0; i < data.length; ++i) ret[i] = data[i];
        return ret;
    }

}