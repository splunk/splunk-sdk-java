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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Base class of tests that need to access the service's InputCollection
 * or otherwise need to manipulate Input entities.
 *
 * Class was abstract, and abstract has only been removed, and testNop added,
 * to make Ant's test runner happy.
 */
public class InputTest extends SDKTestCase {
    protected InputCollection inputs;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        inputs = service.getInputs();
        
        removeTestInputs();
    }
    
    @After
    public void tearDown() throws Exception {
        removeTestInputs();
        
        super.tearDown();
    }

    @Test
    public void testNop() {} // Here only to make Ant's test runner happy.

    @Test
    public void testRemoveFromCollection() {
        Args namespace = Args.create();
        namespace.put("owner", "nobody");
        namespace.put("app", "search");

        inputs.create("2911", InputKind.Tcp);
        assertTrue(inputs.containsKey("2911"));
        inputs.remove("2911", namespace);
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                inputs.refresh();
                return !inputs.containsKey("2911");
            }
        });
    }

    private void removeTestInputs() {
        for (Input input : inputs.refresh().values()) {
            final String inputName = input.getName();
            if (inputName.startsWith("delete-me")) {
                input.remove();
                assertEventuallyTrue(new EventuallyTrueBehavior() {
                    @Override
                    public boolean predicate() {
                        inputs.refresh();
                        return !inputs.containsKey(inputName);
                    }
                });
            }
        }
    }
    
    // === Utility ===
    
    protected void deleteInputIfExists(String name) {
        if (inputs.containsKey(name)) {
            inputs.remove(name);
            inputs.refresh();
        }
        assertFalse(inputs.containsKey(name));
    }
}
