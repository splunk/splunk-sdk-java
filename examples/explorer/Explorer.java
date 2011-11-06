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

    class AppNode extends EntityNode<Application> {
        AppNode(Application app) {
            super(app);
            String displayName = app.getLabel();
            if (displayName == null) displayName = app.getName();
            setDisplayName(displayName);
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

    class AppsNode extends EntityCollectionNode<Application> {
        AppsNode(EntityCollection<Application> apps) {
            super("Apps", apps);
        }

        @Override Node createKid(Application app) {
            return new AppNode(app);
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

    // Abstract node that simplifies the creation of node metadata.
    abstract class ExplorerNode<T> extends AbstractNode {
        T value;

        ExplorerNode(T value) {
            super(new NoKids());
            this.value = value;
        }

        ExplorerNode(T value, Children kids) {
            super(kids);
            this.value = value;
        }

        abstract PropertyList getMetadata();

        @Override protected Sheet createSheet() {
            return getMetadata().createSheet(value);
        }
    }

    abstract class EntityNode<T extends Entity> extends ExplorerNode<T> {
        EntityNode(T entity) {
            super(entity);
            setDisplayName(entity.getName());
        }
    }

    // Abstract class that generalizes an explorer node for any EntityCollection
    abstract class EntityCollectionNode<TEntity extends Entity>
        extends ExplorerNode<EntityCollection> 
    {
        EntityCollectionNode(String title, EntityCollection collection) {
            super(collection);
            setDisplayName(String.format("%s (%d)", title, collection.size()));
            setChildren(new EntityCollectionKids(this));
        }

        abstract Node createKid(TEntity entity);

        @Override PropertyList getMetadata() {
             return new PropertyList() {{
                add(int.class, "size");
            }};
        }

        class EntityCollectionKids extends Children.Keys<TEntity> {
            EntityCollectionNode<TEntity> parent;

            EntityCollectionKids(EntityCollectionNode<TEntity> parent) {
                this.parent = parent;
            }

            @Override protected void addNotify() {
                setKeys(this.parent.value.values());
            }

            @Override protected Node[] createNodes(TEntity entity) {
                return new Node[] { parent.createKid(entity) };
            }
        }
    }

    class IndexNode extends EntityNode<Index> {
        IndexNode(Index index) {
            super(index);
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

    class IndexesNode extends EntityCollectionNode<Index> {
        IndexesNode(EntityCollection<Index> indexes) {
            super("Indexes", indexes);
        }

        @Override Node createKid(Index index) {
            return new IndexNode(index);
        }
    }

    class JobNode extends EntityNode<Job> {
        JobNode(Job job) {
            super(job);
            setDisplayName(job.getTitle());
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

    class JobsNode extends EntityCollectionNode<Job> {
        JobsNode(EntityCollection<Job> jobs) {
            super("Jobs", jobs);
        }

        @Override Node createKid(Job job) {
            return new JobNode(job);
        }
    }

    class LicenseGroupNode extends EntityNode<LicenseGroup> {
        LicenseGroupNode(LicenseGroup licenseGroup) {
            super(licenseGroup);
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                // UNDONE: add(String[].class, "getStackIds");
                add(boolean.class, "isActive");
            }};
        }
    }

    class LicenseGroupsNode extends EntityCollectionNode<LicenseGroup> {
        LicenseGroupsNode(EntityCollection<LicenseGroup> licenseGroups) {
            super("License Groups", licenseGroups);
        }

        @Override Node createKid(LicenseGroup licenseGroup) {
            return new LicenseGroupNode(licenseGroup);
        }
    }

    class LicenseSlaveNode extends EntityNode<LicenseSlave> {
        LicenseSlaveNode(LicenseSlave licenseSlave) {
            super(licenseSlave);
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getLabel");
                // UNDONE: add(String[].class, "getPoolIds");
                // UNDONE: add(String[].class, "getStackIds");
            }};
        }
    }

    class LicenseSlavesNode extends EntityCollectionNode<LicenseSlave> {
        LicenseSlavesNode(EntityCollection<LicenseSlave> licenseSlaves) {
            super("License Slaves", licenseSlaves);
        }

        @Override Node createKid(LicenseSlave licenseSlave) {
            return new LicenseSlaveNode(licenseSlave);
        }
    }

    class LicenseStackNode extends EntityNode<LicenseStack> {
        LicenseStackNode(LicenseStack licenseStack) {
            super(licenseStack);
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getLabel");
                // UNDONE: add(String[].class, "getPoolIds");
                add(long.class, "getQuota");
                // UNDONE: add(String[].class, "getStackIds");
            }};
        }
    }

    class LicenseStacksNode extends EntityCollectionNode<LicenseStack> {
        LicenseStacksNode(EntityCollection<LicenseStack> licenseStacks) {
            super("License Stacks", licenseStacks);
        }

        @Override Node createKid(LicenseStack licenseStack) {
            return new LicenseStackNode(licenseStack);
        }
    }

    class LicenseNode extends ExplorerNode<License> {
        LicenseNode(License license) {
            super(license);
            String displayName = license.getLabel();
            if (displayName == null) displayName = license.getName();
            setDisplayName(displayName);
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(Date.class, "getCreationTime");
                add(Date.class, "getExpirationTime");
                // UNDONE: add(String[].class, "getFeatures");
                add(String.class, "getGroupId");
                add(String.class, "getLabel");
                add(String.class, "getLicenseHash");
                add(int.class, "getMaxViolations");
                add(long.class, "getQuota");
                // UNDONE: add(String[].class, "getSourceTypes");
                add(String.class, "getStackId");
                add(String.class, "getStatus");
                add(String.class, "getType");
                add(int.class, "getWindowPeriod");
            }};
        }
    }

    class LicensesNode extends EntityCollectionNode<License> {
        LicensesNode(EntityCollection<License> licenses) {
            super("Licenses", licenses);
        }

        @Override Node createKid(License license) {
            return new LicenseNode(license);
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

    class SavedSearchNode extends EntityNode<SavedSearch> {
        SavedSearchNode(SavedSearch savedSearch) {
            super(savedSearch);
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

    class SavedSearchesNode extends EntityCollectionNode<SavedSearch> {
        SavedSearchesNode(EntityCollection<SavedSearch> savedSearches) {
            super("Saved Searches", savedSearches);
        }

        @Override Node createKid(SavedSearch savedSearch) {
            return new SavedSearchNode(savedSearch);
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
                return new AppsNode(service.getApplications());
            if (kind.equals("indexes"))
                return new IndexesNode(service.getIndexes());
            if (kind.equals("jobs"))
                return new JobsNode(service.getJobs());
            if (kind.equals("licenses"))
                return new LicensesNode(service.getLicenses());
            if (kind.equals("licenseGroups"))
                return new LicenseGroupsNode(service.getLicenseGroups());
            if (kind.equals("licenseSlaves"))
                return new LicenseSlavesNode(service.getLicenseSlaves());
            if (kind.equals("licenseStacks"))
                return new LicenseStacksNode(service.getLicenseStacks());
            if (kind.equals("searches"))
                return new SavedSearchesNode(service.getSearches());
            if (kind.equals("settings"))
                return new SettingsNode(service.getSettings());
            if (kind.equals("users"))
                return new UsersNode(service.getUsers());
            return null;
        }

        @Override protected Node[] createNodes(String kind) {
            return new Node[] { createNode(kind) };
        }
    }

    class SettingsNode extends EntityNode<Settings> {
        SettingsNode(Settings settings) {
            super(settings);
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

    class UserNode extends EntityNode<User> {
        UserNode(User user) {
            super(user);
        }

        @Override protected PropertyList getMetadata() {
            return new PropertyList() {{
                add(String.class, "getDefaultApp");
                add(boolean.class, "getDefaultAppIsUserOverride");
                add(String.class, "getDefaultAppSourceRole");
                add(String.class, "getEmail");
                add(String.class, "getPassword");
                add(String.class, "getRealName");
                // UNDONE: add(String[].class, "getRoles")
            }};
        }
    }

    class UsersNode extends EntityCollectionNode<User> {
        UsersNode(EntityCollection<User> users) {
            super("Users", users);
        }

        @Override Node createKid(User user) {
            return new UserNode(user);
        }
    }
}
