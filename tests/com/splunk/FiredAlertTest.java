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
import com.splunk.sdk.Program;
import com.splunk.Service;

import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.Assert;

import org.junit.*;

public class FiredAlertTest extends TestCase {
    Program program = new Program();

    public FiredAlertTest() {}

    Service connect() throws IOException {
        return new Service(
            program.host, program.port, program.scheme)
                .login(program.username, program.password);
    }

    private Service waitForSplunk() throws Exception {
        // there is still a race condition here: if the restart takes more
        // than 5 seconds to percolate through splunk, apps will not be
        // reset
        int retry = 10;
        while (retry > 0) {
            Thread.sleep(5000); // 5 seconds
            retry = retry-1;
            try {
                return connect();
            }
            catch (Exception e) {
                // server not back yet
            }
        }
        Assert.fail("Splunk service did not restart");
        return null;
    }


    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    @Test public void testFiredAlerts() throws Exception {
        Service service = connect();
        EntityCollection<FiredAlert> alerts = service.getFiredAlerts();

        if (alerts.values().size() == 0) {
            System.out.println("WARNING: no fired elerts detected");
            return;
        }

        for (FiredAlert entity: alerts.values()) {
            entity.getAction();
            entity.getAlertType();
            entity.getExpirationTime();
            entity.getSavedSearchName();
            entity.getSeverity();
            entity.getSid();
            entity.getTriggerTime();
        }
    }
}