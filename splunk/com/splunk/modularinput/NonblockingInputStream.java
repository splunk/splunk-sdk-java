/*
 * Copyright 2013 Splunk, Inc.
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

package com.splunk.modularinput;

import java.io.IOException;
import java.io.InputStream;

/**
 * The {@code NonblockingInputStream} class is a stream wrapper that acts as though
 * the underlying stream has terminated every time it blocks.
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
