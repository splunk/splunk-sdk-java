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

// parse
// typeahead
// timeparser
// jobs.create
//  - result kinds
// 

package com.splunk;

import com.splunk.http.HTTPException;
import com.splunk.http.ResponseMessage;
import com.splunk.sdk.Command;

import junit.framework.TestCase;
import org.junit.*;

public class SearchTest extends TestCase {
    Command command;

    public SearchTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testParse() {
        Args parseArgs;
        ResponseMessage response;

        Service service = connect();

        String query = "search index=sdk-tests * | head 1";

        // Check simple parse.
        response = service.parse(query);
        assertEquals(response.getStatus(), 200);

        // Check parse with parse_only argument.
        parseArgs = new Args("parse_only", true);
        service.parse(query, parseArgs); 
        assertEquals(response.getStatus(), 200);

        // Check parse with multiple arguments.
        parseArgs = new Args();
        parseArgs.put("parse_only", false);
        parseArgs.put("output_mode", "json");
        parseArgs.put("enable_lookups", true);
        parseArgs.put("reload_macros", true);
        service.parse(query, parseArgs); 
        assertEquals(response.getStatus(), 200);
    }

    @Test public void testParseFail() {
        Service service = connect();

        String query = "syntax-error";

        // Check for parse error.
        try {
            service.parse(query);
            fail("Expected a parse error");
        }
        catch (HTTPException e) { 
            assertEquals(e.getStatus(), 400);
        }

        // Check for parse error with args.
        try {
            Args parseArgs = new Args();
            parseArgs.put("parse_only", false);
            parseArgs.put("output_mode", "json");
            parseArgs.put("enable_lookups", true);
            parseArgs.put("reload_macros", true);
            service.parse(query, parseArgs); 
            fail("Expected a parse error");
        }
        catch (HTTPException e) {
            assertEquals(e.getStatus(), 400);
        }

        // Check for argument error.
        try {
            Args parseArgs = new Args("babble", 42);
            service.parse(query, parseArgs);
        }
        catch (HTTPException e) {
            assertEquals(e.getStatus(), 400);
        }
    }
}

