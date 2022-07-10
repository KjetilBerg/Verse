package com.kbindiedev.verse.io.net.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/** A socket that can receive responses to sent packages. */
public class ResponseSocket {

    private IPayloadOffice office;
    private long currentId;

    private HashMap<Long, Package> packages;
    private List<Package> unmarkedPackages;

    public ResponseSocket(IPayloadOffice office) {
        this.office = office;
        currentId = 1;
        packages = new HashMap<>();
        unmarkedPackages = new LinkedList<>();

        new Thread(() -> {
            try {
                while (true) {
                    byte[] payload = office.retrieve(true);
                    System.out.println("Payload: " + Arrays.toString(payload));
                    byte[] header = new byte[] { payload[0], payload[1], payload[2], payload[3], payload[4], payload[5], payload[6], payload[7] };
                    byte[] data = new byte[payload.length - header.length];
                    for (int i = 0; i < data.length; ++i) data[i] = payload[i+8];
                    long id = ByteBuffer.wrap(header).getLong();
                    if (id == 0) unmarkedPackages.add(new Package(data));
                    else packages.put(id, new Package(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public long send(byte[] data) throws IOException {
        byte[] header = ByteBuffer.allocate(8).putLong(currentId).array();
        byte[] toSend = new byte[header.length + data.length]; // TODO: continous stream instead of byte array
        System.arraycopy(header, 0, toSend, 0, 8);
        System.arraycopy(data, 0, toSend, 8, data.length);
        office.send(toSend);
        return currentId++;
    }

    // TODO TEMP
    public byte[] retrieve(long id) {
        if (packages.containsKey(id)) return packages.remove(id).data;
        return null;
    }

    // TODO VERY TEMP and bad:
    public byte[] retrieveBlocking(long id) {
        while (true) {
            byte[] p = retrieve(id);
            if (p != null) return p;
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public boolean hasUnmarkedPackage() { return unmarkedPackages.size() > 0; }
    public byte[] getUnmarkedPackage() {
        if (!hasUnmarkedPackage()) return null;
        return unmarkedPackages.remove(0).data;
    }

    public void close() throws IOException { office.close(); }

    private static class Package {
        private byte[] data;
        public Package(byte[] data) { this.data = data; }
    }

}
