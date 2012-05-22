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


public class NamespaceTest extends SplunkTestCase {
    final static String assertRoot = "Namespace assert: ";

    @Test public void testStaticNamespace() {
        Service service = connect();
        Args namespace = new Args();

        // synctactic tests
        namespace.clear();
        assertEquals(assertRoot + "#1", "/services/",
            service.fullpath("", null));

        namespace.clear();
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#2", "/servicesNS/Bob/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("app", "search");
        assertEquals(assertRoot + "#3", "/servicesNS/-/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#4",  "/servicesNS/Bob/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        assertEquals(assertRoot + "#5", "/servicesNS/-/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#6", "/servicesNS/Bob/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        assertEquals(assertRoot + "#7", "/servicesNS/-/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#8", "/servicesNS/Bob/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        assertEquals(assertRoot + "#9", "/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#10", "/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        assertEquals(assertRoot + "#11", "/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#12", "/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        assertEquals(assertRoot + "#13", "/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#14", "/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        assertEquals(assertRoot + "#15", "/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#16", "/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        assertEquals(assertRoot + "#17", "/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#18", "/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        assertEquals(assertRoot + "#19", "/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals(assertRoot + "#20", "/servicesNS/nobody/system/",
            service.fullpath("", namespace));
    }

    @Test public void testLiveNamespace1() throws Exception {

        String username = "sdk-user";
        String password = "changeme";
        String savedSearch = "sdk-test1";
        String searchString = "search index=main * | head 10";

        // Setup a namespace
        Args namespace = new Args();
        namespace.put("owner", username);
        namespace.put("app", "search");

        Service service = connect(); // using default name space

        // get all users, scrub and make our test user
        UserCollection users = service.getUsers();
        if (users.containsKey(username))
            users.remove(username);
        assertFalse(assertRoot + "#21", users.containsKey(username));
        users.create(username, password, "user");
        assertTrue(assertRoot + "#22", users.containsKey(username));

        // get saved searches for our new namespace, clean to make sure
        // we remove, before we create one.
        SavedSearchCollection savedSearches =
            service.getSavedSearches(namespace);

        if (savedSearches.containsKey(savedSearch))
            savedSearches.remove(savedSearch);
        assertFalse(assertRoot + "#23", savedSearches.containsKey(savedSearch));

        savedSearches.create(savedSearch, searchString);
        assertTrue(assertRoot + "#24", savedSearches.containsKey(savedSearch));

        // remove saved search
        savedSearches.remove(savedSearch);
        assertFalse(assertRoot + "#25", savedSearches.containsKey(savedSearch));

        // remove user
        users.remove(username);
        assertFalse(assertRoot + "#26", users.containsKey(username));
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

        Args namespace11 = new Args();
        Args namespace12 = new Args();
        Args namespace21 = new Args();
        Args namespace22 = new Args();
        Args namespacex1  = new Args();
        Args namespaceNobody1 = new Args();
        Args namespaceBad = new Args();

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
        assertFalse(assertRoot + "#27", apps.containsKey(appname1));
        assertFalse(assertRoot + "#28", apps.containsKey(appname2));

        /* scrub to make sure users don't already exist */
        UserCollection users = service.getUsers();
        if (users.containsKey(username1))
            users.remove(username1);
        if (users.containsKey(username2))
            users.remove(username2);
        assertFalse(assertRoot + "#29", users.containsKey(username1));
        assertFalse(assertRoot + "#30", users.containsKey(username2));

        /* create users */
        users.create(username1, "abc", "user");
        users.create(username2, "abc", "user");
        assertTrue(assertRoot + "#31", users.containsKey(username1));
        assertTrue(assertRoot + "#32", users.containsKey(username2));

        /* create apps */
        apps.create(appname1);
        apps.create(appname2);
        assertTrue(assertRoot + "#33", apps.containsKey(appname1));
        assertTrue(assertRoot + "#34", apps.containsKey(appname2));

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
        assertTrue(assertRoot + "#35",
            savedSearches11.containsKey(searchName11));
        savedSearches12.refresh();
        assertFalse(assertRoot + "#36",
            savedSearches12.containsKey(searchName11));
        savedSearches21.refresh();
        assertFalse(assertRoot + "#37",
            savedSearches21.containsKey(searchName11));
        savedSearches22.refresh();
        assertFalse(assertRoot + "#38",
            savedSearches22.containsKey(searchName11));

        // create in 12 namespace, make sure there, but not in others
        savedSearches12.create(searchName12, search);
        assertTrue(assertRoot + "#39",
            savedSearches12.containsKey(searchName12));
        savedSearches11.refresh();
        assertFalse(assertRoot + "#40",
            savedSearches11.containsKey(searchName12));
        savedSearches12.refresh();
        assertFalse(assertRoot + "#42",
            savedSearches21.containsKey(searchName12));
        savedSearches22.refresh();
        assertFalse(assertRoot + "#43",
            savedSearches22.containsKey(searchName12));

        // create in 21 namespace, make sure there, but not in others
        savedSearches21.create(searchName21, search);
        assertTrue(assertRoot + "#44",
            savedSearches21.containsKey(searchName21));
        savedSearches11.refresh();
        assertFalse(assertRoot + "#45",
            savedSearches11.containsKey(searchName21));
        savedSearches12.refresh();
        assertFalse(assertRoot + "#46",
            savedSearches12.containsKey(searchName21));
        savedSearches22.refresh();
        assertFalse(assertRoot + "#47",
            savedSearches22.containsKey(searchName21));

        // create in 22 namespace, make sure there, but not in others
        savedSearches22.create(searchName22, search);
        assertTrue(assertRoot + "#48",
            savedSearches22.containsKey(searchName22));
        savedSearches11.refresh();
        assertFalse(assertRoot + "#49",
            savedSearches11.containsKey(searchName22));
        savedSearches12.refresh();
        assertFalse(assertRoot + "#50",
            savedSearches12.containsKey(searchName22));
        savedSearches21.refresh();
        assertFalse(assertRoot + "#51",
            savedSearches21.containsKey(searchName22));

        /* now remove the UNIQUE saved searches */
        savedSearches11.remove(searchName11);
        savedSearches12.remove(searchName12);
        savedSearches21.remove(searchName21);
        savedSearches22.remove(searchName22);
        assertFalse(assertRoot + "#52",
            savedSearches11.containsKey(searchName11));
        assertFalse(assertRoot + "#53",
            savedSearches12.containsKey(searchName12));
        assertFalse(assertRoot + "#54",
            savedSearches21.containsKey(searchName21));
        assertFalse(assertRoot + "#55",
            savedSearches22.containsKey(searchName22));

        /* create same search name in different namespaces */
        savedSearches11.create("sdk-test-search", search + " | head 1");
        savedSearches21.create("sdk-test-search", search + " | head 2");
        savedSearchesNobody1.create("sdk-test-search", search + " | head 4");
        assertTrue(assertRoot + "#56",
            savedSearches11.containsKey("sdk-test-search"));
        assertTrue(assertRoot + "#57",
            savedSearches21.containsKey("sdk-test-search"));
        assertTrue(assertRoot + "#58",
            savedSearchesNobody1.containsKey("sdk-test-search"));

        // we have created three saved searches with the same name, make sure we
        // can see all three with a wild-carded get.
        savedSearchesx1.refresh();
        assertEquals(assertRoot + "#59", 3, savedSearchesx1.values().size());

        assertFalse(assertRoot + "#60",
            savedSearchesx1.containsKey("sdk-test-search", namespaceBad));
        assertTrue(assertRoot + "#61",
            savedSearchesx1.containsKey("sdk-test-search", namespace21));
        assertTrue(assertRoot + "#62",
            savedSearchesx1.get("sdk-test-search", namespace21) != null);

        // remove one of the saved searches through a specific namespace path
        savedSearchesx1.remove("sdk-test-search", namespace21);
        savedSearches11.remove("sdk-test-search");
        savedSearchesNobody1.remove("sdk-test-search");
        assertFalse(assertRoot + "#63",
            savedSearches11.containsKey("sdk-test-search"));
        savedSearches21.refresh();
        assertFalse(assertRoot + "#64",
            savedSearches21.containsKey("sdk-test-search"));
        assertFalse(assertRoot + "#65",
            savedSearchesNobody1.containsKey("sdk-test-search"));

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
        assertFalse(assertRoot + "#66", apps.containsKey(appname1));
        assertFalse(assertRoot + "#67", apps.containsKey(appname2));

        /* cleanup users */
        users = service.getUsers(); // need to re-establish, because of restart
        if (users.containsKey(username1))
            users.remove(username1);
        if (users.containsKey(username2))
            users.remove(username2);
        assertFalse(assertRoot + "#68", users.containsKey(username1));
        assertFalse(assertRoot + "#69", users.containsKey(username2));
    }
}

