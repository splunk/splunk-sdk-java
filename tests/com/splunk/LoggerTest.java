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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class LoggerTest extends SplunkTestCase {
    final static String assertRoot = "Logger assert: ";

    @Test public void testLogger() throws Exception {
        Service service = connect();

        List<String> expected = Arrays.asList(
            "INFO", "WARN", "ERROR", "DEBUG", "CRIT");

        EntityCollection<Logger> serviceLoggers = service.getLoggers();
        for (Logger ent: serviceLoggers.values()) {
            assertTrue(assertRoot + "#1", expected.contains(ent.getLevel()));
        }

        Logger logger = serviceLoggers.get("AuditLogger");
        String saved = logger.getLevel();
        Args update = new Args();

        // set with setters
        for (String level: expected) {
            logger.setLevel(level);
            logger.update();
            assertEquals(assertRoot + "#2", level, logger.getLevel());
        }

        // restore with map technique
        update.clear();
        update.put("level", saved);
        logger.update(update);
        assertEquals(assertRoot + "#3", saved, logger.getLevel());
    }
}
