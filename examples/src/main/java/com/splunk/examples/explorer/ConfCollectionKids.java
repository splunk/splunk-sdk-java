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

import com.splunk.ConfCollection;
import com.splunk.Entity;
import com.splunk.EntityCollection;

import java.util.Collection;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

class ConfCollectionKids extends Children.Keys<EntityCollection> {
    ConfCollection confs;

    ConfCollectionKids(ConfCollection confs) {
        this.confs = confs;
    }

    @Override protected void addNotify() {
        Collection<EntityCollection<Entity>> values = confs.values();
        setKeys(values);
    }

    @Override protected Node[] createNodes(EntityCollection conf) {
        return new Node[] { 
            new EntityCollectionNode(conf.getTitle(), conf, StanzaNode.class)
        };
    }
}
