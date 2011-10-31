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

// UNDONE: Date values give "no editor" message in property view.
// UNDONE: Support for multiple service roots

//
// The NetBeans tutorial on which this sample is based:
//
//   http://blogs.oracle.com/geertjan/entry/netbeans_apis_outside_of_the
//

import com.splunk.*;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
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

    public class AppNode extends ExplorerNode<Application> {
        AppNode(Application app) {
            super(app, new NoKids());
            setDisplayName(app.getName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(boolean.class, "getCheckForUpdates");
                add(String.class, "getLabel");
                add(String.class, "getVersion");
                add(boolean.class, "isConfigured");
                add(boolean.class, "isManageable");
                add(boolean.class, "isVisible");
            }};
        }
    }

    public class PropertyInfo {
        public Class datatype;
        public String getter;
        public String setter;

        public PropertyInfo() {}

        public PropertyInfo(Class datatype, String getter, String setter) {
            this.datatype = datatype;
            this.getter = getter;
            this.setter = setter;
        }
    }

    public class PropertyList extends ArrayList<PropertyInfo> {
        public void add(Class datatype, String getter) {
            add(datatype, getter, null);
        }

        public void add(Class datatype, String getter, String setter) {
            add(new PropertyInfo(datatype, getter, setter));
        }

        public Sheet createSheet(Object object) {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set props = Sheet.createPropertiesSet();
            try {
                for (PropertyInfo info : this) {
                    props.put(new PropertySupport.Reflection(
                        object, info.datatype, info.getter, info.setter));
                }
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
            sheet.put(props);
            return sheet;
        }
    }

    public abstract class ExplorerNode<T> extends AbstractNode {
        T value;

        ExplorerNode(T value, Children kids) {
            super(kids);
            this.value = value;
        }

        abstract PropertyList getMetadata();

        @Override protected Sheet createSheet() {
            return getMetadata().createSheet(value);
        }
    }

    public class AppsNode extends ExplorerNode<EntityCollection<Application>> {
        AppsNode(EntityCollection<Application> apps) {
            super(apps, new AppsKids(apps));
            setDisplayName("Apps");
        }

        @Override PropertyList getMetadata() {
             return new PropertyList() {{
                add(int.class, "size");
            }};
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

    class JobNode extends ExplorerNode<Job> {
        JobNode(Job job) {
            super(job, new NoKids());
            setDisplayName(job.getName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(Date.class, "getCursorTime");
                add(String.class, "getDelegate");
                add(int.class, "getDiskUsage");
                add(String.class, "getDispatchState");
                add(float.class, "getDoneProgress");
                add(int.class, "getDropCount");
                add(Date.class, "getEarliestTime");
                add(int.class, "getEventAvailableCount");
                add(int.class, "getEventCount");
                add(int.class, "getEventFieldCount");
                add(boolean.class, "getEventIsStreaming");
                add(boolean.class, "getEventIsTruncated");
                add(String.class, "getEventSearch");
                add(String.class, "getEventSorting");
                add(String.class, "getKeywords");
                add(String.class, "getLabel");
                add(Date.class, "getLatestTime");
                add(int.class, "getNumPreviews");
                add(int.class, "getPriority");
                add(String.class, "getRemoteSearch");
                add(String.class, "getReportSearch");
                add(int.class, "getResultCount");
                add(boolean.class, "getResultIsStreaming");
                add(int.class, "getResultPreviewCount");
                add(float.class, "getRunDuration");
                add(int.class, "getScanCount");
                add(String.class, "getSearch");
                add(String.class, "getSearchLatestTime");
                add(String.class, "getSid");
                add(int.class, "getStatusBuckets");
                add(int.class, "getTtl");
                add(boolean.class, "isDone");
                add(boolean.class, "isFailed");
                add(boolean.class, "isFinalized");
                add(boolean.class, "isPaused");
                add(boolean.class, "isPreviewEnabled");
                add(boolean.class, "isRemoteTimeline");
                add(boolean.class, "isSaved");
                add(boolean.class, "isSavedSearch");
                add(boolean.class, "isZombie");
            }};
        }
    }

    class JobsNode extends AbstractNode {
        JobsNode(JobCollection jobs) {
            super(new JobsKids(jobs));
            setDisplayName("Jobs");
        }
    }

    class JobsKids extends Children.Keys<Job> {
        JobCollection jobs;

        JobsKids(JobCollection jobs) {
            this.jobs = jobs; 
        }

        @Override protected void addNotify() { 
            setKeys(jobs.values());
        }

        @Override protected Node[] createNodes(Job job) { 
            return new Node[] { new JobNode(job) };
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

    class ServiceNode extends ExplorerNode<ServiceInfo> {
        ServiceNode(Service service) {
            super(service.getInfo(), new ServiceKids(service));
            setDisplayName(value.getServerName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(int.class, "getBuild");
                add(String.class, "getCpuArch");
                // UNDONE: Figure out how to get class for following
                // add(List<String>.class, "getLicenseKeys");
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
                "apps",
                "jobs"
            };
            setKeys(kinds);
        }

        @Override protected Node[] createNodes(String kind) {
            if (kind.equals("apps"))
                return new Node[] { new AppsNode(service.getApplications()) };
            if (kind.equals("jobs"))
                return new Node[] { new JobsNode(service.getJobs()) };
            return null;
        }
    }
}
