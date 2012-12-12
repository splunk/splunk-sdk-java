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

public class ModularInputTest extends InputTest {
    @Test
    public void testModularInputKinds() {
        if (service.versionCompare("5.0") < 0 || !hasTestData()) {
            return;
        } else {
            installApplicationFromTestData("modular-inputs");
            // Will not pick up the new inputs unless a restart is done.
            // Nevertheless Splunk does not request a restart after app installation.
            uncheckedSplunkRestart();
            inputs.refresh();
        }
        
        boolean hasTest2 = false;
        for (InputKind inputKind : inputs.getInputKinds()) {
            if (inputKind.getKind().equals("test2")) {
                hasTest2 = true;
            }
        }
        assertTrue(hasTest2);
    }
    
    @Test
    public void testListModularInputs() {
        if (service.versionCompare("5.0") < 0 || !hasTestData()) {
            return;
        } else {
            installApplicationFromTestData("modular-inputs");
            // Will not pick up the new inputs unless a restart is done.
            // Nevertheless Splunk does not request a restart after app installation.
            uncheckedSplunkRestart();
            inputs.refresh();
        }

        assertFalse(inputs.isEmpty());
        
        String inputName = createTemporaryName();
        inputs.create(
                inputName,
                InputKind.create("test2"),
                new Args("field1", "boris"));

        boolean inputFound = false;
        for (Input input : inputs.values()) {
            if (input.getName().equals(inputName) &&
                    input.getKind().getKind().equals("test2")) {
                inputFound = true;
            }
        }
        assertTrue("Modular input did not show up in list.", inputFound);
    }
}
