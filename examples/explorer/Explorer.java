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
// UNDONE: Add all Entity base properties: getName, getPath, isDisabled

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

    class IndexNode extends ExplorerNode {
        IndexNode(Index index) {
            super(index, new NoKids());
            setDisplayName(index.getName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(boolean.class, "getAssureUTF8");
                add(int.class, "getBlockSignSize");
                add(String.class, "getBlockSignatureDatabase");
                add(String.class, "getColdPath");
                add(String.class, "getColdPathExpanded");
                add(String.class, "getColdToFrozenDir");
                add(String.class, "getColdToFrozenScript");
                add(boolean.class, "getCompressRawdata");
                add(int.class, "getCurrentDBSizeMB");
                add(String.class, "getDefaultDatabase");
                add(boolean.class, "getEnableRealtimeSearch");
                add(int.class, "getFrozenTimePeriodInSecs");
                add(String.class, "getHomePath");
                add(String.class, "getHomePathExpanded");
                add(String.class, "getIndexThreads");
                add(String.class, "getLastInitTime");
                add(int.class, "getMaxConcurrentOptimizes");
                add(String.class, "getMaxDataSize");
                add(int.class, "getMaxHotBuckets");
                add(int.class, "getMaxHotIdleSecs");
                add(int.class, "getMaxHotIdleSecs");
                add(int.class, "getMaxHotSpanSecs");
                add(int.class, "getMaxMemMB");
                add(int.class, "getMaxMetaEntries");
                add(int.class, "getMaxRunningProcessGroups");
                add(Date.class, "getMaxTime");
                add(int.class, "getMaxTotalDataSizeMB");
                add(int.class, "getMaxWarmDBCount");
                add(int.class, "getMemPoolMB");
                add(String.class, "getMinRawFileSyncSecs");
                add(Date.class, "getMinTime");
                add(int.class, "getPartialServiceMetaPeriod");
                add(int.class, "getQuarantineFutureSecs");
                add(int.class, "getQuarantinePastSecs");
                add(int.class, "getRawChunkSizeBytes");
                add(int.class, "getRotatePeriodInSecs");
                add(int.class, "getServiceMetaPeriod");
                add(String.class, "getSuppressBannerList");
                add(boolean.class, "getSync");
                add(boolean.class, "getSyncMeta");
                add(String.class, "getThawedPath");
                add(String.class, "getThawedPathExpanded");
                add(int.class, "getThrottleCheckPeriod");
                add(int.class, "getTotalEventCount");
                add(boolean.class, "isDisabled");
                add(boolean.class, "isInternal");
            }};
        }
    }

    class IndexesNode extends AbstractNode {
        IndexesNode(EntityCollection<Index> indexes) {
            super(new IndexesKids(indexes));
            setDisplayName("Indexes");
        }
    }

    class IndexesKids extends Children.Keys<Index> {
        EntityCollection<Index> indexes;

        IndexesKids(EntityCollection<Index> indexes) {
            this.indexes = indexes; 
        }

        @Override protected void addNotify() { 
            setKeys(indexes.values());
        }

        @Override protected Node[] createNodes(Index index) { 
            return new Node[] { new IndexNode(index) };
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

    class SavedSearchNode extends ExplorerNode {
        SavedSearchNode(SavedSearch search) {
            super(search, new NoKids());
            setDisplayName(search.getName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getActionEmailSendResults");
                add(String.class, "getActionEmailTo");
                add(String.class, "getAlertExpires");
                add(int.class, "getAlertSeverity");
                add(String.class, "getAlertSuppress");
                add(String.class, "getAlertSuppressPeriod");
                add(String.class, "getAlertTrack");
                add(String.class, "getAlertComparator");
                add(String.class, "getAlertCondition");
                add(String.class, "getAlertThreshold");
                add(String.class, "getAlertType");
                add(String.class, "getCronSchedule");
                add(String.class, "getDescription");
                add(int.class, "getDispatchBuckets");
                add(String.class, "getDispatchEarliestTime");
                add(String.class, "getDispatchLatestTime");
                add(boolean.class, "getDispatchLookups");
                add(int.class, "getDispatchMaxCount");
                add(String.class, "getDispatchMaxTime");
                add(int.class, "getDispatchReduceFreq");
                add(boolean.class, "getDispatchSpawnProcess");
                add(String.class, "getDispatchTimeFormat");
                add(String.class, "getDispatchTtl");
                add(String.class, "getDisplayView");
                add(int.class, "getMaxConcurrent");
                add(String.class, "getNextScheduledTime");
                add(String.class, "getQualifiedSearch");
                add(boolean.class, "getRealtimeSchedule");
                add(String.class, "getRequestUiDispatchApp");
                add(String.class, "getRequestUiDispatchView");
                add(boolean.class, "getRestartOnSearchPeerAdd");
                add(boolean.class, "getRunOnStartup");
                add(String.class, "getSearch");
                add(String.class, "getVsid");
                add(boolean.class, "isActionEmail");
                add(boolean.class, "isActionPopulateLookup");
                add(boolean.class, "isActionRss");
                add(boolean.class, "isActioncScript");
                add(boolean.class, "isActionSummaryIndex");
                add(boolean.class, "isDigestMode");
                add(boolean.class, "isDisabled");
                add(boolean.class, "isScheduled");
                add(boolean.class, "isVisible");
            }};
        }
    }

    class SavedSearchesNode extends AbstractNode {
        SavedSearchesNode(EntityCollection<SavedSearch> searches) {
            super(new SavedSearchesKids(searches));
            setDisplayName("Saved Searches");
        }
    }

    class SavedSearchesKids extends Children.Keys<SavedSearch> {
        EntityCollection<SavedSearch> searches;

        SavedSearchesKids(EntityCollection<SavedSearch> searches) {
            this.searches = searches; 
        }

        @Override protected void addNotify() { 
            setKeys(searches.values());
        }

        @Override protected Node[] createNodes(SavedSearch search) { 
            return new Node[] { new SavedSearchNode(search) };
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
                "settings",
                "apps",
                "indexes",
                "jobs",
                "searches",
                "users"
            };
            setKeys(kinds);
        }

        @Override protected Node[] createNodes(String kind) {
            if (kind.equals("settings"))
                return new Node[] { new SettingsNode(service.getSettings()) };
            if (kind.equals("apps"))
                return new Node[] { new AppsNode(service.getApplications()) };
            if (kind.equals("indexes"))
                return new Node[] { new IndexesNode(service.getIndexes()) };
            if (kind.equals("jobs"))
                return new Node[] { new JobsNode(service.getJobs()) };
            if (kind.equals("searches"))
                return new Node[] { new SavedSearchesNode(service.getSearches()) };
            if (kind.equals("users"))
                return new Node[] { new UsersNode(service.getUsers()) };
            return null;
        }
    }

    class SettingsNode extends ExplorerNode {
        SettingsNode(Settings settings) {
            super(settings, new NoKids());
            setDisplayName("Settings");
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getSplunkDB");
                add(String.class, "getSplunkHome");
                add(boolean.class, "getEnableSplunkWebSSL");
                add(String.class, "getHost");
                add(int.class, "getHttpPort");
                add(int.class, "getMgmtPort");
                add(int.class, "getMinFreeSpace");
                add(String.class, "getPass4SymmKey");
                add(String.class, "getServerName");
                add(String.class, "getSessionTimeout");
                add(boolean.class, "getStartWebServer");
                add(String.class, "getTrustedIP");
            }};
        }
    }

    class UserNode extends ExplorerNode {
        UserNode(User user) {
            super(user, new NoKids());
            setDisplayName(user.getName());
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getDefaultApp");
                add(boolean.class, "getDefaultAppIsUserOverride");
                add(String.class, "getDefaultAppSourceRole");
                add(String.class, "getEmail");
                add(String.class, "getPassword"); // UNDONE: Really?
                add(String.class, "getRealName");
                // UNDONE: Figure out how to handle property of List<String>
                // add(List<String>.class, "getRoles")
            }};
        }
    }

    class UsersNode extends AbstractNode {
        UsersNode(UserCollection users) {
            super(new UsersKids(users));
            setDisplayName("Users");
        }
    }

    class UsersKids extends Children.Keys<User> {
        UserCollection users;

        UsersKids(UserCollection users) {
            this.users = users; 
        }

        @Override protected void addNotify() { 
            setKeys(users.values());
        }

        @Override protected Node[] createNodes(User user) { 
            return new Node[] { new UserNode(user) };
        }
    }
}
