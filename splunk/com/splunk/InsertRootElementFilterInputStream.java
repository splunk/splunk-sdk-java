/*
 * Copyright 2014 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.splunk;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Takes an InputStream containing a UTF-8 encoded XML document containing one or more
 * root 'results' elements, and wraps a 'doc' element around
 * all of them so normal XML parsers can handle the stream. Multiple root 'results'
 * elements occur when getting results from an export search.
 *
 * It works by finding the first instance of '<results' and inserting
 * the string '<doc>' before it, and then returning '</doc>' after the end of the stream
 * it is filtering.
 */
class InsertRootElementFilterInputStream extends FilterInputStream {
    private final ByteArrayInputStream suffix = new ByteArrayInputStream("</doc>".getBytes("UTF-8"));
    private boolean wrotePrefix;

    private byte[] oneByte = new byte[1];

    InsertRootElementFilterInputStream(InputStream in) throws IOException {
        // Wrap in with a pushback stream so we can write our modified version back
        // onto the beginning of it.
        super(new PushbackInputStream(in, 512));
        PushbackInputStream pin = (PushbackInputStream)this.in;

        // Read bytes until we reach '>', then push everything we read, followed by "<doc>",
        // back onto the stream. If we run out of input before we reach '>', then don't
        // modify the stream.
        ByteArrayOutputStream beforeResultsChars = new ByteArrayOutputStream();
        ByteArrayOutputStream atResultsChars = new ByteArrayOutputStream();

        int ch;
        while (true) {
            ch = this.in.read();
            if (ch == -1) {
                // Never found a results element to write after, don't touch the stream.
                wrotePrefix = false;
                pin.unread(beforeResultsChars.toByteArray());
                return;
            } else if (ch == (int)'<') {
                // Try extending
                atResultsChars.reset();
                int ech;
                boolean matched = true;
                for (byte b : "results".getBytes("UTF-8")) {
                    ech = this.in.read();
                    atResultsChars.write(ech);
                    if (ech != b) {
                        // Extension failed. Put the bytes back on and search again.
                        pin.unread(atResultsChars.toByteArray());
                        matched = false;
                        break;
                    }
                }

                if (matched) {
                    // If we reach here, the extension succeeded, so we insert <doc>, unread everything,
                    // and return.

                    // Unread the match.
                    pin.unread(atResultsChars.toByteArray());
                    // Unread the opening '<' that led to our extension
                    pin.unread(ch);
                    // Add a '<doc>' element to our read charactes and unread them.
                    beforeResultsChars.write("<doc>".getBytes("UTF-8"));
                    pin.unread(beforeResultsChars.toByteArray());
                    wrotePrefix = true;
                    return;
                } else {
                    // Extension didn't find a match. Put the byte on and continue.
                    beforeResultsChars.write(ch);
                }
            } else {
                // Not a character of interest. Put it on the buffer and continue.
                beforeResultsChars.write(ch);
            }
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        int result = in.read(buffer, offset, length);
        if (result == -1 && wrotePrefix) {
            // No more bytes to read from in, and we have written '<doc>' earlier in the stream
            return suffix.read(buffer, offset, length);
        } else {
            // in still has data to return, so we return it.
            return result;
        }
    }

    @Override
    public int read() throws IOException {
        // Override to dispatch to the other read method. The third overload of read in FilterInputStream
        // already dispatches to read(byte[], int, int).
        if (read(oneByte, 0, 1) == -1) {
            return -1;
        } else {
            return oneByte[0];
        }
    }
}
