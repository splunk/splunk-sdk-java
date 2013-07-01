package com.splunk.modularinput;

import java.io.IOException;
import java.io.InputStream;

/**
 * A stream wrapper that acts as though the underlying stream has terminated every time it blocks.
 */
public class NonblockingInputStream extends InputStream {
    private final InputStream stream;

    public NonblockingInputStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        if (stream.available() != 0) {
            return stream.read();
        } else {
            return -1;
        }
    }
}
