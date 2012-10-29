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

public class OutputSyslogTest extends SDKTestCase {
    @Test
    public void testOutputSyslog() throws Exception {
        EntityCollection<OutputSyslog> dos = service.getOutputSyslogs();

        if (dos.values().size() == 0) {
            System.out.println("WARNING: No OutputSyslogs to test");
            return;
        }

        for (OutputSyslog entity : dos.values()) {
            // Save
            String server = entity.getServer();
            String type = entity.getType();

            // Probe
            {
                entity.setServer("1.1.1.1:514");
                //entity.setPriority(); -- can't check
                //entity.setTimestampFormat(); -- can't check
                String otherType = "tcp";
                if (type.equals("tcp"))
                    otherType = "udp";
                entity.setType(otherType);
                entity.update();
    
                // check
                assertEquals("1.1.1.1:514", entity.getServer());
                assertEquals(otherType, entity.getType());
            }

            // Restore
            {
                entity.setServer(server);
                entity.setType(type);
                entity.update();
    
                assertEquals(server, entity.getServer());
                assertEquals(type, entity.getType());
            }
        }
    }
}
