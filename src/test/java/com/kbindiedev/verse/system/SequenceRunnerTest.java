package com.kbindiedev.verse.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequenceRunnerTest {

    private List<Integer> done;
    private SequenceRunner runner;

    private Runnable run1 = () -> done.add(1);
    private Runnable run2 = () -> done.add(2);
    private Runnable run3 = () -> done.add(3);
    private Runnable run4 = () -> done.add(4);
    private Runnable run5 = () -> done.add(5);

    @BeforeEach
    public void setup() {
        done = new ArrayList<>();
        runner = new SequenceRunner();
    }

    @Test
    public void executesInOrder() {
        runner.addJob(run1, 1);
        runner.addJob(run2, 2);
        runner.addJob(run3, 3);
        runner.addJob(run4, 4);
        runner.addJob(run5, 5);

        int count = runner.runAll();

        assertEquals(5, count, "5 jobs should have been run");

        List<Integer> order = listify(1, 2, 3, 4, 5);
        assertEquals(order, done, "jobs done in the wrong order");
    }

    @Test
    public void executesOutOfOrder() {
        runner.addJob(run1, 3);
        runner.addJob(run2, 2);
        runner.addJob(run3, 4);
        runner.addJob(run4, 1);
        runner.addJob(run5, 7);

        int count = runner.runAll();

        assertEquals(5, count, "5 jobs should have been run");

        List<Integer> order = listify(4, 2, 1, 3, 5);
        assertEquals(order, done, "jobs done in the wrong order");
    }

    @Test
    public void jobIntroducesNewJob() {
        run1 = () -> { done.add(1); runner.addJob(run2, 5); };

        runner.addJob(run1, 3);
        runner.addJob(run3, 8);
        runner.addJob(run4, 1);
        runner.addJob(run5, 7);

        int count = runner.runAll();

        assertEquals(5, count, "5 jobs should have been run");

        List<Integer> order = listify(4, 1, 2, 5, 3);
        assertEquals(order, done, "jobs done in the wrong order");
    }

    @Test
    public void samePriorityHappensByOrderOfRegistration() {

        run1 = () -> { done.add(1); runner.addJob(run2, 0); };

        runner.addJob(run1, 0);
        runner.addJob(run3, 0);
        runner.addJob(run5, 0);
        runner.addJob(run4, 0);

        int count = runner.runAll();

        assertEquals(5, count, "5 jobs should have been run");

        List<Integer> order = listify(1, 3, 5, 4, 2);
        assertEquals(order, done, "jobs done in the wrong order");
    }

    private <T> List<T> listify(T... list) {
        return new ArrayList<>(Arrays.asList(list));
    }

}