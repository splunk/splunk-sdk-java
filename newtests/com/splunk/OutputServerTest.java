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

public class OutputServerTest extends SDKTestCase {
    @Test
    public void testOutputServer() throws Exception {
        EntityCollection<OutputServer> outputServers =
                service.getOutputServers();

        if (outputServers.values().size() == 0) {
            System.out.println("WARNING: No OutputServers to test");
            return;
        }

        for (OutputServer outputServer : outputServers.values()) {
            // Getters throw no exceptions
            // NOTE: No corresponding setters.
            outputServer.getDestHost();
            outputServer.getDestIp();
            outputServer.getDestPort();
            outputServer.getMethod();
            outputServer.getSourcePort();
            outputServer.getStatus();

            // Setters throw no exceptions.
            // NOTE: No corresponding getters.
            /*
            outputServer.setMethod();
            outputServer.setSslAltNameToCheck();
            outputServer.setSslCertPath();
            outputServer.setSslCipher();
            outputServer.setSslCommonNameToCheck();
            outputServer.setSslPassword();
            outputServer.setSslRootCAPath();
            outputServer.setSslVerifyServerCert();
            */
            
            // Getter on "allCollections" endpoint throw no exceptions
            OutputServerAllConnections outputServerAllConnections =
                    outputServer.allConnections();
            outputServerAllConnections.getDestHost();
            outputServerAllConnections.getDestIp();
            outputServerAllConnections.getDestPort();
            outputServerAllConnections.getSourcePort();
            outputServerAllConnections.getStatus();
        }
    }
}
