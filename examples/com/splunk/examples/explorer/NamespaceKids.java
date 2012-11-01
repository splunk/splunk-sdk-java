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

import com.splunk.Service;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

class NamespaceKids extends Children.Keys<String> {
    Service service;

    NamespaceKids(Service service) {
        this.service = service;
    }

    @Override protected void addNotify() {
        String[] kinds = new String[] {
            "confs",
            "eventtypes",
            "searches",
            "jobs",
        };
        setKeys(kinds);
    }

    private Node createNode(String kind) {
        if (kind.equals("confs"))
            return new ConfCollectionNode(service.getConfs());

        if (kind.equals("eventtypes"))
            return new EntityCollectionNode(
                "EventTypes", 
                service.getEventTypes(), 
                EventTypeNode.class);

        if (kind.equals("jobs"))
            return new EntityCollectionNode(
                "Jobs", 
                service.getJobs(), 
                JobNode.class);

        if (kind.equals("searches"))
            return new EntityCollectionNode(
                "Saved Searches", 
                service.getSavedSearches(), 
                SavedSearchNode.class);

        return null;
    }

    @Override protected Node[] createNodes(String kind) {
        return new Node[] { createNode(kind) };
    }
}
