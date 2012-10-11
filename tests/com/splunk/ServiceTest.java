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

package com.splunk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;


public class ServiceTest extends SplunkTestCase {
    final static String assertRoot = "Service assert: ";

    // Perform some non-intrusive inspection of the given Job object.
    void checkJob(Job job) {
        ready(job);
        job.getDelegate();
        job.getDiskUsage();
        job.getDispatchState();
        job.getDoneProgress();
        job.getDropCount();
        job.getEarliestTime();
        job.getEventAvailableCount();
        job.getEventCount();
        job.getEventFieldCount();
        job.getEventIsStreaming();
        job.getEventIsTruncated();
        job.getEventSearch();
        job.getEventSorting();
        job.getKeywords();
        job.getLabel();
        job.getLatestTime();
        job.getNumPreviews();
        job.getPriority();
        job.getRemoteSearch();
        job.getReportSearch();
        job.getResultCount();
        job.getResultIsStreaming();
        job.getResultPreviewCount();
        job.getRunDuration();
        job.getScanCount();
        job.getSearch();
        job.getSearchEarliestTime();
        job.getSearchLatestTime();
        job.getSid();
        job.getStatusBuckets();
        job.getTtl();
        job.isDone();
        job.isFailed();
        job.isFinalized();
        job.isPaused();
        job.isPreviewEnabled();
        job.isRealTimeSearch();
        job.isSaved();
        job.isSavedSearch();
        job.isZombie();
        assertEquals(assertRoot + "#1", job.getName(), job.getSid());
    }

    void checkResponse(ResponseMessage response) {
        assertEquals(assertRoot + "#2", 200, response.getStatus());
        try {
            // Make sure we can at least load the Atom response
            AtomFeed.parseStream(response.getContent());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test public void testCapabilities() throws Exception {
        Service service = connect();

        List<String> expected = Arrays.asList(
            "admin_all_objects", "change_authentication",
            "change_own_password", "delete_by_keyword",
            "edit_deployment_client", "edit_deployment_server",
            "edit_dist_peer", "edit_forwarders", "edit_httpauths",
            "edit_input_defaults", "edit_monitor", "edit_roles",
            "edit_scripted", "edit_search_server", "edit_server",
            "edit_splunktcp", "edit_splunktcp_ssl", "edit_tcp", "edit_udp",
            "edit_user", "edit_web_settings", "get_metadata",
            "get_typeahead", "indexes_edit", "license_edit", "license_tab",
            "list_deployment_client", "list_forwarders", "list_httpauths",
            "list_inputs", "request_remote_tok", "rest_apps_management",
            "rest_apps_view", "rest_properties_get", "rest_properties_set",
            "restart_splunkd", "rtsearch", "schedule_search", "search",
            "use_file_operator");

        String[] caps = service.getCapabilities();
        for (String name : expected)
            assertTrue(assertRoot + "#3", contains(caps, name));
    }

    // Make a few simple requests and make sure the results look ok.
    @Test public void testGet() {
        Service service = connect();

        // Check a few paths that we know exist
        String[] paths = { 
            "/", 
            "/services", 
            "/services/search/jobs",
            "search/jobs",
            "authentication/users"
        };

        for (String path : paths)
            checkResponse(service.get(path));

        // And make sure we get the expected 404
        try {
            service.get("/zippy");
            fail("Expected HttpException");
        }
        catch (HttpException e) {
            assertEquals(assertRoot + "#4", 404, e.getStatus());
        }
    }

    @Test public void testInfo() throws Exception {
        Service service = connect();

        List <String> expected = Arrays.asList(
            "build", "cpu_arch", "guid", "isFree", "isTrial", "licenseKeys",
            "licenseSignature", "licenseState", "master_guid", "mode",
            "os_build", "os_name", "os_version", "serverName", "version");

        ServiceInfo info = service.getInfo();
        for (String name : expected)
            assertTrue(assertRoot + "#5", info.containsKey(name));

        info.getBuild();
        info.getCpuArch();
        info.getGuid();
        info.getLicenseKeys();
        info.getLicenseLabels();
        info.getLicenseSignature();
        info.getLicenseState();
        info.getMasterGuid();
        info.getMode();
        info.getOsBuild();
        info.getOsName();
        info.getOsVersion();
        info.getServerName();
        info.getVersion();
        info.isFree();
        info.isRtSearchEnabled();
        info.isTrial();
    }

    @Test public void testLogin() {
        ResponseMessage response;

        Service service = new Service(
            command.host, 
            command.port, 
            command.scheme);

        // Not logged in, should fail with 401
        try {
            response = service.get("/services/authentication/users");
            fail("Expected HttpException");
        }
        catch (HttpException e) {
            assertEquals(assertRoot + "#6", 401, e.getStatus());
        }

        // Logged in, request should succeed
        service.login(command.username, command.password);
        response = service.get("/services/authentication/users");
        checkResponse(response);

        // Logout, the request should fail with a 401
        service.logout();
        try {
            response = service.get("/services/authentication/users");
            fail("Expected HttpException");
        }
        catch (HttpException e) {
            assertEquals(assertRoot + "#7", 401, e.getStatus());
        }
    }

    @Test public void testRestart() throws Exception {
        splunkRestart();
    }

    @Test public void testJobs() {
        Job job;

        Service service = connect();

        JobCollection jobs = service.getJobs();
        for (Entity entity : jobs.values())
            checkJob((Job)entity);

        job = jobs.create("search *");
        checkJob(job);
        job.cancel();
    }

    @Test public void testSettersAndGetters() {

        // The individual classes test most of the set/get methods,
        // but call out some specific cases here.

        Service service = connect();
        Settings settings = service.getSettings();
        String originalHost = settings.getHost();
        int originalMinSpace = settings.getMinFreeSpace();

        // make sure set updates state before getting.
        // entity.setMethod(value)
        // entity.getMethod() --> gets value.
        settings.setHost("sdk-host");
        assertEquals(assertRoot + "#8", "sdk-host", settings.getHost());

        // make sure posts arguments are merged
        // entity.setMethod(value)
        // entity.update(args.create("key2", value2))
        settings.setHost("sdk-host2");
        settings.update(
            new HashMap<String, Object>(Args.create("minFreeSpace", 500)));
        assertEquals(assertRoot + "#9", "sdk-host2", settings.getHost());
        assertEquals(assertRoot + "#10", 500, settings.getMinFreeSpace());

        // make sure live posts argument take precedents over setters
        // entity.setMethod(value)
        // entity.update(args.create("samekey", value2))
        settings.setMinimumFreeSpace(600);
        settings.update(
            new HashMap<String, Object>(Args.create("minFreeSpace", 700)));
        assertEquals(assertRoot + "#11", 700, settings.getMinFreeSpace());

        // Restore original
        settings.setHost(originalHost);
        settings.setMinimumFreeSpace(originalMinSpace);
        settings.update();
        assertEquals(assertRoot + "#12", settings.getMinFreeSpace(),
            originalMinSpace);
        assertEquals(assertRoot + "#13", settings.getHost(), originalHost);
    }

    @Test public void testUsers() {
        Args args;
        User user;

        Service service = connect();

        String username = "sdk-user";
        String password = "changeme";

        UserCollection users = service.getUsers();

        // Cleanup potential prior failed test run.
        users.remove(username);
        assertFalse(assertRoot + "#14", users.containsKey(username));

        // Create user using base create method
        args = new Args();
        args.put("password", password);
        args.put("roles", "power");
        user = users.create(username, args);
        assertTrue(assertRoot + "#15", users.containsKey(username));
        assertEquals(assertRoot + "#16", username, user.getName());
        assertEquals(assertRoot + "#17", 1, user.getRoles().length);
        assertTrue(assertRoot + "#18", contains(user.getRoles(), "power"));

        users.remove(username);
        assertFalse(assertRoot + "#19", users.containsKey(username));

        // Create user using derived create method 
        user = users.create(username, password, "power");
        assertTrue(assertRoot + "#21", users.containsKey(username));
        assertEquals(assertRoot + "#22", username, user.getName());
        assertEquals(assertRoot + "#23", 1, user.getRoles().length);
        assertTrue(assertRoot + "#24", contains(user.getRoles(), "power"));

        users.remove(username);
        assertFalse(assertRoot + "#25", users.containsKey(username));

        // Create using derived method with multiple roles
        user = users.create(
            username, password, new String[] { "power", "user" });
        assertTrue(assertRoot + "#26", users.containsKey(username));
        assertEquals(assertRoot + "#27", username, user.getName());
        assertEquals(assertRoot + "#28", 2, user.getRoles().length);
        assertTrue(assertRoot + "#29", contains(user.getRoles(), "power"));
        assertTrue(assertRoot + "#30", contains(user.getRoles(), "user"));

        users.remove(username);
        assertFalse(assertRoot + "#31", users.containsKey(username));

        // Create using drived method with multiple roles and extra properties
        args = new Args();
        args.put("realname", "Renzo");
        args.put("email", "email.me@now.com");
        args.put("defaultApp", "search");
        user = users.create(
            username, password, new String[] { "power", "user" }, args);
        assertTrue(assertRoot + "#32", users.containsKey(username));
        assertEquals(assertRoot + "#33", username, user.getName());
        assertEquals(assertRoot + "#34", 2, user.getRoles().length);
        assertTrue(assertRoot + "#35", contains(user.getRoles(), "power"));
        assertTrue(assertRoot + "#36", contains(user.getRoles(), "user"));
        assertEquals(assertRoot + "#37", "Renzo", user.getRealName());
        assertEquals(assertRoot + "#38", "email.me@now.com", user.getEmail());
        assertEquals(assertRoot + "#39", "search", user.getDefaultApp());
        user.getTz();

        // update user using setters
        user.setDefaultApp("search");
        user.setEmail("none@noway.com");
        user.setPassword("new-password");
        user.setRealName("SDK-name");
        if (service.versionCompare("4.3") >= 0) {
            user.setRestartBackgroundJobs(false);
        }
        user.setRoles("power");
        user.update();
        user.refresh();

        assertEquals(assertRoot + "#40", 1, user.getRoles().length);
        assertTrue(assertRoot + "#41", contains(user.getRoles(), "power"));
        assertEquals(assertRoot + "#42", "SDK-name", user.getRealName());
        assertEquals(assertRoot + "#43", "none@noway.com", user.getEmail());
        assertEquals(assertRoot + "#44", "search", user.getDefaultApp());

        users.remove(username);
        assertFalse(assertRoot + "#45", users.containsKey(username));

        assertFalse(assertRoot + "#46", users.containsKey("sdk-user"));
        if (users.containsKey("SDK-user"))
            users.remove("SDK-user");
        assertFalse(assertRoot + "#47", users.containsKey("SDK-user"));

        args = new Args();
        args.put("password", password);
        args.put("roles", "power");
        assertEquals(assertRoot + "#48",username,  user.getName());
        assertFalse(assertRoot + "#49", users.containsKey("SDK-user"));
        users.remove(username);
        assertFalse(assertRoot + "#50", users.containsKey(username));
    }
    
    @SuppressWarnings("deprecation")
    @Test public void testClassicServiceArgs() {
        ServiceArgs args = new ServiceArgs();
        args.app = "myapp";
        args.host = "myhost.splunk.com";
        args.owner = "myuser";
        args.port = 9999;
        args.scheme = "https";
        args.token = "Splunk MY_SESSION_KEY";
        
        Service service = new Service(args);
        assertEquals(args.app, service.getApp());
        assertEquals(args.host, service.getHost());
        assertEquals(args.owner, service.getOwner());
        assertEquals((int) args.port, (int) service.getPort());
        assertEquals(args.scheme, service.getScheme());
        assertEquals(args.token, service.getToken());
    }
    
    @SuppressWarnings("deprecation")
    @Test public void testNewServiceArgs() {
        ServiceArgs args = new ServiceArgs();
        args.setApp("myapp");
        args.setHost("myhost.splunk.com");
        args.setOwner("myuser");
        args.setPort(9999);
        args.setScheme("https");
        args.setToken("Splunk MY_SESSION_KEY");
        
        assertEquals("Arg setters didn't replicate value to deprecated fields.", args.app, "myapp");
        
        Service service = new Service(args);
        assertEquals(args.app, service.getApp());
        assertEquals(args.host, service.getHost());
        assertEquals(args.owner, service.getOwner());
        assertEquals((int) args.port, (int) service.getPort());
        assertEquals(args.scheme, service.getScheme());
        assertEquals(args.token, service.getToken());
    }
    
    @Test public void testNewServiceArgsAsMap() {
        ServiceArgs args = new ServiceArgs();
        args.put("app", "myapp");
        args.put("host", "myhost.splunk.com");
        args.put("owner", "myuser");
        args.put("port", 9999);
        args.put("scheme", "https");
        args.put("token", "Splunk MY_SESSION_KEY");
        
        Service service = new Service(args);
        assertEquals("myapp", service.getApp());
        assertEquals("myhost.splunk.com", service.getHost());
        assertEquals("myuser", service.getOwner());
        assertEquals(9999, (int) service.getPort());
        assertEquals("https", service.getScheme());
        assertEquals("Splunk MY_SESSION_KEY", service.getToken());
    }
    
    @Test public void testNewServiceArgsWithDefaults() {
        ServiceArgs args = new ServiceArgs();
        
        Service service = new Service(args);
        assertEquals(null, service.getApp());
        assertEquals("localhost", service.getHost());
        assertEquals(null, service.getOwner());
        assertEquals(8089, (int) service.getPort());
        assertEquals("https", service.getScheme());
        assertEquals(null, service.getToken());
    }
}

