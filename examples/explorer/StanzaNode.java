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

import com.splunk.Entity;
import com.splunk.Resource;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

class StanzaNode extends AbstractNode {
    Entity entity;

    StanzaNode(Resource value) {
        super(Children.LEAF);
        this.entity = (Entity)value;
        setDisplayName(value.getName());
    }

    @Override protected Sheet createSheet() {
        Sheet.Set props = Sheet.createPropertiesSet();
        for (String key : entity.getContent().keySet()) {
            if (key.equals("eai:acl") || key.equals("eai:attributes"))
                continue;
            props.put(new StanzaProperty(entity, key));
        }
        Sheet sheet = Sheet.createDefault();
        sheet.put(props);
        return sheet;
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
            return (String)stanza.getContent().get(key);
        }
    }
}

