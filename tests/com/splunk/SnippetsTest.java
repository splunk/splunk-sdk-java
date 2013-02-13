/*
 * Copyright 2012 Splunk, Inc.
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

import javax.management.relation.RoleStatus;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * [Insert documentation here]
 */
public class SnippetsTest extends SDKTestCase {
    @Test
    public void testConnect1() {
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setUsername((String)command.opts.get("username"));
        loginArgs.setPassword((String) command.opts.get("password"));
        loginArgs.setHost((String) command.opts.get("host"));
        loginArgs.setPort((Integer)command.opts.get("port"));

        // Create a Service instance and log in with the argument map
        Service service = Service.connect(loginArgs);

        // A second way to create a new Service object and log in
        // Service service = new Service("localhost", 8089);
        // service.login("admin", "changeme");

        // A third way to create a new Service object and log in
        // Service service = new Service(loginArgs);
        // service.login();

        // Print installed apps to the console to verify login
        for (Application app : service.getApplications().values()) {
            System.out.println(app.getName());
        }
        assertTrue(true);
    }

    @Test
    public void testSavedSearches1() {
        // Connect to Splunk
        Service service = Service.connect(command.opts);

        // Retrieves the collection of saved searches
        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Creates a saved search
        {
            String name = createTemporaryName();
            SavedSearch savedSearch = savedSearches.create(name, "search * | head 10");
        }

        // Another way to create a saved search
        {
            String name = createTemporaryName();
            SavedSearch savedSearch = service.getSavedSearches().create(name, "search * | head 10");
        }
    }

    @Test
    public void testSavedSearches2() {
        // List all saved searches for the current namespace
        SavedSearchCollection savedSearches = service.getSavedSearches();
        System.out.println(savedSearches.size() + " saved searches are available to the current user:\n");
        for (SavedSearch entity: savedSearches.values()) {
            System.out.println("     " + entity.getName());
        }
    }

    @Test
    public void testSavedSearches3() {
        // Get the collection of users and save the name of the last user
        UserCollection users = service.getUsers();
        String lastUser = null;
        for (User user : users.values()) {
            lastUser = user.getName();
        }

        // Specify a namespace using the name of the last user
        ServiceArgs namespace = new ServiceArgs();
        namespace.setApp("search");
        namespace.setOwner(lastUser);
        SavedSearchCollection savedSearches2 = service.getSavedSearches(namespace);

        System.out.println(savedSearches2.size() + " saved searches are available to '" + lastUser + "':\n");
        for(SavedSearch search : savedSearches2.values()) {
            System.out.println("     " + search.getName());
        }
    }

    @Test
    public void testSavedSearches4() {
        // Retrieve the collection of saved searches
        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Iterate through the collection of saved searches and display the history for each one
        for(SavedSearch entity : savedSearches.values()) {
            Job[] sHistory = entity.history();
            System.out.println("\n" + sHistory.length + " jobs for the '" + entity.getName() + "' saved search");
            for (int i = 0; i < sHistory.length; ++i) {
                System.out.println("     " + sHistory[i].getEventCount() + " events for Search ID " + sHistory[i].getSid() + "\n");
            }
        }
    }

    @Test
    public void testSavedSearches5() {
        if (service.getSavedSearches().containsKey("Test Search")) {
            service.getSavedSearches().remove("Test Search");
        }
        // ###
        // Create a saved search by specifying a name and search query
        // Note: Do not include the 'search' keyword for a saved search
        String myQuery = "* | head 10";
        String mySearchName = "Test Search";
        SavedSearch savedSearch = service.getSavedSearches().create(mySearchName, myQuery);
        System.out.println("The search '" + savedSearch.getName() +
                "' (" + savedSearch.getSearch() + ") was saved");
    }

    @Test
    public void testSavedSearches6() {
        {
            if (service.getSavedSearches().containsKey("Test Search")) {
                service.getSavedSearches().remove("Test Search");
            }
            String myQuery = "* | head 10";
            String mySearchName = "Test Search";
            SavedSearch savedSearch = service.getSavedSearches().create(mySearchName, myQuery);
        }
        // ###

        // Retrieve the search that was just created
        SavedSearch savedSearch = service.getSavedSearches().get("Test Search");

        // Display some properties of the new search
        System.out.println("Properties for '" + savedSearch.getName() + "':\n\n" +
                "Description:         " + savedSearch.getDescription() + "\n" +
                "Scheduled:           " + savedSearch.isScheduled() + "\n" +
                "Next scheduled time: " + savedSearch.getNextScheduledTime() + "\n"
        );
    }

    @Test
    public void testSavedSearches7() {
        {
            if (service.getSavedSearches().containsKey("Test Search")) {
                service.getSavedSearches().remove("Test Search");
            }
            String myQuery = "* | head 10";
            String mySearchName = "Test Search";
            SavedSearch savedSearch = service.getSavedSearches().create(mySearchName, myQuery);
        }

        // Retrieve the new saved search
        SavedSearch savedSearch = service.getSavedSearches().get("Test Search");

        // Set the properties and schedule
        savedSearch.setDescription("This is a test search");
        savedSearch.setIsScheduled(true);
        savedSearch.setCronSchedule("15 4 * * 6");

        // Update the server with changes
        savedSearch.update();

        System.out.println("New properties for '" + savedSearch.getName()
                + "':\n\n" + "Description:         "
                + savedSearch.getDescription() + "\n" + "Scheduled:           "
                + savedSearch.isScheduled() + "\n" + "Next scheduled time: "
                + savedSearch.getNextScheduledTime() + "\n");

    }

    @Test
    public void testSavedSearches8() {
        {
            if (service.getSavedSearches().containsKey("Test Search")) {
                service.getSavedSearches().remove("Test Search");
            }
            String myQuery = "* | head 10";
            String mySearchName = "Test Search";
            SavedSearch savedSearch = service.getSavedSearches().create(mySearchName, myQuery);
        }
        // Retrieve the new saved search
        SavedSearch savedSearch = service.getSavedSearches().get("Test Search");

        // Run a saved search and poll for completion
        System.out.println("Run the '" + savedSearch.getName() + "' search ("
                + savedSearch.getSearch() + ")\n");
        Job jobSavedSearch = null;

        // Run the saved search
        try {
            jobSavedSearch = savedSearch.dispatch();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("Waiting for the job to finish...\n");

        // Wait for the job to finish
        while (!jobSavedSearch.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Get the search results and use the built-in XML parser to display them
        try {
            InputStream results =  jobSavedSearch.getResults();
            ResultsReaderXml resultsReader = new ResultsReaderXml(results);
            HashMap<String, String> map;

            while ((map = resultsReader.getNextEvent()) != null) {
                System.out.println("\n****************EVENT****************\n");
                for (String key: map.keySet())
                    System.out.println("   " + key + ":  " + map.get(key));
            }
            resultsReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSavedSearches9() {
        {
            if (service.getSavedSearches().containsKey("Test Search")) {
                service.getSavedSearches().remove("Test Search");
            }
            String myQuery = "* | head 10";
            String mySearchName = "Test Search";
            SavedSearch savedSearch = service.getSavedSearches().create(mySearchName, myQuery);
        }
        // Retrieve a saved search
        SavedSearch savedSearch = service.getSavedSearches().get("Test Search");

        // Delete the saved search
        savedSearch.remove();

    }

    @Test
    public void testSavedSearches10() {
        {
            if (service.getSavedSearches().containsKey("My Test Search")) {
                service.getSavedSearches().remove("My Test Search");
            }
        }
        // Create an argument map with properties for a new saved search
        Args savedSearchArgs = new Args();
        savedSearchArgs.put("description", "This is my test search");
        SavedSearch savedSearch = service.getSavedSearches().create("My Test Search",
                "search * | head 5", savedSearchArgs);

    }

    @Test
    public void testJobs1() {
        // Connect to Splunk
        Service service = Service.connect(command.opts);

        // Retrieves the collection of search jobs
        JobCollection jobs = service.getJobs();

        String query = "search * | head 10";
        {
            // Creates a search job
            Job job = jobs.create(query);
        }

        {
            // Another way to create a search job
            Job job = service.getJobs().create(query);
        }

    }

    @Test
    public void testJobs2() {
        // Retrieve the collection
        JobCollection jobs = service.getJobs();
        System.out.println("There are " + jobs.size() + " jobs available to 'admin'\n");

        // List the job SIDs
        for(Job job : jobs.values()) {
            System.out.println(job.getName());
        }
    }

    @Test
    public void testJobs3() {
        // Run a blocking search
        String searchQuery_blocking = "search * | head 100"; // Return the first 100 events
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);

        // A blocking search returns the job when the search is done
        System.out.println("Wait for the search to finish...");
        Job job = service.getJobs().create(searchQuery_blocking, jobargs);
        System.out.println("...done!\n");

        // Get properties of the job
        System.out.println("Search job properties:\n---------------------");
        System.out.println("Search job ID:         " + job.getSid());
        System.out.println("The number of events:  " + job.getEventCount());
        System.out.println("The number of results: " + job.getResultCount());
        System.out.println("Search duration:       " + job.getRunDuration() + " seconds");
        System.out.println("This job expires in:   " + job.getTtl() + " seconds");

        // Find out how many results your system is configured to return
        Entity restApi = service.getConfs().get("limits").get("restapi");
        int maxResults = Integer.parseInt((String)restApi.get("maxresultrows"));
        System.out.println("Your system is configured to return a maximum of " + maxResults + " results");

        // Page through results by looping through sets of results
        int resultCount = job.getResultCount(); // Number of results this job returned
        int x = 0;          // Result counter
        int offset = 0;     // Start at result 0
        int count = 10;     // Get sets of 10 results at a time

        // Loop through each set of results
        while (offset < resultCount) {
            Args outputArgs = new Args();
            outputArgs.put("count", count);
            outputArgs.put("offset", offset);

            // Get the search results and use the built-in XML parser to display them
            InputStream blocksearchresults =  job.getResults(outputArgs);

            try {
                ResultsReaderXml resultsReader = new ResultsReaderXml(blocksearchresults);
                HashMap<String, String> map;

                while ((map = resultsReader.getNextEvent()) != null) {
                    System.out.println("\n**************** RESULT " + x + " ****************\n");
                    for (String key: map.keySet())
                        System.out.println("   " + key + ":  " + map.get(key));
                    x++;
                }
                resultsReader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Increase the offset to get the next set of results
            offset = offset + count;
        }
    }

    @Test
    public void testJobs4() {
        // Run a normal search
        String searchQuery_normal = "search * | head 100";
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(searchQuery_normal, jobargs);

        // Wait for the search to finish
        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Get the search results and use the built-in XML parser to display them
        InputStream resultsNormalSearch =  job.getResults();

        ResultsReaderXml resultsReaderNormalSearch;

        try {
            resultsReaderNormalSearch = new ResultsReaderXml(resultsNormalSearch);
            HashMap<String, String> map;
            while ((map = resultsReaderNormalSearch.getNextEvent()) != null) {
                System.out.println("\n****************EVENT****************\n");
                for (String key: map.keySet())
                    System.out.println("   " + key + ":  " + map.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get properties of the completed job
        System.out.println("\nSearch job properties\n---------------------");
        System.out.println("Search job ID:         " + job.getSid());
        System.out.println("The number of events:  " + job.getEventCount());
        System.out.println("The number of results: " + job.getResultCount());
        System.out.println("Search duration:       " + job.getRunDuration() + " seconds");
        System.out.println("This job expires in:   " + job.getTtl() + " seconds");
    }

    @Test
    public void testJobs5() {
        // Set the parameters for the search:
        Args oneshotSearchArgs = new Args();
        oneshotSearchArgs.put("earliest_time", "2012-06-19T12:00:00.000-07:00");
        oneshotSearchArgs.put("latest_time",   "2012-06-20T12:00:00.000-07:00");
        String oneshotSearchQuery = "search * | head 10";

        // The search results are returned directly
        InputStream results_oneshot =  service.oneshotSearch(oneshotSearchQuery, oneshotSearchArgs);

        // Get the search results and use the built-in XML parser to display them
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(results_oneshot);
            System.out.println("Searching everything in a 24-hour time range starting June 19, 12:00pm and displaying 10 results in XML:\n");
            HashMap<String, String> map;
            while ((map = resultsReader.getNextEvent()) != null) {
                System.out.println("\n********EVENT********");
                for (String key: map.keySet())
                    System.out.println("   " + key + ":  " + map.get(key));
            }
            resultsReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInput1() {
        {
            if (service.getInputs().containsKey("9999")) {
                service.getInputs().remove("9999");
            }
        }

        // Connect to Splunk
        Service service = Service.connect(command.opts);

        // Retrieve the collection of data inputs
        InputCollection myInputs = service.getInputs();

        // Create a TCP input
        TcpInput tcpInput = myInputs.create("9999", InputKind.Tcp);
    }

    @Test
    public void testInput2() {
        // Get the collection of data inputs
        InputCollection myInputs = service.getInputs();

        // Iterate and list the collection of inputs
        System.out.println("There are " + myInputs.size() + " data inputs:\n");
        for (Input entity: myInputs.values()) {
            System.out.println("  " + entity.getName() + " (" + entity.getKind() + ")");
        }
    }

    @Test
    public void testInput3() {
        {
            if (service.getInputs().containsKey("/Users/fross/splunks/splunk-5.0.2/README-splunk.txt")) {
                service.getInputs().remove("/Users/fross/splunks/splunk-5.0.2/README-splunk.txt");
            }
        }
        // Get the collection of data inputs
        InputCollection myInputs = service.getInputs();

        // Create a new Monitor data input
        String monitor_filepath = "/Users/fross/splunks/splunk-5.0.2/README-splunk.txt";
        MonitorInput monitorInput= myInputs.create(monitor_filepath, InputKind.Monitor);
    }

    @Test
    public void testInput4() {
        // Retrieve the new input
        String testinput = "/Users/fross/splunks/splunk-5.0.2/README-splunk.txt";
        {
            if (!service.getInputs().containsKey(testinput)) {
                MonitorInput monitorInput= (MonitorInput)service.getInputs().create(testinput, InputKind.Monitor);
            }
        }

        MonitorInput monitorInput = (MonitorInput) service.getInputs().get(testinput);

        // Retrieve and display some properties for the new input
        System.out.println("Name:      " + monitorInput.getName());
        System.out.println("Kind:      " + monitorInput.getKind());
        System.out.println("Path:      " + monitorInput.getPath());
        System.out.println("Index:     " + monitorInput.getIndex());
        System.out.println("Whitelist: " + monitorInput.getWhitelist());

        // Modify some properties and update the server
        System.out.println("\nSet some properties\n");
        monitorInput.setIndex("main");
        monitorInput.setWhitelist("phonyregex*2");
        monitorInput.update();

        // Display the changed properties again to show the change
        System.out.println("Index:     " + monitorInput.getIndex());
        System.out.println("Whitelist: " + monitorInput.getWhitelist());
    }

    @Test
    public void testInput5() {
        // Retrieve the collection of indexes, sorted by number of events
        IndexCollectionArgs indexcollArgs = new IndexCollectionArgs();
        indexcollArgs.setSortKey("totalEventCount");
        indexcollArgs.setSortDirection(IndexCollectionArgs.SortDirection.DESC);
        IndexCollection myIndexes = service.getIndexes(indexcollArgs);

// List the indexes and their event counts
        System.out.println("There are " + myIndexes.size() + " indexes:\n");
        for (Index entity: myIndexes.values()) {
            System.out.println("  " + entity.getName() + " (events: "
                    + entity.getTotalEventCount() + ")");
        }
    }

    @Test
    public void testInput6() {
        {
            if (service.getIndexes().containsKey("test_index")) {
                service.getIndexes().remove("test_index");
                assertEventuallyTrue(new EventuallyTrueBehavior() {
                    @Override
                    public boolean predicate() {
                        return !service.getIndexes().containsKey("test_index");
                    }
                });
            }
        }

        //Get the collection of indexes
        IndexCollection myIndexes = service.getIndexes();

        //Create a new index
        Index myIndex = myIndexes.create("test_index");
    }

    @Test
    public void testInput7() {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }
        // Retrieve the index that was created earlier
        Index myIndex = service.getIndexes().get("test_index");

        // Retrieve properties
        System.out.println("Name:                " + myIndex.getName());
        System.out.println("Current DB size:     " + myIndex.getCurrentDBSizeMB() + "MB");
        System.out.println("Max hot buckets:     " + myIndex.getMaxHotBuckets());
        System.out.println("# of hot buckets:    " + myIndex.getNumHotBuckets());
        System.out.println("# of warm buckets:   " + myIndex.getNumWarmBuckets());
        System.out.println("Max data size:       " + myIndex.getMaxDataSize());
        System.out.println("Max total data size: " + myIndex.getMaxTotalDataSizeMB() + "MB");

        // Modify a property and update the server
        myIndex.setMaxTotalDataSizeMB(myIndex.getMaxTotalDataSizeMB()-1);
        myIndex.update();
        System.out.println("Max total data size: " + myIndex.getMaxTotalDataSizeMB() + "MB");

    }

    @Test
    public void testInput8() {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }
        // Retrieve the index that was created earlier
        Index myIndex = service.getIndexes().get("test_index");

        // Clean events from the index, printing the before-and-after size
        System.out.println("Current DB size:     " + myIndex.getCurrentDBSizeMB() + "MB");
        myIndex.clean(180);
        System.out.println("Current DB size:     " + myIndex.getCurrentDBSizeMB() + "MB");

    }

    @Test
    public void testInput9() {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }

        // Retrieve the index for the data
        Index myIndex = service.getIndexes().get("test_index");

        // Specify a file and upload it
        String uploadme = "/Users/fross/splunks/splunk-5.0.2/README-splunk.txt";
        myIndex.upload(uploadme);
    }

    @Test
    public void testInput10() {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }
        // Retrieve the index for the data
        Index myIndex = service.getIndexes().get("test_index");

        // Specify  values to apply to the event
        Args eventArgs = new Args();
        eventArgs.put("sourcetype", "access_combined.log");
        eventArgs.put("host", "local");

        // Submit an event over HTTP
        myIndex.submit(eventArgs, "This is my HTTP event");
    }

    @Test
    public void testInput11() throws Exception {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }
        // Retrieve the index for the data
        Index myIndex = service.getIndexes().get("test_index");

        // Set up a timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String date = sdf.format(new Date());

        // Open a socket and stream
        Socket socket = myIndex.attach();
        try {
            OutputStream ostream = socket.getOutputStream();
            Writer out = new OutputStreamWriter(ostream, "UTF8");

            // Send events to the socket then close it
            out.write(date + "Event one!\r\n");
            out.write(date + "Event two!\r\n");
            out.flush();
        } finally {
            socket.close();
        }
    }

    @Test
    public void testInput12() throws IOException {
        {
            if (!service.getIndexes().containsKey("test_index")) {
                service.getIndexes().create("test_index");
            }
        }
        Index myIndex = service.getIndexes().get("test_index");

        myIndex.attachWith(new ReceiverBehavior() {
            public void run(OutputStream stream) throws IOException {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                String date = dateFormat.format(new Date());
                String eventText = date + " Boris the mad baboon!\r\n";
                stream.write(eventText.getBytes("UTF8"));
            }
        });

    }

    @Test
    public void testInput13() throws IOException {
        {
            if (!service.getInputs().containsKey("10000")) {
                service.getInputs().create("10000", InputKind.Tcp);
            }
        }

        // Retrieve the input
        TcpInput myInput = (TcpInput)service.getInputs().get("10000");

        // Send a single event to the input
        myInput.submit("This is my event.");

    }

    @Test
    public void testInput14() throws IOException {
        {
            if (!service.getInputs().containsKey("10000")) {
                service.getInputs().create("10000", InputKind.Tcp);
            }
        }
        // Retrieve the input
        TcpInput myInput = (TcpInput)service.getInputs().get("10000");

// Open a socket
        Socket socket = myInput.attach();

// Wrap the socket in a try block so we can close it in case of an error
        try {
            OutputStream ostream = socket.getOutputStream();
            Writer out = new OutputStreamWriter(ostream, "UTF8");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            String date = dateFormat.format(new Date());
            // Send events to the socket then close it
            out.write(date + "Event one!\r\n");
            out.write(date + "Event two!\r\n");
            out.flush();
        } finally {
            socket.close();
        }
    }

    @Test
    public void testInput15() throws IOException {
        TcpInput myInput = (TcpInput)service.getInputs().get("10000");

        myInput.attachWith(new ReceiverBehavior() {
            public void run(OutputStream stream) throws IOException {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                String date = dateFormat.format(new Date());
                String eventText = date + " Boris the mad baboon!\r\n";
                stream.write(eventText.getBytes("UTF8"));
            }
        });
    }

    @Test
    public void testInput16() throws IOException {
        {
            if (!service.getInputs().containsKey("9998")) {
                service.getInputs().create("9998", InputKind.Udp);
            }
        }
        // Get a UDP input to send to
        UdpInput myInput = (UdpInput)service.getInputs().get("9998");

        // Send an event via a UDP datagram
        myInput.submit("This is my event.");

    }

    @Test
    public void testUsers1() {
        {
            if (service.getUsers().containsKey("boris")) {
                service.getUsers().remove("boris");
            }
        }
        // Connect to Splunk
        Service service = Service.connect(command.opts);

        // Get the collection of users
        UserCollection usercollection = service.getUsers();

        // Create a user
        String[] capabilities = {"power"};
        User user = service.getUsers().create("boris", "meep", capabilities);

    }

    @Test
    public void testUsers2() {
        // List all users, sorted by real name
        Args userArgs = new Args();
        userArgs.put("sort_key", "realname");
        userArgs.put("sort_dir", "asc");
        UserCollection usercoll = service.getUsers(userArgs);

        // Display info for each user (real name, username, roles)
        System.out.println("There are " + usercoll.size() + " users:\n");
        for (User user: usercoll.values()) {
            System.out.println(user.getRealName() + " (" + user.getName() + ")");
            for (String role: user.getRoles()) {
                System.out.println(" - " + role);
            }
        }
    }

    @Test
    public void testUser3() {
        {
            if (service.getUsers().containsKey("testuser")) {
                service.getUsers().remove("testuser");
            }
        }
        // Create a new user with multiple roles
        String username = "testuser";
        String password = "changeme";
        String[] roles = {"power","user"};
        User user = service.getUsers().create(username, password, roles);

// Display the properties of the new user
        System.out.println("Properties for " + username + "\n-----------------------");

        System.out.println("Username:    " + user.getName());
        System.out.println("Full name:   " + user.getRealName());
        System.out.println("Default app: " + user.getDefaultApp());
        System.out.println("Time zone:   " + user.getTz());
        System.out.println("Roles:");
        for (String rolename: user.getRoles()) {
            System.out.println(" - " + rolename);
        }

// Change some properties and update the server
        user.setRealName("Test User");
        user.setRoles("can_delete");
        user.setDefaultApp("launcher");
        user.setTz("US/Pacific");
        user.update();

// Display the updated properties
        System.out.println("\n\nUpdated properties\n-----------------------");
        System.out.println("Username:    " + user.getName());
        System.out.println("Full name:   " + user.getRealName());
        System.out.println("Default app: " + user.getDefaultApp());
        System.out.println("Time zone:   " + user.getTz());
        System.out.println("Roles:");
        for (String rolename: user.getRoles()) {
            System.out.println(" - " + rolename);
        }
    }

    @Test
    public void testUsers4() {
        // Display the current user
        System.out.println("The current user is " + service.getUsername());

    }

    @Test
    public void testUsers5() {
        // Get the collection of roles
        EntityCollection<Role> rolecoll = service.getRoles();

// Display the name and capabilities (included imported ones) of each role
        System.out.println("There are " + rolecoll.size() + " defined roles:\n");
        for (Role role: rolecoll.values()) {
            System.out.println("Role: " + role.getName());
            System.out.println("  Capabilities:");
            for (String cape: role.getCapabilities()) {
                System.out.println("   - " + cape);
            }
            if(role.getImportedCapabilities().length >0) {
                System.out.println("  Imported capabilities: ");
                for (String cape: role.getImportedCapabilities()) {
                    System.out.println("   - " + cape);
                }
            }
            System.out.println("\n");
        }
    }

    @Test
    public void testUsers6() {
        {
            if (service.getRoles().containsKey("testrole")) {
                service.getRoles().remove("testrole");
            }
        }

        // Create a new role called 'testrole'
        Role role = service.getRoles().create("testrole");

// Import properties from the 'user' role and update the server
        role.setImportedRoles("user");
        role.update();

// Display the properties of the new role
        System.out.println("\nProperties for 'testrole'");
        System.out.println("-------------------------\n");
        System.out.println("Allowed indexes:");
        for (String index: role.getImportedIndexesAllowed()) {
            System.out.println(" - " + index);
        }
        System.out.println("Capabilities: ");
        for (String cape: role.getCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("Imported capabilities: ");
        for (String cape: role.getImportedCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("\n");


// Set new properties

// Set capabilities for the role
        String[] editingcape = {"indexes_edit","edit_forwarders","edit_monitor"};
        role.setCapabilities(editingcape);

// Set a default app for the role
        role.setDefaultApp("search");

// Set a search filter
        role.setSearchFilter("source=/var/log/*");

// Make changes to the server
        role.update();

// Print the latest properties of the new role
        System.out.println("\nUpdated properties\n------------------\n");
        System.out.println("Capabilities:");
        for (String cape: role.getCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("Imported capabilities:");
        for (String cape: role.getImportedCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("Imported roles:");
        for (String improle: role.getImportedRoles()) {
            System.out.println(" - " + improle);
        }
        System.out.println("Default app: " + role.getDefaultApp());
        System.out.println("Search filter: " + role.getSearchFilter());

// Set additional properties--these overwrite the existing values
        String[] editingcape2 = {"list_forwarders","list_httpauths"};
        role.setCapabilities(editingcape2);
        role.setImportedRoles("can_delete");
        role.update();


// Print the latest properties of the role again
// Note that existing properties are overwritten
        System.out.println("\n\nUpdated properties again\n------------------------\n");
        System.out.println("Capabilities:");
        for (String cape: role.getCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("Imported capabilities:");
        for (String cape: role.getImportedCapabilities()) {
            System.out.println(" - " + cape);
        }
        System.out.println("Imported roles:");
        for (String improle: role.getImportedRoles()) {
            System.out.println(" - " + improle);
        }
        System.out.println("Default app: " + role.getDefaultApp());
        System.out.println("Search filter: " + role.getSearchFilter());
    }
}
