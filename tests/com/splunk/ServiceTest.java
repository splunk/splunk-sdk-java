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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceTest extends SDKTestCase {
    private static final String QUERY = "search index=_internal | head 10";
    
    @Test
    public void testCapabilities() throws Exception {
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
        for (String name : expected) {
            assertTrue(contains(caps, name));
        }
    }

    // Make a few simple requests and make sure the results look ok.
    @Test
    public void testReceiver() {
        Receiver receiver = service.getReceiver();

        final String indexName = service.getIndexes().get("_internal").getDefaultDatabase();
        final Index index = service.getIndexes().get(indexName);
        final int originalEventCount = index.getTotalEventCount();

        try {
            Socket socket1 = receiver.attach();
            OutputStream stream = socket1.getOutputStream();

            String s = createTimestamp() + " Boris the mad baboon1!\r\n";
            stream.write(s.getBytes("UTF8"));
            // Splunk won't deterministically index these events until the socket is closed or greater than 1MB
            // has been written.
            stream.close();
            socket1.close();
        } catch (IOException e) {
            fail("Exception on attach");
        }

        try {
            Socket socket1 = receiver.attach(Args.create("sourcetype", "mysourcetype"));
            OutputStream stream = socket1.getOutputStream();

            String s = createTimestamp() + " Boris the mad baboon2!\r\n";
            stream.write(s.getBytes("UTF8"));
            // Splunk won't deterministically index these events until the socket is closed or greater than 1MB
            // has been written.
            stream.close();
            socket1.close();
        } catch (IOException e) {
            fail("Exception on attach");
        }

        receiver.submit("Boris the mad baboon3!\r\n");
        receiver.submit(Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon4!\r\n");
        receiver.log("Boris the mad baboon5!\r\n");
        receiver.log("main", "Boris the mad baboon6!\r\n");
        receiver.log(Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon7!\r\n");
        receiver.log(indexName, Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon8!\r\n");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 200;
            }

            @Override
            public boolean predicate() {
                index.refresh();
                int eventCount = index.getTotalEventCount();
                // WORKAROUND (SPL-75109): Splunk 6.0 on Windows doesn't record events submitted to the streaming
                // HTTP input without a sourcetype.
                if (service.versionCompare("6.0.0") == 0 && service.getInfo().getOsName().equals("Windows")) {
                    return eventCount == originalEventCount + 7;
                } else {
                    return eventCount == originalEventCount + 8;
                }
            }
        });
    }

    // Make a few simple requests and make sure the results look ok.
    @Test
    public void testGet() {
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
            assertEquals(404, e.getStatus());
        }
    }

    @Test
    public void testInfo() throws Exception {
        List <String> expected = Arrays.asList(
            "build", "cpu_arch", "guid", "isFree", "isTrial", "licenseKeys",
            "licenseSignature", "licenseState", "master_guid", "mode",
            "os_build", "os_name", "os_version", "serverName", "version");

        ServiceInfo info = service.getInfo();
        for (String name : expected)
            assertTrue(info.containsKey(name));

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
        
        assertEquals(info.getService(), service);
    }

    private void checkLoggedIn(Service service) {
        ResponseMessage response;
        response = service.get("/services/authentication/users");
        checkResponse(response);
    }
    
    protected void checkNotLoggedIn(Service service) {
        try {
            service.get("/services/authentication/users");
            fail("Expected HttpException");
        }
        catch (HttpException e) {
            assertEquals(401, e.getStatus());
        }
    }
    
    @Test
    public void testLogin() {
        Service service = new Service(
                (String) command.opts.get("host"),
                (Integer) command.opts.get("port"),
                (String) command.opts.get("scheme"));

        // Not logged in, should fail with 401
        checkNotLoggedIn(service);

        // Logged in, request should succeed
        service.login(
                (String) command.opts.get("username"),
                (String) command.opts.get("password"));
        checkLoggedIn(service);

        // Logout, the request should fail with a 401
        service.logout();
        checkNotLoggedIn(service);
    }
    
    @Test
    public void testLoginWithoutArguments() {
        ServiceArgs args = new ServiceArgs();
        args.setHost((String) command.opts.get("host"));
        args.setPort((Integer) command.opts.get("port"));
        args.setScheme((String) command.opts.get("scheme"));
        args.setUsername((String) command.opts.get("username"));
        args.setPassword((String) command.opts.get("password"));
        Service service = new Service(args);
        
        checkNotLoggedIn(service);
        
        service.login();
        checkLoggedIn(service);
        
        service.logout();
        checkNotLoggedIn(service);
    }
    
    @Test
    public void testLoginWithArgumentsOverridesServiceArgs() {
        ServiceArgs args = new ServiceArgs();
        args.setHost((String) command.opts.get("host"));
        args.setPort((Integer) command.opts.get("port"));
        args.setScheme((String) command.opts.get("scheme"));
        args.setUsername("I can't possibly be a user");
        args.setPassword("This password is nonsense.");
        Service service = new Service(args);
        
        checkNotLoggedIn(service);
        
        service.login(
            (String) command.opts.get("username"),
            (String) command.opts.get("password")
        );
        checkLoggedIn(service);
        
        service.logout();
        checkNotLoggedIn(service);
    }
    
    @Test
    public void testLoginWithoutAnyUsernameFails() {
        ServiceArgs args = new ServiceArgs();
        args.setHost((String) command.opts.get("host"));
        args.setPort((Integer) command.opts.get("port"));
        args.setScheme((String) command.opts.get("scheme"));
        Service service = new Service(args);
        
        try {
            service.login();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e) {}
    }
    
    @Test
    public void testLoginWithToken() {
        String validToken = service.getToken();
        
        assertTrue(validToken.startsWith("Splunk "));
        
        Service s = new Service(service.getHost(), service.getPort());
        s.setToken(validToken);
        
        // Ensure we can perform some action.
        // In particular we don't expect an unauthenticated error.
        s.getSettings().refresh();
        
        // Make sure we're still using the same token.
        // In particular we don't want to trigger auto-login functionality
        // that gets a new token.
        assertEquals(s.getToken(), validToken);
    }
    
    @Test
    public void testLoginGetters() {
        Service s = new Service("theHost");
        try {
            s.login("theUser", "thePassword");
        } catch (Exception e) {
            // Don't care if login fails. It probably will.
        }
        
        assertEquals("theUser", s.getUsername());
        assertEquals("thePassword", s.getPassword());
    }

    @Test
    public void testJobs() throws InterruptedException {
        JobCollection jobs = service.getJobs();
        for (Job entity : jobs.values())
            testGetters(entity);

        Job job = jobs.create("search * | head 1");

        while (!job.isDone()) {
            Thread.sleep(150);
        }

        testGetters(job);
        job.cancel();
    }
        
    // Perform some non-intrusive inspection of the given Job object.
    private void testGetters(Job job) {
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
        assertEquals(job.getName(), job.getSid());
    }

    @Test
    public void testSettersAndGettersPattern() {
        Settings settings = service.getSettings();
        
        // Save
        String originalHost = settings.getHost();
        int originalMinSpace = settings.getMinFreeSpace();

        // Ensure setter updates local state
        settings.setHost("sdk-host");
        assertEquals("sdk-host", settings.getHost());

        // Ensure update() merges arguments with local state
        settings.setHost("sdk-host2");
        settings.update(Args.create("minFreeSpace", 500));
        assertEquals("sdk-host2", settings.getHost());
        assertEquals(500, settings.getMinFreeSpace());

        // Ensure update argument takes precedence over local state
        settings.setMinimumFreeSpace(600);
        settings.update(Args.create("minFreeSpace", 700));
        assertEquals(700, settings.getMinFreeSpace());

        // Restore
        {
            settings.setHost(originalHost);
            settings.setMinimumFreeSpace(originalMinSpace);
            settings.update();
            
            assertEquals(settings.getMinFreeSpace(),
                originalMinSpace);
            assertEquals(settings.getHost(), originalHost);
        }
        
        // Twiddling the host value makes Splunk want to restart.
        // No actual instability results, though.
        clearRestartMessage();
    }

    @Test
    public void testUsers() {
        Args args;
        User user;

        String username = "sdk-user";
        String password = "changeme";

        UserCollection users = service.getUsers();

        // Cleanup potential prior failed test run.
        users.remove(username);
        assertFalse(users.containsKey(username));

        // Create user using base create method
        {
            args = new Args();
            args.put("password", password);
            args.put("roles", "power");
            user = users.create(username, args);
            
            assertTrue(users.containsKey(username));
            assertEquals(username, user.getName());
            assertEquals(1, user.getRoles().length);
            assertTrue(contains(user.getRoles(), "power"));
            
            users.remove(username);
            assertFalse(users.containsKey(username));
        }

        // Create user using derived create method 
        {
            user = users.create(username, password, "power");
            
            assertTrue(users.containsKey(username));
            assertEquals(username, user.getName());
            assertEquals(1, user.getRoles().length);
            assertTrue(contains(user.getRoles(), "power"));
    
            users.remove(username);
            assertFalse(users.containsKey(username));
        }

        // Create using derived method with multiple roles
        {
            user = users.create(
                username, password, new String[] { "power", "user" });
            
            assertTrue(users.containsKey(username));
            assertEquals(username, user.getName());
            assertEquals(2, user.getRoles().length);
            assertTrue(contains(user.getRoles(), "power"));
            assertTrue(contains(user.getRoles(), "user"));
    
            users.remove(username);
            assertFalse(users.containsKey(username));
        }

        // Create using derived method with multiple roles and extra properties
        {
            args = new Args();
            args.put("realname", "Renzo");
            args.put("email", "email.me@now.com");
            args.put("defaultApp", "search");
            user = users.create(
                username, password, new String[] { "power", "user" }, args);
            
            assertTrue(users.containsKey(username));
            assertEquals(username, user.getName());
            assertEquals(2, user.getRoles().length);
            assertTrue(contains(user.getRoles(), "power"));
            assertTrue(contains(user.getRoles(), "user"));
            assertEquals("Renzo", user.getRealName());
            assertEquals("email.me@now.com", user.getEmail());
            assertEquals("search", user.getDefaultApp());
            assertNotNull(user.getPassword());
            user.getDefaultAppIsUserOverride();
            assertNotNull(user.getDefaultAppSourceRole());
            assertNotNull(user.getType());
    
            // Probe
            {
                String tz = user.getTz();
                
                user.setDefaultApp("search");
                user.setEmail("none@noway.com");
                user.setPassword("new-password");
                user.setRealName("SDK-name");
                if (service.versionCompare("4.3") >= 0) {
                    user.setRestartBackgroundJobs(false);
                    user.setTz("Pacific/Midway");
                }
                user.setRoles("power");
                user.update();
                user.refresh();
        
                assertEquals("search", user.getDefaultApp());
                assertEquals("none@noway.com", user.getEmail());
                assertEquals("SDK-name", user.getRealName());
                assertEquals(1, user.getRoles().length);
                if (service.versionIsAtLeast("4.3")) {
                    assertEquals("Pacific/Midway", user.getTz());
                }
                assertTrue(contains(user.getRoles(), "power"));
                
                if (service.versionIsAtLeast("4.3")) {
                    user.setTz(tz == null ? "" : tz);
                }
                user.setRoles(new String[] {"power"});
                user.update();
                user.refresh();
                assertTrue(contains(user.getRoles(), "power"));
            }
    
            users.remove(username);
            assertFalse(users.containsKey(username));
        }
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testClassicServiceArgs() {
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
    @Test
    public void testNewServiceArgs() {
        ServiceArgs args = new ServiceArgs();
        args.setApp("myapp");
        args.setHost("myhost.splunk.com");
        args.setOwner("myuser");
        args.setPort(9999);
        args.setScheme("https");
        args.setToken("Splunk MY_SESSION_KEY");
        
        assertEquals("Arg setters didn't replicate value to deprecated fields.",
            args.app, "myapp");
        
        Service service = new Service(args);
        assertEquals(args.app, service.getApp());
        assertEquals(args.host, service.getHost());
        assertEquals(args.owner, service.getOwner());
        assertEquals((int) args.port, (int) service.getPort());
        assertEquals(args.scheme, service.getScheme());
        assertEquals(args.token, service.getToken());
    }
    
    @Test
    public void testNewServiceArgsAsMap() {
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
    
    @Test
    public void testNewServiceArgsWithDefaults() {
        ServiceArgs args = new ServiceArgs();
        
        Service service = new Service(args);
        assertEquals(null, service.getApp());
        assertEquals("localhost", service.getHost());
        assertEquals(null, service.getOwner());
        assertEquals(8089, (int) service.getPort());
        assertEquals("https", service.getScheme());
        assertEquals(null, service.getToken());
    }
    
    @Test
    public void testConstructors() {
        Service s;
        
        s = new Service("localhost");
        assertEquals("localhost", s.getHost());
        assertEquals(8089, s.getPort());
        assertEquals("https", s.getScheme());
        
        s = new Service("localhost", 9999);
        assertEquals("localhost", s.getHost());
        assertEquals(9999, s.getPort());
        assertEquals("https", s.getScheme());
        
        s = new Service("localhost", 9999, "http");
        assertEquals("localhost", s.getHost());
        assertEquals(9999, s.getPort());
        assertEquals("http", s.getScheme());
    }
    
    @Test
    public void testSearch() throws IOException {
        service.search(QUERY);    // throws no exception

        Job job = service.search(QUERY, new Args());
        while (!job.isDone()) {
            sleep(50);
        }
        
        InputStream jobOutput = job.getResults();
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(jobOutput);
        
            Map<String, String> event;
            int nEvents = 0;
        
            do {
                event = resultsReader.getNextEvent();
                if (event != null) {
                    nEvents += 1;
                }
            } while (event != null);
        
            assertEquals(10, nEvents);
        }
        finally {
            jobOutput.close();
        }
    }
    
    @Test
    public void testOneshot() throws IOException {
        service.oneshotSearch(QUERY); // throws no exception
        
        InputStream jobOutput = service.oneshotSearch(
            QUERY,
            new Args("output_mode", "json")
        );
 
        try {
            ResultsReaderJson resultsReader = new ResultsReaderJson(jobOutput);
        
            Map<String, String> event;
            int nEvents = 0;
        
            do {
                event = resultsReader.getNextEvent();
                if (event != null) {
                    nEvents += 1;
                }
            } while (event != null);
        
            assertEquals(10, nEvents);
        }
        finally {
            jobOutput.close();
        }
    }
    
    // === Utility ===
    
    private static void checkResponse(ResponseMessage response) {
        assertEquals(200, response.getStatus());
        
        // Make sure we can at least load the Atom response
        AtomFeed.parseStream(response.getContent());
    }
    
    // Wait for the given job to be ready
    private static Job ready(Job job) {
        while (!job.isReady()) {
            sleep(10);
        }
        return job;
    }

    @Test
    public void testLoginWithNamespace() {
        Args loginArgs = new Args();
        loginArgs.add("username", command.opts.get("username"));
        loginArgs.add("password", command.opts.get("password"));
        loginArgs.add("sharing", "user");
        loginArgs.add("owner", "admin");
        loginArgs.add("app", "search");
        loginArgs.add("host", command.opts.get("host"));
        loginArgs.add("port", command.opts.get("port"));

        Service.connect(loginArgs);
    }

    @Test
    public void testHandleErrorsReturnedAsJson() {
        JobExportArgs exportArgs = new JobExportArgs();

        exportArgs.setOutputMode(JobExportArgs.OutputMode.JSON);
        exportArgs.setSearchMode(JobExportArgs.SearchMode.REALTIME);
        exportArgs.setEarliestTime("rt");
        exportArgs.setLatestTime("rt");

        try {
            service.export("notasearchcommand", exportArgs);
        } catch (Exception e) {
            assertEquals(
                    "HTTP 400 -- {\"messages\":[{\"type\":\"FATAL\",\"text\":" +
                            "\"Unknown search command 'notasearchcommand'.\"}]}",
                    e.getMessage()
            );
            return;
        }
        fail();
    }

    @Test
    public void testDelete() {
        Args deleteArgs = Args.create("output_mode", "json");
        try {
            service.delete("/services/search/jobs/foobar_doesntexist", deleteArgs);
        } catch (HttpException e) {
            assertEquals(404, e.getStatus());
            assertNotNull(e.getDetail());
        }
    }

    @Test
    public void testPost() {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("foo", "bar");

        ResponseMessage response;
        response = service.post("/services/search/jobs", args);
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

}
