/*
 * Copyright 2011 Splunk, Inc.
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

import org.junit.Test;

public class EventTypeTest extends SplunkTestCase {
    final static String assertRoot = "Event Type assert: ";

    void checkEventType(EventType eventType) {
        eventType.getDescription();
        eventType.getPriority();
        eventType.getSearch();
    }

    void checkEventTypes(EventTypeCollection collection) {
        for (EventType eventType : collection.values())
            checkEventType(eventType);
    }

    @Test public void testEventType() throws Exception {
        Service service = connect();

        EventTypeCollection eventTypeCollection = service.getEventTypes();

        if (eventTypeCollection.containsKey("sdk-test"))
            eventTypeCollection.remove("sdk-test");
        assertFalse(assertRoot + "#1",
            eventTypeCollection.containsKey("sdk-test"));

        checkEventTypes(eventTypeCollection);

        String search = "index=_internal *";

        Args args = new Args();
        args.put("description", "Dummy description");
        args.put("disabled", true);
        args.put("priority", 2);
        EventType eventType =
                eventTypeCollection.create("sdk-test", search, args);

        assertTrue(assertRoot + "#2",
            eventTypeCollection.containsKey("sdk-test"));

        assertEquals(assertRoot + "#3", "sdk-test", eventType.getName());
        assertEquals(assertRoot + "#4",
            args.get("description"), eventType.getDescription());
        assertEquals(assertRoot + "#5",
            args.get("priority"), eventType.getPriority());
        assertEquals(assertRoot + "#6", search, eventType.getSearch());
        assertTrue(assertRoot + "#7", eventType.isDisabled());

        eventType.setDescription("Dummy description a second time");
        eventType.setDisabled(true);
        eventType.setPriority(3);
        eventType.update();
        eventType.enable();

        assertEquals(assertRoot + "#8", "sdk-test", eventType.getName());
        assertEquals(assertRoot + "#9", "Dummy description a second time",
            eventType.getDescription());
        assertEquals(assertRoot + "#10", 3, eventType.getPriority());
        assertEquals(assertRoot + "#11", "index=_internal *",
            eventType.getSearch());
        assertFalse(assertRoot + "#12", eventType.isDisabled());

        eventTypeCollection.remove("sdk-test");
        assertFalse(assertRoot + "#13",
            eventTypeCollection.containsKey("sdk-test"));
    }
}
