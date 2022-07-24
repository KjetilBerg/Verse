package com.kbindiedev.verse.async;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// TODO: quite primitive for now. should utilize the "priority queue" thing developed earlier

/** Pooling asynchronous tasks for execution. */
public class ThreadPool {

    public static final ThreadPool INSTANCE = new ThreadPool(5);    // TODO: better solution, but works for now

    private List<Runnable> single;
    private List<Runnable> continous;

    private List<Thread> threads;

    private boolean running;

    public ThreadPool(int numThreads) {
        single = new LinkedList<>();
        continous = new LinkedList<>();

        running = true;

        threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; ++i) threads.add(new Thread(this::executeTasks));

        for (Thread t : threads) t.start();
    }

    public synchronized void submitOnce(Runnable runnable) {
        single.add(runnable);
        notify();
    }

    // TODO: "as often as you can" vs "once per iteration"
    public synchronized void submitContinous(Runnable runnable) {
        continous.add(runnable);
        notify();
    }

    private synchronized Runnable pickTask() throws InterruptedException {

        while (single.size() == 0 && continous.size() == 0) wait();

        if (single.size() > 0) return single.remove(0);

        Runnable cont = continous.remove(0);

        // TODO: optimize ?
        return () -> {
            try {
                cont.run();
                synchronized(this) { continous.add(cont); notify(); }
            } catch (Exception e) {
                Assertions.warn("continuous job threw exception. removing job from queue. ex:"); // TODO: Assertions accept exceptions for special handling
                e.printStackTrace();
            }

        };
    }

    private void executeTasks() {
        try {
            while (running) {
                Runnable task = pickTask();
                task.run();
            }
        } catch (InterruptedException e) {
            System.out.println("closing ThreadPool thread by interrupt...");
        }

    }

    public void shutdown() {
        running = false;

        single.clear();
        continous.clear(); // TODO: clear any tasks about "re-adding continous"


        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

}