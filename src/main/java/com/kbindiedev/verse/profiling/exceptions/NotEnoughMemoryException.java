package com.kbindiedev.verse.profiling.exceptions;

/** When there is not enough memory upon an allocation request, this exception may be thrown. This is an unchecked exception */
public class NotEnoughMemoryException extends RuntimeException {

    public NotEnoughMemoryException(String message) { super(message); }

}
