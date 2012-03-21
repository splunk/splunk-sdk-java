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

import java.util.HashMap;
import java.net.Socket;
import org.junit.Test;


public class NamespaceTest extends SplunkTestCase {

    // future use, when dynamic app creation/deletion test in place.
    private void splunkRestart() throws Exception {

        boolean restarted = false;

        Service service = connect();

        ResponseMessage response = service.restart();
        assertEquals(200, response.getStatus());

        // port sniff. expect connection ... then no connection ...
        // the connection. Max 3 minutes.

        int totalTime = 0;
        // server up, wait until socket no longer accepted.
        while (totalTime < (3*60*1000)) {
            try {
                Socket ServerSok = new Socket(service.getHost(),service.getPort());
			    ServerSok.close();
			    Thread.sleep(10); // 10 milliseconds
                totalTime += 10;
    		}
            catch (Exception e) {
                break;
		    }
        }

        // server down, wait until socket accepted.
        while (totalTime < (3*60*1000)) {
            try {
                Socket ServerSok = new Socket(service.getHost(),service.getPort());
			    ServerSok.close();
                break;

    		}
            catch (Exception e) {
			    Thread.sleep(10); // 10 milliseconds
                totalTime += 10;
		    }
        }

        while (totalTime < (3*60*1000)) {
            try {
                connect();
                restarted = true;
                break;
            }
            catch (Exception e) {
                // server not back yet
                Thread.sleep(100);
                totalTime += 10;
            }
        }
        assertTrue(restarted);
    }

    @Test public void testStaticNamespace() {
        Service service = connect();
        HashMap<String, String> namespace = new HashMap<String, String>();

        // synctactic tests
        namespace.clear();
        assertTrue(service.fullpath("", null).equals("/services/"));

        namespace.clear();
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/Bob/-/"));

        namespace.clear();
        namespace.put("app", "search");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/-/search/"));

        namespace.clear();
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/Bob/search/"));

        namespace.clear();
        namespace.put("sharing", "user");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/-/-/"));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/Bob/-/"));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/-/search/"));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/Bob/search/"));

        namespace.clear();
        namespace.put("sharing", "app");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/-/"));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/-/"));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/search/"));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/search/"));

        namespace.clear();
        namespace.put("sharing", "global");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/-/"));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/-/"));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/search/"));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/search/"));

        namespace.clear();
        namespace.put("sharing", "system");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/system/"));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/system/"));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/system/"));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertTrue(service.fullpath("", namespace)
                .equals("/servicesNS/nobody/system/"));
    }



    @Test public void testLiveNamespace() throws Exception {

        String username = "sdk-user";
        String password = "changeme";
        String savedSearch = "sdk-test1";
        String searchString = "search index=main * | head 10";

        // Setup a namespace
        HashMap<String, String> namespace = new HashMap<String, String>();
        namespace.put("app", "search");
        namespace.put("owner", username);

        Service service = connect(); // using default name space

        // get all users, scrub and make our test user
        UserCollection users = service.getUsers();
        if (users.containsKey(username))
            users.remove(username);
        assertFalse(users.containsKey(username));
        users.create(username, password, "user");
        assertTrue(users.containsKey(username));

        // get saved searches for our new namespace, clean to make sure
        // we remove, before we create one.
        SavedSearchCollection savedSearches =
                service.getSavedSearches(namespace);

        if (savedSearches.containsKey(savedSearch))
            savedSearches.remove(savedSearch);
        assertFalse(savedSearches.containsKey(savedSearch));

        savedSearches.create(savedSearch, searchString);
        assertTrue(savedSearches.containsKey(savedSearch));

        // remove saved search
        savedSearches.remove(savedSearch);
        assertFalse(savedSearches.containsKey(savedSearch));

        // remove user
        users.remove(username);
        assertFalse(users.containsKey(username));
    }
}

