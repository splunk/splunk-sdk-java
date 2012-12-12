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

import org.junit.Test;

public class ScriptedInputTest extends SDKTestCase {
    @Test
    public void testMatchNonscriptInputName() {
        assertFalse(InputCollection.matchesInputName(InputKind.Tcp, "1-[]bc", "def"));
        assertTrue(InputCollection.matchesInputName(InputKind.Tcp, "1-[]bc", "1-[]bc"));
    }
    
    @Test
    public void testMatchScriptInputName() {
        assertTrue(InputCollection.matchesInputName(
                InputKind.Script, "abc.py", "$SPLUNK_HOME/etc/apps/boris/bin/abc.py"
        ));
        assertFalse(InputCollection.matchesInputName(
                InputKind.Script, "abc", "$SPLUNK_HOME/etc/apps/boris/bin/abc.py"
        ));
        
        assertTrue(InputCollection.matchesInputName(
                InputKind.Script, "abc.py", "$SPLUNK_HOME\\etc\\apps\\boris\\bin\\abc.py"
        ));
        assertFalse(InputCollection.matchesInputName(
                InputKind.Script, "abc", "$SPLUNK_HOME\\etc\\apps\\boris\\bin\\abc.py"
        ));
    }
    
    @Test
    public void testEntity() {
        // In Splunk 4.2, we can't use $SPLUNK_HOME in the path to the
        // script, so we have to fetch $SPLUNK_HOME separately and interpolate
        // it ourselves.
        String splunkHome = service.getSettings().getSplunkHome();
        InputCollection inputs = service.getInputs();
        inputs.create(
                joinServerPath(new String[] {
                        splunkHome, "etc", "apps", "search", "bin",
                        "bucketdir.py",
                }),
                InputKind.Script,
                Args.create("interval", "0"));
        inputs.refresh();
        
        ScriptInput input = (ScriptInput)inputs.get("bucketdir.py");
        assertNotNull(input);
        
        input.getEndTime();
        input.getGroup();
        input.getRcvBuf();
        input.getSource();
        input.getStartTime();
        
        assertFalse(input.getBoolean("disabled"));
        input.setDisabled(true);
        input.update();
        assertTrue(input.getBoolean("disabled"));
        input.setDisabled(false);
        input.update();
        assertFalse(input.getBoolean("disabled"));
        
        input.remove();
    }
}
