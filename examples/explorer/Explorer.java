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

// UNDONE: Support for multiple service roots
// CONSIDER: Rename ServiceExplorer

//
// The NetBeans tutorial on which this sample is based:
//
//   http://blogs.oracle.com/geertjan/entry/netbeans_apis_outside_of_the
//

import com.splunk.*;

import java.awt.Dimension;
import javax.swing.*;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.*; // UNDONE
import org.openide.explorer.view.ContextTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class Explorer extends JFrame implements ExplorerManager.Provider { 
    private ExplorerManager manager;
    private RootKids roots;

    Explorer(Service service) {
        this.roots = new RootKids(service);
        this.manager = new ExplorerManager();
        Node root = new AbstractNode(roots);
        root.setName("Root");
        this.manager.setRootContext(root);
        initialize();
    }

    public static Explorer create(Service service) {
        return new Explorer(service);
    }

    public ExplorerManager getExplorerManager() {
        return this.manager;
    }

    void initialize() {
        ContextTreeView left;
        left = new ContextTreeView();
        left.setRootVisible(false);

        PropertySheetView right;
        right = new PropertySheetView();
        
        JSplitPane splitPane;
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(0.4);
        splitPane.setPreferredSize(new Dimension(400, 200));
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(150);

        setTitle("Splunk Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(splitPane);
        setSize(500, 400);
    }

    private class RootKids extends Children.Keys<Service> {
        Service service;

        RootKids(Service service) {
            this.service = service;
        }

        @Override protected void addNotify() {
            setKeys(new Service[] { service });
        }

        @Override protected Node[] createNodes(Service service) {
            return new Node[] { new ServiceNode(service) };
        }
    }

    final class ServiceNode extends AbstractNode {
        Service service;

        ServiceNode(Service service) {
            super(new ServiceKids(service));
            this.service = service;
            setName("Service"); // UNDONE: Get server name
        }
    }

    final class ServiceKids extends Children.Keys<String> {
        Service service;

        ServiceKids(Service service) {
            this.service = service;
        }

        @Override protected void addNotify() {
            String[] kinds = new String[] {
                "apps",
                "jobs"
            };
            setKeys(kinds);
        }

        @Override protected Node[] createNodes(String kind) {
            if (kind.equals("apps"))
                return new Node[] { new AppsNode(service) };
            if (kind.equals("jobs"))
                return new Node[] { new JobsNode(service) };
            return null;
        }
    }

    final class AppsNode extends AbstractNode {
        AppsNode(Service service) {
            super(new AppsKids(service));
            setName("Apps");
        }
    }

    final class AppsKids extends Children.Keys<Service> {
        Service service;

        AppsKids(Service service) {
            this.service = service;
        }

        @Override protected void addNotify() { }

        @Override protected Node[] createNodes(Service service) {
            return null;
        }
    }

    final class JobsNode extends AbstractNode {
        JobsNode(Service service) {
            super(new JobsKids(service));
            setName("Jobs");
        }
    }

    final class JobsKids extends Children.Keys<Service> {
        Service service;

        JobsKids(Service service) { 
            this.service = service; 
        }

        @Override protected void addNotify() { }

        @Override protected Node[] createNodes(Service service) { 
            return null;
        }
    }
}
