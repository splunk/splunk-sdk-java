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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Parent class that all other endpoints classes will derive from
 */

public class Client {

    public Service service = null;

    public Client() {
        // default constructor
    }
    public Client(Service serv) {
        service = serv;
    }

    // UNDONE: need to normalize paths, and moved to individual classes (in progress)
    private final String PATH_CAPABILITIES = "authorization/capabilities/";
    private final String PATH_CONF = "configs/conf-%s/"; // (config file)
    private final String PATH_CONFS = "properties/";
    private final String PATH_INPUTS = "data/inputs/";
    private final String PATH_JOBS = "search/jobs/";
    private final String PATH_LOGGER = "server/logger/";
    private final String PATH_MESSAGES = "messages/";
    private final String PATH_ROLES = "authentication/roles/";
    private final String PATH_STANZA = "configs/conf-%s/%s"; // (config file, stanza)
    private final String PATH_USERS = "authentication/users/";
    private final String PATH_DEP_SERV = "deployment/server/";
    private final String PATH_DEP_CLI = "deployment/client/";
    private final String PATH_DEP_SERV_CL = "deployment/serverclass/";
    private final String PATH_DEP_TENANT = "deployment/tenants/";
    private final String PATH_DSEARCH_PEER = "search/distributed/peers/";
    private final String PATH_DSEARCH_CONF = "search/distributed/config/";

    private final String XNAME_ENTRY = "{http://www.w3.org/2005/Atom}entry";
    private final String XNAME_CONTENT = "{http://www.w3.org/2005/Atom}content";

    private ArrayList<String> getList(Document doc) {
        ArrayList<String> outlist = new ArrayList<String>();
        NodeList nl = doc.getElementsByTagName("title");

        // index 0 is always header title
        for (int idx=1; idx < nl.getLength(); idx++) {
            outlist.add(nl.item(idx).getTextContent());
        }

        return outlist;
    }

    public Entity get(String path) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.get(path).getContent());
    }

    public Entity delete(String path) throws Exception {
        Convert converter = new Convert();
        // place holder -- service needs a delete method
        return converter.convertXMLData(service.get/*deletegoeshere*/(path).getContent());
    }

    public ArrayList<String> list(String path) throws Exception {
        Document doc = service.parseXml(service.get(path));
        ArrayList<String> applist = getList(doc);
        return applist;
    }
}
