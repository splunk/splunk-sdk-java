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

import com.splunk.*;
import com.splunk.sdk.Command;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Program {
    static void printActions(Map<String, String> actions) {
        if (actions == null) return;
        for (Map.Entry entry : actions.entrySet()) {
            System.out.format("action %s => %s\n", 
                entry.getKey(), entry.getValue());
        }
    }

    static void printConfig(EntityCollection<Entity> config) {
        System.out.format("\n## %s", config.getTitle());
        for (Entity stanza : config.values())
            printEntity(stanza);
    }

    static void printApplication(Application app) {
        printEntity(app);
        printField("CheckForUpdates", app.getCheckForUpdates());
        printField("Label", app.getLabel());
        printField("Version", app.getVersion());
        printField("Configured", app.isConfigured());
        printField("Manageable", app.isManageable());
        printField("Visible", app.isVisible());
    }

    static void printDistributedConfiguration(DistributedConfiguration config) {
        printEntity(config);
        printField("AutoAddServers", config.getAutoAddServers());
        printField("BlacklistNames", config.getBlacklistNames());
        printField("BlacklistUrls", config.getBlacklistUrls());
        printField("CheckTimedOutServersFrequency", config.getCheckTimedOutServersFrequency());
        printField("HeartbeatFrequency", config.getHeartbeatFrequency());
        printField("HeartbeatMcastAddress", config.getHeartbeatMcastAddress());
        printField("HeartbeatPort", config.getHeartbeatPort());
        printField("RemovedTimedOutServers", config.getRemovedTimedOutServers());
        printField("ServerTimeout", config.getServerTimeout());
        printField("Servers", config.getServers());
        printField("ShareBundles", config.getShareBundles());
        printField("SkipOurselves", config.getSkipOurselves());
        printField("StatusTimeout", config.getStatusTimeout());
        printField("Ttl", config.getTtl());
        printField("isDisabled", config.isDisabled());
    }

    static void printDistributedPeer(DistributedPeer peer) {
        printEntity(peer);
        printField("BundleVersions", peer.getBundleVersions());
        printField("Guid", peer.getGuid());
        printField("LicenseSignature", peer.getLicenseSignature());
        printField("PeerName", peer.getPeerName());
        printField("PeerType", peer.getPeerType());
        printField("ReplicationStatus", peer.getReplicationStatus());
        printField("Status", peer.getStatus());
        printField("Version", peer.getVersion());
        printField("isDisabled", peer.isDisabled());
        printField("isHttps", peer.isHttps());
    }

    static void printEntity(Entity entity) {
        System.out.println("");
        if (entity == null) {
            System.out.println("null");
            return;
        }
        printResource(entity);
        printActions(entity.getActions());
        Map<String, Object> content = entity.getContent();
        if (content != null) {
            for (Map.Entry entry : content.entrySet()) {
                System.out.format("%s = %s\n",
                    entry.getKey(), entry.getValue().toString());
            }
        }
    }

    static <T extends Entity> void 
    printEntities(EntityCollection<T> entities) {
    	System.out.format("\n# %s\n", entities.getPath());
        System.out.format("path = %s\n", entities.getPath());
        printActions(entities.getActions());
        System.out.format("keys = %s\n", entities.keySet().toString());
        for (T entity : entities.values()) 
            printEntity(entity);
    }

    static void printEventType(EventType eventType) {
        printEntity(eventType);
        printField("Description", eventType.getDescription());
        printField("Priority", eventType.getPriority());
        printField("Search", eventType.getSearch());
    }

    static void printField(String field, boolean value) {
        System.out.format("%s = %b\n", field, value);
    }

    static void printField(String field, float value) {
        System.out.format("%s = %f\n", field, value);
    }

    static void printField(String field, Date value) {
        System.out.format("%s = %s\n", 
            field, value == null ? "null" : value.toString());
    }

    static void printField(String field, int value) {
        System.out.format("%s = %d\n", field, value);
    }

    static void printField(String field, String value) {
        System.out.format("%s = %s\n", field, value == null ? "null" : value);
    }

    static void printField(String field, String[] value) {
        printField(field, value == null 
            ? (String)null : Arrays.toString(value));
    }

    static void printField(String field, List<String> value) {
        printField(field, value == null ? (String)null : value.toString());
    }

    static void printIndex(Index index) {
        printEntity(index);
        printField("AssureUTF8", index.getAssureUTF8());
        printField("BlockSignSize", index.getBlockSignSize());
        printField("BlockSignatureDatabase", index.getBlockSignatureDatabase());
        printField("ColdPath", index.getColdPath());
        printField("ColdPathExpanded", index.getColdPathExpanded());
        printField("ColdToFrozenDir", index.getColdToFrozenDir());
        printField("ColdToFrozenScript", index.getColdToFrozenScript());
        printField("CompressRawdata", index.getCompressRawdata());
        printField("CurrentDBSizeMB", index.getCurrentDBSizeMB());
        printField("DefaultDatabase", index.getDefaultDatabase());
        printField("EnableRealtimeSearch", index.getEnableRealtimeSearch());
        printField("FrozenTimePeriodInSecs", index.getFrozenTimePeriodInSecs());
        printField("HomePath", index.getHomePath());
        printField("HomePathExpanded", index.getHomePathExpanded());
        printField("IndexThreads", index.getIndexThreads());
        printField("LastInitTime", index.getLastInitTime());
        printField("MaxConcurrentOptimizes", index.getMaxConcurrentOptimizes());
        printField("MaxDataSize", index.getMaxDataSize());
        printField("MaxHotBuckets", index.getMaxHotBuckets());
        printField("MaxHotIdleSecs", index.getMaxHotIdleSecs());
        printField("MaxHotSpanSecs", index.getMaxHotSpanSecs());
        printField("MaxMemMB", index.getMaxMemMB());
        printField("MaxMetaEntries", index.getMaxMetaEntries());
        printField("MaxRunningProcessGroups", index.getMaxRunningProcessGroups());
        printField("MaxTime", index.getMaxTime());
        printField("MaxTotalDataSizeMB", index.getMaxTotalDataSizeMB());
        printField("MaxWarmDBCount", index.getMaxWarmDBCount());
        printField("MemPoolMB", index.getMemPoolMB());
        printField("MinRawFileSyncSecs", index.getMinRawFileSyncSecs());
        printField("MinTime", index.getMinTime());
        printField("PartialServiceMetaPeriod", index.getPartialServiceMetaPeriod());
        printField("QuarantineFutureSecs", index.getQuarantineFutureSecs());
        printField("QuarantimePastSecs", index.getQuarantinePastSecs());
        printField("RawChunkSizeBytes", index.getRawChunkSizeBytes());
        printField("RotatePeriodInSecs", index.getRotatePeriodInSecs());
        printField("ServiceMetaPeriod", index.getServiceMetaPeriod());
        printField("SuppressBannerList", index.getSuppressBannerList());
        printField("Sync", index.getSync());
        printField("SyncMeta", index.getSyncMeta());
        printField("ThawedPath", index.getThawedPath());
        printField("ThawedPathExpanded", index.getThawedPathExpanded());
        printField("ThrottleCheckPeriod", index.getThrottleCheckPeriod());
        printField("TotalEventCount", index.getTotalEventCount());
        printField("isDisabled", index.isDisabled());
        printField("isInternal", index.isInternal());
    }

    static void printLicense(License license) {
        printEntity(license);
        printField("CreationTime", license.getCreationTime());
        printField("ExpirationTime", license.getExpirationTime());
        printField("Features", license.getFeatures());
        printField("GroupId", license.getGroupId());
        printField("Label", license.getLabel());
        printField("LicenseHash", license.getLicenseHash());
        printField("MaxViolations", license.getMaxViolations());
        printField("Quota", license.getQuota());
        printField("SourceTypes", license.getSourceTypes());
        printField("StackId", license.getStackId());
        printField("Status", license.getStatus());
        printField("Type", license.getType());
        printField("WindowPeriod", license.getWindowPeriod());
    }

    static void printLicenseGroup(LicenseGroup licenseGroup) {
        printEntity(licenseGroup);
        printField("StackIds", licenseGroup.getStackIds());
        printField("isActive", licenseGroup.isActive());
    } 

    static void printLicensePool(LicensePool licensePool) {
        printEntity(licensePool);
        printField("Description", licensePool.getDescription());
        printField("Quota", licensePool.getQuota());
        printField("Slaves", licensePool.getSlaves());
        printField("SlavesUsageBytes", licensePool.getSlavesUsageBytes());
        printField("StackId", licensePool.getStackId());
        printField("UsedBytes", licensePool.getUsedBytes());
    }

    static void printLicenseSlave(LicenseSlave licenseSlave) {
        printEntity(licenseSlave);
        printField("Label", licenseSlave.getLabel());
        printField("PoolIds", licenseSlave.getPoolIds());
        printField("StackIds", licenseSlave.getStackIds());
    }

    static void printLicenseStack(LicenseStack licenseStack) {
        printEntity(licenseStack);
        printField("Label", licenseStack.getLabel());
        printField("Quota", licenseStack.getQuota());
        printField("Type", licenseStack.getType());
    }

    static void printUser(User user) {
        printEntity(user);
        printField("DefaultApp", user.getDefaultApp());
        printField("DefaultAppIsUserOverride", user.getDefaultAppIsUserOverride());
        printField("DefaultAppSourceRole", user.getDefaultAppSourceRole());
        printField("Email", user.getEmail());
        printField("Password", user.getPassword());
        printField("RealName", user.getRealName());
        printField("Roles", user.getRoles());
    }

    static void printJob(Job job) {
        printEntity(job);
        printField("CursorTime", job.getCursorTime());
        printField("delegate", job.getDelegate());
        printField("DiskUsage", job.getDiskUsage());
        printField("DispatchState", job.getDispatchState());
        printField("DoneProgress", job.getDoneProgress());
        printField("DropCount", job.getDropCount());
        printField("EarliestTime", job.getEarliestTime());
        printField("EventAvailableCount", job.getEventAvailableCount());
        printField("EventCount", job.getEventCount());
        printField("EventFieldCount", job.getEventFieldCount());
        printField("EventIsStreaming", job.getEventIsStreaming());
        printField("EventIsTruncated", job.getEventIsTruncated());
        printField("EventSearch", job.getEventSearch());
        printField("EventSorting", job.getEventSorting());
        printField("Keywords", job.getKeywords());
        printField("Label", job.getLabel());
        printField("LatestTime", job.getLatestTime());
        printField("NumPreviews", job.getNumPreviews());
        printField("Priority", job.getPriority());
        printField("RemoteSearch", job.getRemoteSearch());
        printField("ReportSearch", job.getReportSearch());
        printField("ResultCount", job.getResultCount());
        printField("ResultIsStreaming", job.getResultIsStreaming());
        printField("ResultPreviewCount", job.getResultPreviewCount());
        printField("RunDuration", job.getRunDuration());
        printField("ScanCount", job.getScanCount());
        printField("Search", job.getSearch());
        printField("SearchEarliestTime", job.getSearchEarliestTime());
        printField("SearchLatestTime", job.getSearchLatestTime());
        printField("Sid", job.getSid());
        printField("StatusBuckets", job.getStatusBuckets());
        printField("Ttl", job.getTtl());
        printField("IsDone", job.isDone());
        printField("IsFailed", job.isFailed());
        printField("IsPaused", job.isPaused());
        printField("IsPreviewEnabled", job.isPreviewEnabled());
        printField("IsRealTimeSearch", job.isRealTimeSearch());
        printField("IsRemoteTimeline", job.isRemoteTimeline());
        printField("IsSaved", job.isSaved());
        printField("IsSavedSearch", job.isSavedSearch());
        printField("IsZombie", job.isZombie());
    }

    static void printMessage(Message message) {
        printEntity(message);
        printField("Key", message.getKey());
        printField("Value", message.getValue());
    }

    static void printOutputDefault(OutputDefault outputDefault) {
        printEntity(outputDefault);
        printField("autoLb", outputDefault.autoLb());
        printField("blockOnCloning", outputDefault.blockOnCloning());
        printField("blockOnQueueFull", outputDefault.blockOnQueueFull());
        printField("getAutoLbFrequency", outputDefault.getAutoLbFrequency());
        printField("getConnectionTimeout", outputDefault.getConnectionTimeout());
        printField("getDefaultGroup", outputDefault.getDefaultGroup());
        printField("getDropClonedEventsOnQueueFull", outputDefault.getDropClonedEventsOnQueueFull());
        printField("getDropEventsOnQueueFull", outputDefault.getDropEventsOnQueueFull());
        printField("getForwardedIndex0Whitelist", outputDefault.getForwardedIndex0Whitelist());
        printField("getForwardedIndex1Blacklist", outputDefault.getForwardedIndex1Blacklist());
        printField("getForwardedIndex2Whitelist", outputDefault.getForwardedIndex2Whitelist());
        printField("getHeartbeatFrequency", outputDefault.getHeartbeatFrequency());
        printField("getMaxConnectionsPerIndexer", outputDefault.getMaxConnectionsPerIndexer());
        printField("getMaxFailuresPerInterval", outputDefault.getMaxFailuresPerInterval());
        printField("getMaxQueueSize", outputDefault.getMaxQueueSize());
        printField("getReadTimeout", outputDefault.getReadTimeout());
        printField("getSecsInFailureInterval", outputDefault.getSecsInFailureInterval());
        printField("getWriteTimeout", outputDefault.getWriteTimeout());
        printField("indexAndForward", outputDefault.indexAndForward());
        printField("isCompressed", outputDefault.isCompressed());
        printField("isDisabled", outputDefault.isDisabled());
        printField("isForwardedIndexFilterDisable", outputDefault.isForwardedIndexFilterDisable());
        printField("isIndexAndForward", outputDefault.isIndexAndForward());
        printField("sendCookedData", outputDefault.sendCookedData());
        printField("useAck", outputDefault.useAck());
    }

    static void printOutputGroup(OutputGroup outputGroup) {
        printEntity(outputGroup);
        printField("Method", outputGroup.getMethod());
        printField("Servers", outputGroup.getServers());
        printField("isDisabled", outputGroup.isDisabled());
    }

    static void printOutputServer(OutputServer outputServer) {
        printEntity(outputServer);
    }

    static void printOutputSyslog(OutputSyslog outputSyslog) {
        printEntity(outputSyslog);
    }

    static void printResource(Resource resource) {
        System.out.format("## %s\n", resource.getName());
        System.out.format("title = %s\n", resource.getTitle());
        System.out.format("path = %s\n", resource.getPath());
    }

    static <T extends Resource> void 
    printResources(ResourceCollection<T> resources) {
    	System.out.format("\n# %s\n", resources.getPath());
        System.out.format("path = %s\n", resources.getPath());
        printActions(resources.getActions());
        System.out.format("keys = %s\n", resources.keySet());
        for (T resource : resources.values()) 
            printResource(resource);
    }

    static void printRole(Role role) {
        printEntity(role);
        printField("Capabilities", role.getCapabilities());
        printField("DefaultApp", role.getDefaultApp());
        printField("ImportedCapabilities", role.getImportedCapabilities());
        printField("ImportedRoles", role.getImportedRoles());
        printField("ImportedRtSearchJobsQuota", role.getImportedRtSearchJobsQuota());
        printField("ImportedSearchDiskQuota", role.getImportedSearchDiskQuota());
        printField("ImportedSearchFilter", role.getImportedSearchFilter());
        printField("ImportedIndexesAllowed", role.getImportedIndexesAllowed());
        printField("ImportedIndexesDefault", role.getImportedIndexesDefault());
        printField("ImportedSearchJobsQuota", role.getImportedSearchJobsQuota());
        printField("RtSearchJobsQuota", role.getRtSearchJobsQuota());
        printField("SearchDiskQuota", role.getSearchDiskQuota());
        printField("SearchFilter", role.getSearchFilter());
        printField("SearchIndexesAllowed", role.getSearchIndexesAllowed());
        printField("SearchIndexesDefault", role.getSearchIndexesDefault());
        printField("SearchJobsQuota", role.getSearchJobsQuota());
        printField("SearchTimeWin", role.getSearchTimeWin());
    }

    static void printSavedSearch(SavedSearch search) {
        printEntity(search);
        printField("ActionEmailSendResults", search.getActionEmailSendResults());
        printField("ActionEmailTo", search.getActionEmailTo());
        printField("AlertExpires", search.getAlertExpires());
        printField("AlertSevertiy", search.getAlertSeverity());
        printField("AlertSuppresss", search.getAlertSuppress());
        printField("AlertSuppressPriod", search.getAlertSuppressPeriod());
        printField("AlertTrack", search.getAlertTrack());
        printField("AlertComparator", search.getAlertComparator());
        printField("AlertCondition", search.getAlertCondition());
        printField("AlertThreshold", search.getAlertThreshold());
        printField("AlertType", search.getAlertType());
        printField("CronSchedule", search.getCronSchedule());
        printField("Description", search.getDescription());
        printField("DispatchBuckets", search.getDispatchBuckets());
        printField("DispatchEarliestTime", search.getDispatchEarliestTime());
        printField("DispatchLatestTime", search.getDispatchLatestTime());
        printField("DispatchLookups", search.getDispatchLookups());
        printField("DispatchMaxCount", search.getDispatchMaxCount());
        printField("DispatchMaxTime", search.getDispatchMaxTime());
        printField("DispatchReduceFreq", search.getDispatchReduceFreq());
        printField("DispatchSpawnProcess", search.getDispatchSpawnProcess());
        printField("DispatchTimeFormat", search.getDispatchTimeFormat());
        printField("DispatchTtl", search.getDispatchTtl());
        printField("DisplayView", search.getDisplayView());
        printField("MaxConcurrent", search.getMaxConcurrent());
        printField("NextScheduledTime", search.getNextScheduledTime());
        printField("QualifiedSearch", search.getQualifiedSearch());
        printField("RealtimeSchedule", search.getRealtimeSchedule());
        printField("RequestUiDispatchApp", search.getRequestUiDispatchApp());
        printField("RequestUiDispatchView", search.getRequestUiDispatchView());
        printField("RestartOnSearchPeerAdd", search.getRestartOnSearchPeerAdd());
        printField("RunOnStartup", search.getRunOnStartup());
        printField("Search", search.getSearch());
        printField("Vsid", search.getVsid());
        printField("isActionEmail", search.isActionEmail());
        printField("isActionPopulateLookup", search.isActionPopulateLookup());
        printField("isActionRss", search.isActionRss());
        printField("isActionScript", search.isActioncScript());
        printField("isActionSummaryIndex", search.isActionSummaryIndex());
        printField("isDigestMode", search.isDigestMode());
        printField("isDisabled", search.isDisabled());
        printField("isScheduled", search.isScheduled());
        printField("isVisible", search.isVisible());
    }

    static void printServiceInfo(ServiceInfo info) {
        printEntity(info);
        printField("Build", info.getBuild());
        printField("CpuArch", info.getCpuArch());
        printField("Guid", info.getGuid());
        printField("isFree", info.isFree());
        printField("isTrial", info.isTrial());
        printField("LicenseKeys", info.getLicenseKeys());
        printField("LicenseSignature", info.getLicenseSignature());
        printField("LicenseState", info.getLicenseState());
        printField("MasterGuid", info.getMasterGuid());
        printField("Mode", info.getMode());
        printField("OsBuild", info.getOsBuild());
        printField("OsVersion", info.getOsVersion());
        printField("ServerName", info.getServerName());
        printField("Version", info.getVersion());
    }

    static void printSettings(Settings settings) {
        printEntity(settings);
        printField("SplunkDB", settings.getSplunkDB());
        printField("SplunkHome", settings.getSplunkHome());
        printField("EnableSplunkWebSSL", settings.getEnableSplunkWebSSL());
        printField("Host", settings.getHost());
        printField("HttpPort", settings.getHttpPort());
        printField("MgmtPort", settings.getMgmtPort());
        printField("Pass4SymmKey", settings.getPass4SymmKey());
        printField("ServerName", settings.getServerName());
        printField("SessionTimeout", settings.getSessionTimeout());
        printField("StartWebServer", settings.getStartWebServer());
        printField("TrustedIP", settings.getTrustedIP());
    }

    public static void main(String[] args) {
        Command command = Command.splunk("loaded").parse(args);
        Service service = Service.connect(command.opts);

        System.out.print("\n# Info");
        printServiceInfo(service.getInfo());

        System.out.print("\n# Settings");
        printSettings(service.getSettings());

        System.out.print("\n# Applications");
        for (Application app : service.getApplications().values())
            printApplication(app);

        System.out.print("\n# Configs");
        for (EntityCollection<Entity> config : service.getConfs().values())
            printConfig(config);

        System.out.print("\n# Capabilities\n");
        for (String capability : service.getCapabilities())
            System.out.println(capability);

        System.out.print("\n# DeploymentClient");
        printEntity(service.getDeploymentClient());

        System.out.print("\n# DeploymentServers");
        printEntities(service.getDeploymentServers());

        System.out.print("\n# DeploymentServerClasses");
        printEntities(service.getDeploymentServerClasses());

        System.out.print("\n# DeploymentTenants");
        printEntities(service.getDeploymentTenants());

        System.out.print("\n# DistributedConfiguration");
        printDistributedConfiguration(service.getDistributedConfiguration());

        System.out.print("\n# DistributedPeers");
        for (DistributedPeer peer : service.getDistributedPeers().values())
            printDistributedPeer(peer);

        System.out.print("\n# EventTypes");
        for (EventType eventType : service.getEventTypes().values())
            printEventType(eventType);

        System.out.print("\n# Indexes");
        for (Index index : service.getIndexes().values())
            printIndex(index);

        System.out.print("\n# Jobs");
        for (Job job : service.getJobs().values())
            printJob(job);

        System.out.print("\n# LicenseGroups");
        for (LicenseGroup licenseGroup : service.getLicenseGroups().values())
            printLicenseGroup(licenseGroup);

        System.out.print("\n# LicenseMessages");
        printEntities(service.getLicenseMessages());

        System.out.print("\n# LicensePools");
        for (LicensePool licensePool : service.getLicensePools().values())
            printLicensePool(licensePool);

        System.out.print("\n# LicenseSlaves");
        for (LicenseSlave licenseSlave : service.getLicenseSlaves().values())
            printLicenseSlave(licenseSlave);

        System.out.print("\n# LicenseStacks");
        for (LicenseStack licenseStack : service.getLicenseStacks().values())
            printLicenseStack(licenseStack);

        System.out.print("\n# Licenses");
        for (License license : service.getLicenses().values())
            printLicense(license);

        System.out.print("\n# Loggers");
        printEntities(service.getLoggers());

        System.out.print("\n# Messages");
        for (Message message : service.getMessages().values())
            printMessage(message);

        System.out.print("\n# OutputDefault");
        printOutputDefault(service.getOutputDefault());

        System.out.print("\n# OutputGroups");
        for (OutputGroup outputGroup : service.getOutputGroups().values())
            printOutputGroup(outputGroup);

        System.out.print("\n# OutputServers");
        for (OutputServer outputServer : service.getOutputServers().values())
            printOutputServer(outputServer);

        System.out.print("\n# OutputSyslogs");
        for (OutputSyslog outputSyslog : service.getOutputSyslogs().values())
            printOutputSyslog(outputSyslog);

        System.out.print("\n# Passwords");
        printEntities(service.getPasswords());

        System.out.print("\n# Roles");
        for (Role role : service.getRoles().values())
            printRole(role);

        System.out.print("\n# Saved Searches");
        for (SavedSearch search : service.getSearches().values())
            printSavedSearch(search);

        System.out.print("\n# Users");
        for (User user : service.getUsers().values())
            printUser(user);
    }
}

