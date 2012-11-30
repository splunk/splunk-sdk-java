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

import java.util.Map;

import org.junit.Test;

public class EntityTest extends SDKTestCase {
    private static final String QUERY = "search index=_internal | head 10";
    
    private static final String KEY = "isSavedSearch";
    private static final String VALUE = "0";

    @Test
    public void testMapOverrides() {
        Entity entity = service.getJobs().create(QUERY);
        
        assertFalse(entity.isEmpty());
        assertEquals(VALUE, entity.get(KEY));
        assertTrue(entity.containsKey(KEY));
        assertTrue(entity.containsValue(VALUE));
        assertTrue(entity.keySet().contains(KEY));
        assertTrue(entity.values().contains(VALUE));
        for (Map.Entry<String, Object> e : entity.entrySet()) {
            assertTrue(entity.containsKey(e.getKey()));
            assertTrue(entity.containsValue(e.getValue()));
        }
        
        try {
            entity.put(null, null);
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            entity.putAll(null);
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            entity.clear();
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            entity.remove(null);
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // Good
        }
    }
    
    @Test
    public void testResourceCollection() {
        IndexCollection indexes = service.getIndexes();
        
        try {
            indexes.clear();
            fail("Should've thrown");
        }
        catch (Exception ex) {
            assertTrue(true);
        }
        
        try {
            indexes.entrySet();
            fail("Should've thrown");
        }
        catch (Exception ex) {
            assertTrue(true);
        }
        
        try {
            indexes.put("hello", null);
            fail("Should've thrown");
        }
        catch (Exception ex) {
            assertTrue(true);
        }
        
        try {
            indexes.putAll(null);
            fail("Should've thrown");
        }
        catch (Exception ex) {
            assertTrue(true);
        }
        
        Index main = indexes.get("main");
        assertTrue(indexes.containsValue(main));
        assertTrue(indexes.equals(indexes.items));
        assertTrue(indexes.hashCode() != 0);
        assertTrue(indexes.keySet().contains("main"));
        assertTrue(indexes.valueSize("main") == 1);
    }
}
