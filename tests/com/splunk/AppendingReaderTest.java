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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

public class AppendingReaderTest {
    @Test
    public void testEmptySuffix() throws IOException {
        AppendingReader reader = new AppendingReader(new StringReader("boris"), new StringReader(""));
        CharBuffer buf = CharBuffer.allocate(5);
        reader.read(buf);
        buf.position(0);
        Assert.assertEquals("boris", buf.toString());
    }

    @Test
    public void testEmptyPrefix() throws IOException {
        AppendingReader reader = new AppendingReader(new StringReader(""), new StringReader("boris"));
        CharBuffer buf = CharBuffer.allocate(5);
        reader.read(buf);
        buf.position(0);
        Assert.assertEquals("boris", buf.toString());
    }

    @Test
    public void testProperAppending() throws IOException {
        AppendingReader reader = new AppendingReader(new StringReader("bo"), new StringReader("ris"));
        CharBuffer buf = CharBuffer.allocate(5);
        reader.read(buf);
        reader.read(buf);
        buf.position(0);
        Assert.assertEquals("boris", buf.toString());
    }
}
