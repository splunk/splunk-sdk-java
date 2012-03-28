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

import java.util.Collection;
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

    @Test public void testLiveNamespace1() throws Exception {

        String username = "sdk-user";
        String password = "changeme";
        String savedSearch = "sdk-test1";
        String searchString = "search index=main * | head 10";

        // Setup a namespace
        HashMap<String, String> namespace = new HashMap<String, String>();
        namespace.put("owner", username);
        namespace.put("app", "search");

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

    @Test public void testLiveNamespace2() throws Exception {

        /* establish naming convention for separate namespaces */
        String search = "search *";

        String searchName11 = "sdk-test-search11";
        String searchName12 = "sdk-test-search12";
        String searchName21 = "sdk-test-search21";
        String searchName22 = "sdk-test-search22";

        String username1 = "sdk-user1";
        String username2 = "sdk-user2";
        String appname1 = "sdk-app1";
        String appname2 = "sdk-app2";

        HashMap<String, String> namespace11 = new HashMap<String, String>();
        HashMap<String, String> namespace12 = new HashMap<String, String>();
        HashMap<String, String> namespace21 = new HashMap<String, String>();
        HashMap<String, String> namespace22 = new HashMap<String, String>();
        HashMap<String, String> namespacex1  = new HashMap<String, String>();
        HashMap<String, String> namespaceNobody1 = new HashMap<String, String>();
        HashMap<String, String> namespaceBad = new HashMap<String, String>();

        namespace11.put("owner", username1);
        namespace11.put("app",  appname1);
        namespace12.put("owner", username1);
        namespace12.put("app",  appname2);
        namespace21.put("owner", username2);
        namespace21.put("app",  appname1);
        namespace22.put("owner", username2);
        namespace22.put("app",  appname2);
        namespacex1.put("owner", "-");
        namespacex1.put("app", appname1);
        namespaceNobody1.put("owner", "nobody");
        namespaceNobody1.put("app", appname1);
        namespaceBad.put("owner", "magilicuddy");
        namespaceBad.put("app",  "oneBadApp");

        Service service = connect(); // using default name space

        /* scrub to make sure apps don't already exist */
        EntityCollection<Application> apps = service.getApplications();
        if (apps.containsKey(appname1)) {
            apps.remove(appname1);
            splunkRestart();
            service = connect(); // using default name space
            apps = service.getApplications();
        }
        if (apps.containsKey(appname2)) {
            apps.remove(appname2);
            splunkRestart();
            service = connect(); // using default name space
            apps = service.getApplications();
        }
        assertFalse(apps.containsKey(appname1));
        assertFalse(apps.containsKey(appname2));

        /* scrub to make sure users don't already exist */
        UserCollection users = service.getUsers();
        if (users.containsKey(username1))
            users.remove(username1);
        if (users.containsKey(username2))
            users.remove(username2);
        assertFalse(users.containsKey(username1));
        assertFalse(users.containsKey(username2));

        /* create users */
        users.create(username1, "abc", "user");
        users.create(username2, "abc", "user");
        assertTrue(users.containsKey(username1));
        assertTrue(users.containsKey(username2));

        /* create apps */
        apps.create(appname1);
        apps.create(appname2);
        assertTrue(apps.containsKey(appname1));
        assertTrue(apps.containsKey(appname2));

        /* create namespace specfic UNIQUE searches */
        SavedSearchCollection
                savedSearches11 = service.getSavedSearches(namespace11);
        SavedSearchCollection
                savedSearches12 = service.getSavedSearches(namespace12);
        SavedSearchCollection
                savedSearches21 = service.getSavedSearches(namespace21);
        SavedSearchCollection
                savedSearches22 = service.getSavedSearches(namespace22);
        SavedSearchCollection
                savedSearchesx1 = service.getSavedSearches(namespacex1);
        SavedSearchCollection
            savedSearchesNobody1 = service.getSavedSearches(namespaceNobody1);

        // create in 11 namespace, make sure there, but not in others
        savedSearches11.create(searchName11, search);
        assertTrue(savedSearches11.containsKey(searchName11));
        savedSearches12.refresh();
        assertFalse(savedSearches12.containsKey(searchName11));
        savedSearches21.refresh();
        assertFalse(savedSearches21.containsKey(searchName11));
        savedSearches22.refresh();
        assertFalse(savedSearches22.containsKey(searchName11));

        // create in 12 namespace, make sure there, but not in others
        savedSearches12.create(searchName12, search);
        assertTrue(savedSearches12.containsKey(searchName12));
        savedSearches11.refresh();
        assertFalse(savedSearches11.containsKey(searchName12));
        savedSearches12.refresh();
        assertFalse(savedSearches21.containsKey(searchName12));
        savedSearches22.refresh();
        assertFalse(savedSearches22.containsKey(searchName12));

        // create in 21 namespace, make sure there, but not in others
        savedSearches21.create(searchName21, search);
        assertTrue(savedSearches21.containsKey(searchName21));
        savedSearches11.refresh();
        assertFalse(savedSearches11.containsKey(searchName21));
        savedSearches12.refresh();
        assertFalse(savedSearches12.containsKey(searchName21));
        savedSearches22.refresh();
        assertFalse(savedSearches22.containsKey(searchName21));

        // create in 22 namespace, make sure there, but not in others
        savedSearches22.create(searchName22, search);
        assertTrue(savedSearches22.containsKey(searchName22));
        savedSearches11.refresh();
        assertFalse(savedSearches11.containsKey(searchName22));
        savedSearches12.refresh();
        assertFalse(savedSearches12.containsKey(searchName22));
        savedSearches21.refresh();
        assertFalse(savedSearches21.containsKey(searchName22));

        /* now remove the UNIQUE saved searches */
        savedSearches11.remove(searchName11);
        savedSearches12.remove(searchName12);
        savedSearches21.remove(searchName21);
        savedSearches22.remove(searchName22);
        assertFalse(savedSearches11.containsKey(searchName11));
        assertFalse(savedSearches12.containsKey(searchName12));
        assertFalse(savedSearches21.containsKey(searchName21));
        assertFalse(savedSearches22.containsKey(searchName22));

        /* create same search name in different namespaces */
        savedSearches11.create("sdk-test-search", search + " | head 1");
        savedSearches21.create("sdk-test-search", search + " | head 2");
        savedSearchesNobody1.create("sdk-test-search", search + " | head 4");
        assertTrue(savedSearches11.containsKey("sdk-test-search"));
        assertTrue(savedSearches21.containsKey("sdk-test-search"));
        assertTrue(savedSearchesNobody1.containsKey("sdk-test-search"));

        // we have created three saved searches with the same name, make sure we
        // can see all three with a wild-carded get.
        savedSearchesx1.refresh();
        assertTrue(savedSearchesx1.values().size() == 3);

        assertFalse(savedSearchesx1.containsKey("sdk-test-search", namespaceBad));
        assertTrue(savedSearchesx1.containsKey("sdk-test-search", namespace21));
        assertTrue(savedSearchesx1.get("sdk-test-search", namespace21) != null);

        // remove one of the saved searches through a specific namespace path
        savedSearchesx1.remove("sdk-test-search", namespace21);
        savedSearches11.remove("sdk-test-search");
        savedSearchesNobody1.remove("sdk-test-search");
        assertFalse(savedSearches11.containsKey("sdk-test-search"));
        savedSearches21.refresh();
        assertFalse(savedSearches21.containsKey("sdk-test-search"));
        assertFalse(savedSearchesNobody1.containsKey("sdk-test-search"));

        /* cleanup apps */
        apps.refresh();
        if (apps.containsKey(appname1)) {
            apps.remove(appname1);
            splunkRestart();
            service = connect(); // using default name space
            apps = service.getApplications();
        }
        if (apps.containsKey(appname2)) {
            apps.remove(appname2);
            splunkRestart();
            service = connect(); // using default name space
            apps = service.getApplications();
        }
        assertFalse(apps.containsKey(appname1));
        assertFalse(apps.containsKey(appname2));

        /* cleanup users */
        users = service.getUsers(); // need to re-establish, because of restart
        if (users.containsKey(username1))
            users.remove(username1);
        if (users.containsKey(username2))
            users.remove(username2);
        assertFalse(users.containsKey(username1));
        assertFalse(users.containsKey(username2));
    }
}

