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

import com.splunk.sdk.Command;

import junit.framework.TestCase;
import org.junit.*;

public class EventTypeTest extends TestCase {
    Command command;

    public EventTypeTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testEventType() throws Exception {
        Service service = connect();

        EntityCollection<EventType> ets = service.getEventTypes();
        if (ets.containsKey("sdk-test")) {
            ets.remove("sdk-test");
        }

        assertFalse(ets.containsKey("sdk-test"));
        Args args = new Args();
        args.put("search","index=_internal *");
        args.put("description", "Dummy description");
        args.put("disabled", true);
        args.put("priority", 2);
        EventType et = ets.create("sdk-test", args);

        for (EventType entity: ets.values()) {
            entity.get(); // force a read
            assertTrue(entity.getPriority() != -1);
            assertTrue(entity.getSearch() != null);
            entity.getDescription();
        }

        assertEquals(et.getDescription(), args.get("description"));
        assertEquals(et.getSearch(), args.get("search"));
        assertEquals(et.getPriority(), args.get("priority"));
        assertEquals(et.getName(), "sdk-test");
        assertTrue(et.isDisabled());

        args.clear();
        args.put("search", "index=_audit *");
        args.put("description", "Dummy description a second time");
        args.put("priority", 3);
        et.update(args);
        et.enable();

        assertEquals(et.getDescription(), args.get("description"));
        assertEquals(et.getSearch(), args.get("search"));
        assertEquals(et.getPriority(), args.get("priority"));
        assertEquals(et.getName(), "sdk-test");
        assertFalse(et.isDisabled());

        ets.remove("sdk-test");
        assertFalse(ets.containsKey("sdk-test"));
    }
}
