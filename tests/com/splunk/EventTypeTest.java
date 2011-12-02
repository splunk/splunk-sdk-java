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
        assertFalse(eventTypeCollection.containsKey("sdk-test"));

        checkEventTypes(eventTypeCollection);

        String search = "index=_internal *";

        Args args = new Args();
        args.put("description", "Dummy description");
        args.put("disabled", true);
        args.put("priority", 2);
        EventType eventType =
                eventTypeCollection.create("sdk-test", search, args);

        assertTrue(eventTypeCollection.containsKey("sdk-test"));

        assertEquals(eventType.getName(), "sdk-test");
        assertEquals(eventType.getDescription(), args.get("description"));
        assertEquals(eventType.getPriority(), args.get("priority"));
        assertEquals(eventType.getSearch(), search);
        assertTrue(eventType.isDisabled());

        args = new Args();
        args.put("search", "index=_audit *");
        args.put("description", "Dummy description a second time");
        args.put("priority", 3);
        eventType.update(args);
        eventType.enable();

        assertEquals(eventType.getName(), "sdk-test");
        assertEquals(eventType.getDescription(), args.get("description"));
        assertEquals(eventType.getPriority(), args.get("priority"));
        assertEquals(eventType.getSearch(), args.get("search"));
        assertFalse(eventType.isDisabled());

        eventTypeCollection.remove("sdk-test");
        assertFalse(eventTypeCollection.containsKey("sdk-test"));
    }
}
