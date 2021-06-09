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

import com.splunk.Args;
import com.splunk.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

class NamespacesKids extends Children.Keys<String> {
    Service service;

    NamespacesKids(Service service) {
        this.service = service;
    }

    @Override protected void addNotify() {
        Set<String> names = service.getApplications().keySet();
        int count = 3 + names.size();
        List<String> keys = new ArrayList<String>(count);
        keys.add("<all>");
        keys.add("<system>");
        keys.add("<global>");
        keys.addAll(names);
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        setKeys(keys.toArray(new String[count]));
    }

    private Node createNode(String name) {

        if (name.equals("<all>"))
            return createNode("<all>", "-");

        if (name.equals("<global>"))
            return createNode("<global>", "-");
            // UNDONE: return new GLobalNamespaceNode()

        if (name.equals("<system>"))
            return createNode("<system>", "system");

        if (service.getApplications().get(name).isDisabled()) {
            Node node = new AbstractNode(Children.LEAF);
            node.setDisplayName(name);
            return node;
        }
                
        return createNode(name, name);
    }

    private Node createNode(String displayName, String app) {
        // Clone the root service, scoped to the requested namespace
        Args args = new Args();
        args.put("host", service.getHost()); 
        args.put("port", service.getPort());
        args.put("scheme", service.getScheme());
        args.put("username", service.getUsername());
        args.put("password", service.getPassword());
        args.put("app", app);
        args.put("owner", "-");
        Service scope = Service.connect(args);

        Node node = new ExplorerNode(scope, new NamespaceKids(scope));
        node.setDisplayName(displayName);
        return node;
    }

    @Override protected Node[] createNodes(String kind) {
        return new Node[] { createNode(kind) };
    }
}

