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

// UNDONE: Boolean properties appear to all be false
// UNDONE: Support for multiple service roots

//
// The NetBeans tutorial on which this sample is based:
//
//   http://blogs.oracle.com/geertjan/entry/netbeans_apis_outside_of_the
//

import com.splunk.*;

import java.awt.Dimension;
import javax.swing.*;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.explorer.view.ContextTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

public class Explorer extends JFrame implements ExplorerManager.Provider { 
    private ExplorerManager manager;
    private RootKids roots;

    Explorer(Service service) {
        this.roots = new RootKids(service);
        this.manager = new ExplorerManager();
        Node root = new AbstractNode(roots);
        root.setDisplayName("Root"); // Not visible
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

    public class AppNode extends AbstractNode {
        Application app;

        AppNode(Application app) {
            super(new NoKids());
            this.app = app;
            setDisplayName(app.getName());
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set props = Sheet.createPropertiesSet();
            try {
                props.put(new PropertySupport.Reflection(
                    app, Boolean.class, "getCheckForUpdates", null));
                props.put(new PropertySupport.Reflection(
                    app, String.class, "getLabel", null));
                props.put(new PropertySupport.Reflection(
                    app, String.class, "getVersion", null));
                props.put(new PropertySupport.Reflection(
                    app, Boolean.class, "isConfigured", null));
                props.put(new PropertySupport.Reflection(
                    app, Boolean.class, "isManageable", null));
                props.put(new PropertySupport.Reflection(
                    app, Boolean.class, "isVisible", null));
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
            sheet.put(props);
            return sheet;
        }
    }

    public class AppsNode extends AbstractNode {
        EntityCollection<Application> apps;

        AppsNode(EntityCollection<Application> apps) {
            super(new AppsKids(apps));
            this.apps = apps;
            setDisplayName("Apps");
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set props = Sheet.createPropertiesSet();
            try {
                props.put(new PropertySupport.Reflection(
                    this, String.class, "getSize", null));
            }
            catch (NoSuchMethodException e) {}
            sheet.put(props);
            return sheet;
        }

        // UNDONE: For some reason NetBeans displays a "<No editors>" message
        // for int properties .. so wrap here for the time being.
        public String getSize() {
            return Integer.toString(apps.size());
        }
    }

    class AppsKids extends Children.Keys<Application> {
        EntityCollection<Application> apps;

        AppsKids(EntityCollection<Application> apps) {
            this.apps = apps;
        }

        @Override protected void addNotify() { 
            setKeys(apps.values());
        }

        @Override protected Node[] createNodes(Application app) {
            return new Node[] { new AppNode(app) };
        }
    }

    class JobsNode extends AbstractNode {
        JobsNode(Service service) {
            super(new JobsKids(service));
            setDisplayName("Jobs");
        }
    }

    class JobsKids extends Children.Keys<Service> {
        Service service;

        JobsKids(Service service) { 
            this.service = service; 
        }

        @Override protected void addNotify() { }

        @Override protected Node[] createNodes(Service service) { 
            return null;
        }
    }

    // UNDONE: Figure out a better way to create leaf nodes.
    class NoKids extends Children.Keys<Object> {
        NoKids() {}

        @Override protected void addNotify() {}

        @Override protected Node[] createNodes(Object key) {
            return null;
        }
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

    class ServiceNode extends AbstractNode {
        Service service;
        ServiceInfo info = null;

        ServiceNode(Service service) {
            super(new ServiceKids(service));
            this.service = service;
            this.info = service.getInfo(); // UNDONE: async
            setDisplayName(info.getServerName());
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set props = Sheet.createPropertiesSet();
            try {
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getBuild", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getCpuArch", null));
                /* UNDONE: Figure out how to get class for following
                props.put(new PropertySupport.Reflection(
                    info, List<String>.class, "getLicenseKeys", null));
                */
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getLicenseSignature", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getLicenseState", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getMasterGuid", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getMode", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getOsBuild", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getOsVersion", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getServerName", null));
                props.put(new PropertySupport.Reflection(
                    info, String.class, "getVersion", null));
                props.put(new PropertySupport.Reflection(
                    info, Boolean.class, "isFree", null));
                props.put(new PropertySupport.Reflection(
                    info, Boolean.class, "isTrial", null));
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
            sheet.put(props);
            return sheet;
        }
    }

    class ServiceKids extends Children.Keys<String> {
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
                return new Node[] { new AppsNode(service.getApplications()) };
            if (kind.equals("jobs"))
                return new Node[] { new JobsNode(service) };
            return null;
        }
    }
}
