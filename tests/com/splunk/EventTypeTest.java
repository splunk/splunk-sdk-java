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

package com.splunk.sdk.tests.com.splunk;

import com.splunk.*;
import com.splunk.sdk.Command;
import com.splunk.Service;

import junit.framework.Assert;
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

        EntityCollection<EventType> ds = service.getEventTypes();

        for (EventType entity: ds.values()) {
            entity.get(); // force a read
            Assert.assertTrue(entity.getPriority() != -1);
            Assert.assertTrue(entity.getSearch() != null);
            entity.getDescription();
            //UNDONE: more?
        }
    }
}
