package com.splunk;

/**
 * Exception thrown when parsing XML which is syntactically valid, but does not
 * match the schema expected by the Splunk SDK for Java.
 */
public class MalformedDataException extends Exception {
    public MalformedDataException(String s) {
        super(s);
    }
}
