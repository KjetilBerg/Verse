package com.kbindiedev.verse.async;

import java.util.concurrent.locks.ReentrantLock;

/** An intermediately class between Verse and Java's ReentrantLock. */
public class Lock {

    private ReentrantLock lock;

    public Lock() {
        lock = new ReentrantLock();
    }

    public void lock() { lock.lock(); }
    public void lockInterruptibly() throws InterruptedException { lock.lockInterruptibly(); }

    public boolean tryLock() { return lock.tryLock(); }

    public void unlock() { lock.unlock(); }

    public boolean isLocked() { return lock.isLocked(); }

    public boolean isHeldByCurrentThread() { return lock.isHeldByCurrentThread(); }

}