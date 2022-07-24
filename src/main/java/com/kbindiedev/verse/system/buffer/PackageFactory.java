package com.kbindiedev.verse.system.buffer;

import com.kbindiedev.verse.async.ThreadPool;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.ISerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Packages and unpackages data from streams. */
public class PackageFactory<T extends ISerializable> {

    private Class<T> clazz;
    private InputStream in;
    private OutputStream out;

    private final ConcurrentLinkedQueue<T> receivedPackages;

    public PackageFactory(Class<T> clazz, InputStream in, OutputStream out) {
        this.clazz = clazz;
        this.in = in;
        this.out = out;

        receivedPackages = new ConcurrentLinkedQueue<>();

        ThreadPool.INSTANCE.submitContinous(this::tick);
    }

    public void write(T item) throws IOException {
        item.serialize(out);
    }

    public T read(boolean wait) {
        if (!wait) return receivedPackages.poll();

        try {
            synchronized (receivedPackages) {
                while (receivedPackages.size() == 0) receivedPackages.wait();
                return receivedPackages.poll();
            }
        } catch (InterruptedException e) {
            Assertions.warn("unexpected thread interrupt:");
            e.printStackTrace();
            return null;
        }
    }

    public T read() { return read(true); }

    /** Create a package from "in". */
    private void tick() {   // TODO: getNumBytesForPackage() or something, or some overarching async packaging system.
        try {
            T t = clazz.newInstance();
            t.deserialize(in);
            synchronized (receivedPackages) {
                receivedPackages.add(t);
                receivedPackages.notify();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            Assertions.warn("unable to instantiate class '%s', (no accessible empty constructor): %s", clazz.getCanonicalName(), e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}