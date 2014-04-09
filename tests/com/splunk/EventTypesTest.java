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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventTypesTest extends SDKTestCase {
    private String eventTypeName;
    private EventType eventType;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        eventTypeName = createTemporaryName();
        
        Args args = new Args();
        args.put("search", "index=_internal *");
        args.put("description", "Test event type.");
        args.put("disabled", true);
        args.put("priority", 2);
        eventType = service.getEventTypes().create(eventTypeName, args);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (service.getEventTypes().containsKey(eventTypeName)) {
            eventType.remove();
        }
        Assert.assertFalse(service.getEventTypes().containsKey(eventTypeName));
        
        super.tearDown();
    }

    @Test
    public void testList() {
        EntityCollection<EventType> eventTypes = service.getEventTypes();
        Assert.assertFalse("No event types in system.", eventTypes.size() == 0);

        for (EventType eventType : eventTypes.values()) {
            eventType.getDescription();
            eventType.getPriority();
            eventType.getSearch();
            Assert.assertNotNull(eventType.getTags());
        }
    }

    @Test
    public void testInitializedProperly() {
        Assert.assertEquals(
                "Wrong search in event type",
                "index=_internal *",
                eventType.getSearch()
        );
        Assert.assertEquals(
                "Wrong description in event type",
                "Test event type.",
                eventType.getDescription()
        );
        Assert.assertTrue("Event type not created disabled.", eventType.isDisabled());
        Assert.assertEquals(
                "Event type created with wrong priority.",
                2, eventType.getPriority()
        );
    }

    @Test
    public void testSetEventTypeProperties() {
        eventType.setDescription("abcd");
        eventType.setDisabled(false);
        eventType.setPriority(3);
        eventType.setSearch("index=_internal foo");
        eventType.update();

        Assert.assertEquals("abcd", eventType.getDescription());
        Assert.assertFalse(eventType.isDisabled());
        Assert.assertEquals(3, eventType.getPriority());
        Assert.assertEquals("index=_internal foo", eventType.getSearch());
    }

    @Test
    public void testEnableDisable() {
        Assert.assertTrue(eventType.isDisabled());
        
        eventType.enable();
        Assert.assertFalse(eventType.isDisabled());
        
        eventType.disable();
        Assert.assertTrue(eventType.isDisabled());
    }
}
