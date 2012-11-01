/*
 * Copyright 2012 Splunk, Inc.
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

import java.lang.reflect.Method;

import org.junit.Test;

public class PortInputTest extends InputTest {
    private static final InputKind[] INPUT_KINDS_WITH_RESTRICTABLE_HOST = {
            InputKind.Tcp, InputKind.TcpSplunk, InputKind.Udp
    };
    
    @Test
    public void testManipulateRestrictToHostProperty() {
        for (InputKind inputKind : INPUT_KINDS_WITH_RESTRICTABLE_HOST) {
            inputs.refresh();
            testUnrestrictedHost(inputKind);
            
            inputs.refresh();
            testSetHost(inputKind);
            
            inputs.refresh();
            testUnsetHost(inputKind);
            
            inputs.refresh();
            testUpdateHost(inputKind);
        }
    }
    
    private void testUnrestrictedHost(InputKind inputKind) {
        String port = "9999";   // test port
        
        deleteInputIfExists(port);
        
        // Create
        inputs.create(port, inputKind);
        assertTrue(inputs.containsKey(port));
        Input input = inputs.get(port);
        
        // Verify getPort() is correct
        assertEquals(Integer.parseInt(port), getPort(input));
        
        // Clean up
        input.remove();
    }
    
    private void testSetHost(InputKind inputKind) {
        String host = "four.five.com";
        String port = "9999";   // test port
        
        deleteInputIfExists(host + ":" + port);
        deleteInputIfExists(port);
        
        // Create
        inputs.create(port, inputKind);
        assertTrue(inputs.containsKey(port));
        Input input = inputs.get(port);
        
        // Update host restriction
        if (tryUpdateRestrictToHost(input, host)) {
            // Make sure it can be reloaded properly (under its new path)
            input.refresh();
            assertTrue(inputs.refresh().containsKey(host + ":" + port));
        }
        
        // Verify getPort() is correct
        assertEquals(Integer.parseInt(port), getPort(input));
        
        // Clean up
        input.remove();
    }
    
    private void testUnsetHost(InputKind inputKind) {
        if (WORKAROUND_KNOWN_BUGS) {
            if (inputKind == InputKind.Udp) {
                // SPL-57264: Cannot manipulate a UDP port that is host restricted
                return;
            }
        }
        
        String host = "four.five.com";
        String port = "9999";   // test port
        
        deleteInputIfExists(host + ":" + port);
        deleteInputIfExists(port);
        
        // Create
        inputs.create(port, inputKind, new Args("restrictToHost", host));
        assertTrue(inputs.containsKey(host + ":" + port));
        Input input = inputs.get(host + ":" + port);
        
        // Update host restriction
        if (tryUpdateRestrictToHost(input, "")) {
            // Make sure it can be reloaded properly (under its new path)
            input.refresh();
            assertTrue(inputs.refresh().containsKey(port));
        }
        
        // Verify getPort() is correct
        assertEquals(Integer.parseInt(port), getPort(input));
        
        // Clean up
        input.remove();
    }
    
    private void testUpdateHost(InputKind inputKind) {
        if (WORKAROUND_KNOWN_BUGS) {
            if (inputKind == InputKind.Udp) {
                // SPL-57264: Cannot manipulate a UDP port that is host restricted
                return;
            }
        }
        
        String host1 = "four.five.com";
        String host2 = "six.seven.com";
        String port = "9999";   // test port
        
        deleteInputIfExists(host1 + ":" + port);
        deleteInputIfExists(host2 + ":" + port);
        
        // Create
        inputs.create(port, inputKind, new Args("restrictToHost", host1));
        assertTrue(inputs.containsKey(host1 + ":" + port));
        Input input = inputs.get(host1 + ":" + port);
        
        // Update host restriction
        if (tryUpdateRestrictToHost(input, host2)) {
            // Make sure it can be reloaded properly (under its new path)
            input.refresh();
            assertTrue(inputs.refresh().containsKey(host2 + ":" + port));
        }
        
        // Verify getPort() is correct
        assertEquals(Integer.parseInt(port), getPort(input));
        
        // Clean up
        input.remove();
    }
    
    private boolean tryUpdateRestrictToHost(Input input, String host) {
        setRestrictToHost(input, host);
        try {
            input.update();
            if (service.versionCompare("5.0") < 0) {
                fail("Should not be able to update 'restrictToHost' property on Splunk < 5.0.");
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            if (service.versionCompare("5.0") < 0) {
                // Expected behavior
                return false;
            } else {
                throw e;
            }
        }
    }
    
    private static void setRestrictToHost(Input input, String host) {
        try {
            Method setRestrictToHost = input.getClass().getMethod(
                    "setRestrictToHost", String.class);
            setRestrictToHost.invoke(input, host);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static int getPort(Input input) {
        try {
            Method getPort = input.getClass().getMethod("getPort");
            return (Integer) getPort.invoke(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
