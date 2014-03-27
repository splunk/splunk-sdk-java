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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.CharBuffer;

public class InsertRootElementFilterInputStreamTest {
    @Test
    public void testNormalOperation() throws IOException {
        InputStream stream = new ByteArrayInputStream("<?xml encoding=\"UTF-8\"><results/><results/>".getBytes("UTF-8"));
        InputStream filteredStream = new InsertRootElementFilterInputStream(stream);

        String found = SDKTestCase.streamToString(filteredStream);
        String expected = "<?xml encoding=\"UTF-8\"><doc><results/><results/></doc>";

        Assert.assertEquals(expected, found);
    }

    @Test
    public void testNoDtd() throws IOException {
        InputStream stream = new ByteArrayInputStream("<results></results><results></results>".getBytes("UTF-8"));
        InputStream filteredStream = new InsertRootElementFilterInputStream(stream);

        String found = SDKTestCase.streamToString(filteredStream);
        String expected = "<doc><results></results><results></results></doc>";

        Assert.assertEquals(expected, found);
    }

    @Test
    public void testNoResultsElement() throws IOException {
        InputStream stream = new ByteArrayInputStream("boris the mad baboon".getBytes("UTF-8"));
        InputStream filteredStream = new InsertRootElementFilterInputStream(stream);

        String found = SDKTestCase.streamToString(filteredStream);
        String expected = "boris the mad baboon";

        Assert.assertEquals(expected, found);
    }
}
