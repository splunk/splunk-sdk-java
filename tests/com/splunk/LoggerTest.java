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

import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.*;

public class LoggerTest extends TestCase {
    Command command;

    public LoggerTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testLogger() throws Exception {
        Service service = connect();

        List<String> expected = Arrays.asList(
            "INFO", "WARN", "ERROR", "DEBUG", "CRIT");

        EntityCollection<Logger> loggers = service.getLoggers();
        for (Logger ent: loggers.values()) {
            assertTrue(expected.contains(ent.getLevel()));
        }

        Logger logger = loggers.get("AuditLogger");
        String saved = logger.getLevel();
        Args update = new Args();

        for (String level: expected) {
            update.clear();
            update.put("level", level);
            logger.update(update);
            assertEquals(level, logger.getLevel());
        }

        update.clear();
        update.put("level", saved);
        logger.update(update);
        assertEquals(saved, logger.getLevel());
    }
}
