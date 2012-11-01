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


import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ModularInputKindsTest extends SDKTestCase {
    private ResourceCollection<ModularInputKind> inputKinds;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (service.versionIsAtEarliest("5.0")) {
            installApplicationFromCollection("modular-inputs");
            inputKinds = service.getModularInputKinds();
        }
    }
    
    public void checkModularInputKind(ModularInputKind m) {
        if (service.versionIsEarlierThan("5.0")) { return; }

        if (m.getName().equals("test1")) {
            assertEquals("Test \"Input\" - 1", m.getTitle());
            assertEquals("xml", m.getStreamingMode());
        } else if (m.getName().equals("test2")) {
            assertEquals("test2", m.getTitle());
            assertEquals("simple", m.getStreamingMode());
        }

    }

    @Test
    public void testListInputKinds() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        for (ModularInputKind kind : inputKinds.values()) {
            checkModularInputKind(kind);
        }
    }

    @Test
    public void testInputByName() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind m;
        m = inputKinds.get("test1");
        checkModularInputKind(m);
        m = inputKinds.get("test2");
        checkModularInputKind(m);
    }

    @Test
    public void testNonexistantArg() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");
        assertFalse(test1.hasArgument("nonexistant_argument"));
        assertNull(test1.getArgument("nonexistant_argument"));
    }

    @Test
    public void testInputKindDescriptionAndTitle() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");
        String expectedDescription1 = "A description of test input 1 with special characters: //;!*%";
        assertEquals(expectedDescription1, test1.getDescription());
        assertEquals("Test \"Input\" - 1", test1.getTitle());

        ModularInputKind test2 = inputKinds.get("test2");
        String expectedDescription2 = "";
        assertEquals(expectedDescription2, test2.getDescription());
        assertEquals("test2", test2.getTitle());
    }

    @Test
    public void testArgDescription() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");

        ModularInputKindArgument arg;

        Map<String,String> expectedValues = new HashMap<String,String>();
        expectedValues.put("key_id", "The key of the system");
        expectedValues.put("no_description", "");
        expectedValues.put("empty_description", "");

        for (String key : expectedValues.keySet()) {
            assertTrue(test1.hasArgument(key));
            arg = test1.getArgument(key);
            String expectedDescription = expectedValues.get(key);
            String foundDescription = arg.getDescription();
            assertEquals(expectedDescription, foundDescription);
        }
    }

    @Test
    public void testArgDataType() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");

        ModularInputKindArgument arg;

        Map<String,ModularInputKindArgument.ModularInputKindArgumentType> expectedValues =
                new HashMap<String,ModularInputKindArgument.ModularInputKindArgumentType>();
        expectedValues.put("number_field", ModularInputKindArgument.ModularInputKindArgumentType.Number);
        expectedValues.put("boolean_field", ModularInputKindArgument.ModularInputKindArgumentType.Boolean);
        expectedValues.put("string_field", ModularInputKindArgument.ModularInputKindArgumentType.String);

        for (String key : expectedValues.keySet()) {
            assertTrue(test1.hasArgument(key));
            arg = test1.getArgument(key);
            assertEquals(expectedValues.get(key), arg.getType());
        }
    }

    @Test
    public void testRequiredOnCreate() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");

        ModularInputKindArgument arg;

        Map<String,Boolean> expectedValues = new HashMap<String,Boolean>();
        expectedValues.put("required_on_create", true);
        expectedValues.put("not_required_on_create", false);

        for (String key : expectedValues.keySet()) {
            assertTrue(test1.hasArgument(key));
            arg = test1.getArgument(key);
            assertEquals((boolean)expectedValues.get(key), arg.getRequiredOnCreate());
        }
    }

    @Test
    public void testRequiredOnEdit() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");

        ModularInputKindArgument arg;

        Map<String,Boolean> expectedValues = new HashMap<String,Boolean>();
        expectedValues.put("arg_required_on_edit", true);
        expectedValues.put("not_required_on_edit", false);

        for (String key : expectedValues.keySet()) {
            assertTrue(test1.hasArgument(key));
            arg = test1.getArgument(key);
            assertEquals((boolean)expectedValues.get(key), arg.getRequiredOnEdit());
        }
    }

    @Test
    public void testGetArguments() {
        if (service.versionIsEarlierThan("5.0")) { return; }

        ModularInputKind test1 = inputKinds.get("test1");
        Map<String, ModularInputKindArgument> args = test1.getArguments();

        Set<String> expectedKeys = new HashSet<String>();
        expectedKeys.add("name");
        expectedKeys.add("resname");
        expectedKeys.add("key_id");
        expectedKeys.add("no_description");
        expectedKeys.add("empty_description");
        expectedKeys.add("arg_required_on_edit");
        expectedKeys.add("not_required_on_edit");
        expectedKeys.add("required_on_create");
        expectedKeys.add("not_required_on_create");
        expectedKeys.add("number_field");
        expectedKeys.add("string_field");
        expectedKeys.add("boolean_field");

        assertEquals(expectedKeys, args.keySet());
    }
}
