/*
 * Copyright 2014 Splunk, Inc.
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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the type of a field in a data model object.
 */
public enum FieldType {
    STRING {
        public String toString() { return "string"; }
    },
    NUMBER {
        public String toString() { return "number"; }
    },
    BOOLEAN {
        public String toString() { return "boolean"; }
    },
    IPV4 {
        public String toString() { return "ipv4"; }
    },
    TIMESTAMP {
        public String toString() { return "timestamp"; }
    },
    CHILDCOUNT {
        public String toString() { return "childcount"; }
    },
    OBJECTCOUNT {
        public String toString() { return "objectcount"; }
    },
    UNDEFINED {
        public String toString() {
            throw new UnsupportedOperationException("No serialization for undefined field type.");
        }
    };

    private final static Map<String, FieldType> typeLookup = new HashMap<String, FieldType>() {{
        put("string", STRING);
        put("number", NUMBER);
        put("boolean", BOOLEAN);
        put("ipv4", IPV4);
        put("timestamp", TIMESTAMP);
        put("childcount", CHILDCOUNT);
        put("objectcount", OBJECTCOUNT);
    }};

    public static FieldType parseType(String text) {
        FieldType result = typeLookup.get(text.toLowerCase());
        if (result == null) {
            result = UNDEFINED;
        }
        return result;
    }

};
