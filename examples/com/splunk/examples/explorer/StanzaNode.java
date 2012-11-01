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

package com.splunk.examples.explorer;

import com.splunk.Entity;

import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

class StanzaNode extends EntityNode {
    StanzaNode(Entity value) {
        super(value);
        setDisplayName(value.getName());
    }

    // Implement createSheet directly in order to dynamically construct
    // based on the contents of the stanza.
    @Override protected Sheet createSheet() {
        Entity entity = (Entity)value;
        Sheet.Set props = Sheet.createPropertiesSet();
        for (String key : entity.keySet()) {
            if (key.equals("eai:acl") || key.equals("eai:attributes"))
                continue;
            props.put(new StanzaProperty(entity, key));
        }
        Sheet sheet = Sheet.createDefault();
        sheet.put(props);
        return sheet;
    }

    // This should never be called because we implement createSheet directly.
    @Override protected PropertyList getMetadata() {
        throw new UnsupportedOperationException();
    }

    class StanzaProperty extends PropertySupport.ReadOnly<String> {
        private String key;
        private Entity stanza;

        StanzaProperty(Entity stanza, String key) {
            super(key, String.class, key, null);
            this.key = key;
            this.stanza = stanza;
        }

        @Override public String getValue() {
            return (String)stanza.get(key);
        }
    }
}

