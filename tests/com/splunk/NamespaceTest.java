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

import java.net.URLEncoder;

public class NamespaceTest extends SDKTestCase {
    @Test
    public void testStaticNamespace() {
        assertEquals(
                "This test is not valid when Service owner is overridden.",
                null, service.getOwner());
        assertEquals(
                "This test is not valid when Service app is overridden.",
                null, service.getApp());
        
        Args namespace = new Args();

        // syntactic tests
        namespace.clear();
        assertEquals("/services/",
            service.fullpath("", null));

        namespace.clear();
        namespace.put("owner", "bill@some domain\u0150");
        namespace.put("app", "my! app!@");
        assertEquals("/servicesNS/bill%40some+domain%C5%90/my%21+app%21%40/",
                service.fullpath("", namespace));

        namespace.clear();
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/Bob/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("app", "search");
        assertEquals("/servicesNS/-/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/Bob/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        assertEquals("/servicesNS/-/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/Bob/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        assertEquals("/servicesNS/-/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "user");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/Bob/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        assertEquals("/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        assertEquals("/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "app");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        assertEquals("/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/-/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        assertEquals("/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "global");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/search/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        assertEquals("/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        assertEquals("/servicesNS/nobody/system/",
            service.fullpath("", namespace));

        namespace.clear();
        namespace.put("sharing", "system");
        namespace.put("app", "search");
        namespace.put("owner", "Bob");
        assertEquals("/servicesNS/nobody/system/",
            service.fullpath("", namespace));
    }

    @Test
    public void testLiveNamespace1() throws Exception {
        String username = "sdk-user@somedomain!";
        String password = "changeme";
        String savedSearch = "sdk-test1";
        String searchString = "search index=main * | head 10";

        // Setup a namespace
        Args namespace = new Args();
        namespace.put("owner", username);
        namespace.put("app", "search");

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

    // Check that a saved search created in one user namespace is invisible in another.
    @Test
    public void testNamespaceVisibility() {
        final String savedSearchName = createTemporaryName();
        final String query = "search *";

        final String user = "admin";
        final String app = "search";

        Args namespace = new Args();
        namespace.put("owner", user);
        namespace.put("app", app);

        Args otherUserNamespace = new Args();
        otherUserNamespace.put("owner", user);
        otherUserNamespace.put("app", "launcher");

        Args appNamespace = new Args();
        appNamespace.put("owner", "nobody");
        appNamespace.put("app", app);

        Args wildcardUserNamespace = new Args();
        wildcardUserNamespace.put("owner", "-");
        wildcardUserNamespace.put("app", app);

        try {
            SavedSearch ss = service.getSavedSearches(namespace).create(savedSearchName, query);

            assertTrue(service.getSavedSearches(namespace).containsKey(savedSearchName));
            assertFalse(service.getSavedSearches(otherUserNamespace).containsKey(savedSearchName));
            assertFalse(service.getSavedSearches(appNamespace).containsKey(savedSearchName));
            assertTrue(service.getSavedSearches(wildcardUserNamespace).containsKey(savedSearchName));
        } finally {
            if (service.getSavedSearches(namespace).containsKey(savedSearchName)) {
                service.getSavedSearches(namespace).remove(savedSearchName);
            }
        }
    }

    @Test
    public void testNamespaceConflicts() {
        final String user = "admin";
        final String app1 = "search";
        final String app2 = "launcher";

        Args namespace1 = new Args();
        namespace1.put("owner", user);
        namespace1.put("app", app1);

        Args namespace2 = new Args();
        namespace2.put("owner", user);
        namespace2.put("app", app2);

        Args wildcardNamespace = new Args();
        wildcardNamespace.put("owner", user);
        wildcardNamespace.put("app", "-");

        final String savedSearchName = createTemporaryName();
        final String query1 = "search * | head 1";
        final String query2 = "search * | head 2";

        try {
            SavedSearch ss1 = service.getSavedSearches(namespace1).create(savedSearchName, query1);
            SavedSearch ss2 = service.getSavedSearches(namespace2).create(savedSearchName, query2);

            assertEquals(query1, service.getSavedSearches(namespace1).get(savedSearchName).getSearch());
            assertEquals(query2, service.getSavedSearches(namespace2).get(savedSearchName).getSearch());

            try {
                service.getSavedSearches(wildcardNamespace).get(savedSearchName).getSearch();
                fail("Expected SplunkException about multiple keys.");
            } catch (SplunkException e) {

            }
        } finally {
            if (service.getSavedSearches(namespace1).containsKey(savedSearchName)) {
                service.getSavedSearches(namespace1).remove(savedSearchName);
            }

            if (service.getSavedSearches(namespace2).containsKey(savedSearchName)) {
                service.getSavedSearches(namespace2).remove(savedSearchName);
            }
        }


    }
}
