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

//
// The NetBeans tutorial on which this sample is based:
//
//   http://blogs.oracle.com/geertjan/entry/netbeans_apis_outside_of_the
//

// UNDONE: Date values give "no editor" message in property view.
// UNDONE: Add support for String[] properties
// UNDONE: Support for multiple service roots
// UNDONE: Add all Entity base properties: getName, getPath, isDisabled
// UNDONE: Figure out how to convey leaf node so that it shows up without arrow

import com.splunk.*;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class Explorer extends JFrame implements ExplorerManager.Provider { 
    private ExplorerManager manager;
    private RootKids roots;

    Explorer(Service service) {
        this.roots = new RootKids(service);
        Node root = new AbstractNode(roots);
        root.setDisplayName("Root"); // Not visible
        this.manager = new ExplorerManager();
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
        BeanTreeView left;
        left = new BeanTreeView();
        left.setRootVisible(false);

        PropertySheetView right;
        right = new PropertySheetView();
        right.setDescriptionAreaVisible(false);
        
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


    class RootKids extends Children.Keys<Service> {
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

    class ServiceNode extends ExplorerNode {
        ServiceNode(Service service) {
            super(service.getInfo(), new ServiceKids(service));
            setDisplayName(service.getInfo().getServerName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(int.class, "getBuild");
                add(String.class, "getCpuArch");
                // UNDONE: add(String[].class, "getLicenseKeys");
                add(String.class, "getLicenseSignature");
                add(String.class, "getLicenseState");
                add(String.class, "getMasterGuid");
                add(String.class, "getMode");
                add(String.class, "getOsBuild");
                add(String.class, "getOsVersion");
                add(String.class, "getServerName");
                add(String.class, "getVersion");
                add(boolean.class, "isFree");
                add(boolean.class, "isTrial");
            }};
        }
    }

    class ServiceKids extends Children.Keys<String> {
        Service service;

        ServiceKids(Service service) {
            this.service = service;
        }

        @Override protected void addNotify() {
            String[] kinds = new String[] {
                "settings",
                "licenses",
                "licenseGroups",
                "licenseSlaves",
                "licenseStacks",
                "apps",
                "indexes",
                "jobs",
                "searches",
                "users"
            };
            setKeys(kinds);
        }

        private Node createNode(String kind) {
            if (kind.equals("apps"))
                return new EntityCollectionNode(
                    "Apps", service.getApplications(), AppNode.class);

            if (kind.equals("indexes"))
                return new EntityCollectionNode(
                    "Indexes", service.getIndexes(), IndexNode.class);

            if (kind.equals("jobs"))
                return new EntityCollectionNode(
                    "Jobs", service.getJobs(), JobNode.class);

            if (kind.equals("licenses"))
                return new EntityCollectionNode(
                    "Licenses", service.getLicenses(), LicenseNode.class);

            if (kind.equals("licenseGroups"))
                return new EntityCollectionNode(
                    "License Groups", 
                    service.getLicenseGroups(), 
                    LicenseGroupNode.class);

            if (kind.equals("licenseSlaves"))
                return new EntityCollectionNode(
                    "License Slaves",
                    service.getLicenseSlaves(),
                    LicenseSlaveNode.class);

            if (kind.equals("licenseStacks"))
                return new EntityCollectionNode(
                    "License Stacks", 
                    service.getLicenseStacks(), 
                    LicenseStackNode.class);

            if (kind.equals("searches"))
                return new EntityCollectionNode(
                    "Saved Searches", 
                    service.getSearches(), 
                    SavedSearchNode.class);

            if (kind.equals("settings"))
                return new SettingsNode(service.getSettings());

            if (kind.equals("users"))
                return new EntityCollectionNode(
                    "Users", service.getUsers(), UserNode.class);

            return null;
        }

        @Override protected Node[] createNodes(String kind) {
            return new Node[] { createNode(kind) };
        }
    }
}
