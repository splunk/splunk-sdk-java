/*
 * Copyright 2012 Splunk, Inc.
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

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UtilTest extends SDKTestCase {
    @Test
    public void testJoin() {
        List<String> emptyList = new ArrayList<String>();
        assertEquals("", Util.join("/", emptyList));

        List<String> oneElementList = new ArrayList<String>();
        oneElementList.add("abcd");
        assertEquals("abcd", Util.join("/", oneElementList));

        List<String> fullList = new ArrayList<String>();
        fullList.add("abcd");
        fullList.add("defg");
        assertEquals(
                "abcd/defg",
                Util.join("/", fullList)
        );
    }

    @Test
    public void testJoinOnArray() {
        String[] emptyArray = {};
        assertEquals("", Util.join("/", emptyArray));

        String[] oneElementArray = {"abcd"};
        assertEquals("abcd", Util.join("/", oneElementArray));

        String[] fullArray = {"abcd", "defg"};
        assertEquals(
                "abcd/defg",
                Util.join("/", fullArray)
        );
    }

    @Test
    public void testSubstringAfterSucceeds() {
        assertEquals(
                "efg",
                Util.substringAfter("abcdefg", "cd", "boris")
        );
    }

    @Test
    public void testSubstringAfterFails() {
        assertEquals(
                "boris",
                Util.substringAfter("abcdefg", "pq", "boris")
        );
    }

    @Test
    public void testArgs() {
        Args args = Args.create();
        assertTrue(args != null);
        assertTrue(args instanceof Args);
        
        assertTrue(Args.encode((String)null).equals(""));
    }

    @Test
    public void testValue() {
        assertEquals(1024, Value.toByteCount("1KB"));
        assertEquals(1024 * 1024, Value.toByteCount("1MB"));
        assertEquals(1024 * 1024 * 1024, Value.toByteCount("1GB"));
        
        try {
            Value.toByteCount("0GGGGB");
            fail("Should error!");
        } catch(Exception e) {
            // nothing
        }
    }
}
