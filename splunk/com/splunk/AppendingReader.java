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
import java.nio.CharBuffer;

public class AppendingReader extends Reader {
    private final Reader suffixReader;
    private final Reader reader;
    private int suffixOffset = 0;

    public AppendingReader(Reader reader, Reader suffixReader) {
        this.reader = reader;
        this.suffixReader = suffixReader;
    }

    @Override
    public int read(char[] chars, int offset, int length) throws IOException {
        int nRead = reader.read(chars, offset, length);
        if (nRead == -1) {
            // Reader is exhausted. Return the suffix.
            return suffixReader.read(chars, offset, length);
        } else {
            // Reader has produced characters.
            return nRead;
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
        suffixReader.close();
    }
}
