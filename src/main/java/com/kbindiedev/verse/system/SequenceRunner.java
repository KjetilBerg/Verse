package com.kbindiedev.verse.system;

import java.util.*;

/**
 * Executes {@link Runnable} in order of their priorities.
 * Runnable and priorities are defined by the user.
 *
 * If two jobs have the same priority, then they are executed by order of registration.
 *
 * TODO: tests
 */
public class SequenceRunner {

    private PriorityQueue<JobList> queue;
    private HashMap<Integer, JobList> activeJobListsByPriority;
    private HashSet<Integer> jobsInQueueById;
    private int id;

    public SequenceRunner() {
        queue = new PriorityQueue<>(Comparator.comparingInt(j -> j.priority));
        activeJobListsByPriority = new HashMap<>();
        jobsInQueueById = new HashSet<>();
        id = 0;
    }

    /**
     * Add a new job to this SequenceRunner.
     * The job will immediately be added to the queue of active jobs to be run by {@link #runOne()}.
     * If there exist multiple jobs of the same priority, then they are executed by order of registration.
     * @param runnable - The runnable that should be executed.
     * @param priority - The priority of the job.
     * @return the id of the job.
     */
    public int addJob(Runnable runnable, int priority) {
        ensureExistsListForPriority(priority);

        Job job = new Job(runnable, id++);
        activeJobListsByPriority.get(priority).addJob(job);
        jobsInQueueById.add(job.id);
        return job.id;
    }

    /**
     * Check if a job is currently in the queue by its id.
     * The job is only removed from the queue after its execution has finished and returned.
     * @param id - The id of the job to look for.
     * @return whether or not the job by the given id exists in the queue.
     */
    public boolean isJobInQueue(int id) { return jobsInQueueById.contains(id); }

    /**
     * Check if this SequenceRunner has jobs that have not been executed yet.
     * @return true if there are jobs to be executed, false otherwise.
     */
    public boolean hasJobs() { return queue.size() > 0; }

    /**
     * Execute a single job from the queue.
     * @return true if a job was executed, false if the queue was empty.
     */
    public boolean runOne() {
        if (!hasJobs()) return false;

        Job job = queue.peek().poll();
        removeHeadListIfEmpty();

        job.runnable.run();
        jobsInQueueById.remove(job.id);
        return true;
    }

    /**
     * Execute all jobs in the queue.
     * @return the number of jobs that were performed.
     */
    public int runAll() {
        int count = 0;
        while (runOne()) count++;
        return count;
    }

    /**
     * Create a new JobList and add it to {@link #queue} and {@link #activeJobListsByPriority}
     *      if there exists none for the given priority.
     * @param priority - The priority.
     */
    private void ensureExistsListForPriority(int priority) {
        if (activeJobListsByPriority.containsKey(priority)) return;
        JobList list = new JobList(priority);
        queue.add(list);
        activeJobListsByPriority.put(priority, list);
    }

    /** Check if the head list is empty, and remove it if it is. */
    private void removeHeadListIfEmpty() {
        JobList list = queue.peek();
        if (list == null) return;
        if (list.hasJob()) return;

        queue.poll();
        activeJobListsByPriority.remove(list.priority);
    }


    private static class JobList {
        private List<Job> jobs;
        private int priority;
        public JobList(int priority) { jobs = new ArrayList<>(); this.priority = priority; }
        public Job poll() { return jobs.remove(0); }
        public boolean hasJob() { return jobs.size() > 0; }
        public void addJob(Job job) { jobs.add(job); }
    }

    private static class Job {
        private Runnable runnable;
        private int id;
        public Job(Runnable runnable, int id) { this.runnable = runnable; this.id = id; }
    }

}
