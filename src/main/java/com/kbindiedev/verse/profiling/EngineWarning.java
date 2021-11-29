package com.kbindiedev.verse.profiling;

public class EngineWarning {

    private String warning;

    public EngineWarning(String format, Object... args) {
        warning = String.format(format, args);
    }

    public void print() {
        StringBuilder sb = new StringBuilder();

        sb.append("\u001B[33m");    //yellow

        sb.append(this.getClass().getName());
        sb.append(": ");
        sb.append(warning);
        sb.append("\n");

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2, len = Math.min(30, stackTrace.length); i < len; ++i) {
            sb.append("\t at ");
            sb.append(stackTrace[i].toString());
            sb.append("\n");
        }

        sb.append("\u001B[0m");     //reset

        System.out.println(sb.toString());
    }

}
