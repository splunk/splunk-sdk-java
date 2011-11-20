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

public class FiredAlertTest extends TestCase {
    Command command;

    public FiredAlertTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testFiredAlerts() throws Exception {
        Service service = connect();
        EntityCollection<FiredAlert> alerts = service.getFiredAlerts();

        if (alerts.values().size() == 0) {
            System.out.println("WARNING: no fired alerts detected");
            return;
        }

        // N.B. cant create or modify, only read & delete. skipping delete.
        for (FiredAlert entity: alerts.values()) {
            entity.getAction();
            entity.getAlertType();
            entity.getExpirationTime();
            entity.getSavedSearchName();
            entity.getSeverity();
            entity.getSid();
            entity.getTriggeredAlertCount();
            entity.getTriggerTime();
            entity.getTriggerTimeRendered();
            entity.isDigestMode();
        }
    }
}
