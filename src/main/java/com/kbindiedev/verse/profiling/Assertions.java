package com.kbindiedev.verse.profiling;

public class Assertions {

    private static final AssertLevel assertLevel = AssertLevel.ERROR;


    public static void warn(String message, Object... args) {
        intelligentLog("WARN", message, args);
        azzert(message, AssertLevel.WARN);
    }

    public static void error(String message, Object... args) {
        intelligentLog("ERR", message, args);
        azzert(message, AssertLevel.ERROR);
    }

    /** assert false : message, if myLevel >= assertLevel */
    private static void azzert(String message, AssertLevel myLevel) {
        assert (myLevel.getLevel() < assertLevel.getLevel()) : message;
    }

    private static void intelligentLog(String prefix, String message, Object... args) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (trace.length < 4) { System.out.printf(prefix + ": " + message, args); return; }

        StackTraceElement location = trace[3];
        //System.out.printf(prefix + ": " + location.getClassName() + " # " + location.getMethodName() + " (line " + location.getLineNumber() + ") - " + message + "\n", args);
        System.out.printf(prefix + ": " + location.toString() + " - " + message + "\n", args);
    }

    private enum AssertLevel {
        ERROR(0), WARN(1);

        private int level;
        AssertLevel(int level) { this.level = level; }
        public int getLevel() { return level; }
    }

}
