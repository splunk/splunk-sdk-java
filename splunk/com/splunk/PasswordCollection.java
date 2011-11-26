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

import java.util.Map;

public class PasswordCollection extends EntityCollection<Password> {
    PasswordCollection(Service service) {
        // Starting with 4.3 this is available at "storage/passwords"
        super(service, "admin/passwords", Password.class);
    }
    
    public Password create(String name, String password) {
        Args args = new Args("password", password);
        return create(name, args);
    }

    public Password create(String name, String password, String realm) {
        Args args = new Args();
        args.put("password", password);
        args.put("realm", realm);
        return create(name, args);
    }

    // Passwords user the username as a key.
    @Override protected String itemKey(AtomEntry entry) {
        return (String)entry.content.get("username");
    }
}
