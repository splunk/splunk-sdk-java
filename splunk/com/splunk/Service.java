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

// UNDONE: storage/passwords
// UNDONE: getAlerts
// UNDONE: public Object parse(String query) {}
// UNDONE: public void restart() {}

package com.splunk;

import com.splunk.http.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Service extends com.splunk.http.Service {
    String token = null;

    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public EntityCollection getApplications() {
        return new EntityCollection(
            this, "/services/apps/local", Application.class);
    }

    public EntityCollection getConfigurations() {
        return null; // UNDONE
    }

    public List<String> getCapabilities() {
        Entity caps = Entity.read(this, "/services/authorization/capabilities");
        return (List<String>)caps.getValue("capabilities");
    }

    public Entity getDeploymentClient() {
        return Entity.read(this, "/services/deployment/client");
    }

    public EntityCollection getDeploymentServers() {
        return new EntityCollection(this, "/services/deployment/server");
    }

    public EntityCollection getDeploymentServerClasses() {
        return new EntityCollection(this, "/services/deployment/serverclass");
    }

    public EntityCollection getDeploymentTenants() {
        return new EntityCollection(this, "/services/deployment/tenants");
    }

    public EntityCollection getEventTypes() {
        return new EntityCollection(this, "/services/saved/eventtypes");
    }

    public EntityCollection getIndexes() {
        return new EntityCollection(this, "/services/data/indexes", Index.class);
    }

    public Entity getInfo() {
        return Entity.read(this, "/services/server/info");
    }

    public EntityCollection getInputs() {
        return null; //  UNDONE: flatten?
    }

    public EntityCollection getJobs() {
        return new EntityCollection(this, "/services/search/jobs", Job.class);
    }

    public EntityCollection getLicenseGroups() {
        return new EntityCollection(this, "/services/licenser/groups");
    }

    public EntityCollection getLicenseMessages() {
        return new EntityCollection(this, "/services/licenser/messages");
    }

    public EntityCollection getLicensePools() {
        return new EntityCollection(this, "/services/licenser/pools");
    }

    public EntityCollection getLicenseSlaves() {
        return new EntityCollection(this, "/services/licenser/slaves");
    }

    public EntityCollection getLicenseStacks() {
        return new EntityCollection(this, "/services/licenser/stacks");
    }

    public EntityCollection getLicenses() {
        return new EntityCollection(this, "/services/licenser/licenses");
    }

    public EntityCollection getLoggers() {
        return new EntityCollection(this, "/services/server/logger");
    }

    public Object getMessages() {
        return null; // UNDONE
    }

    // UNDONE: getOutputs

    public EntityCollection getRoles() {
        return new EntityCollection(this, "/services/authentication/roles");
    }

    public EntityCollection getSearches() {
        return new EntityCollection(this, "/services/saved/searches");
    }

    public Object getSettings() {
        return null; // UNDONE
    }

    public EntityCollection getUsers() {
        return new EntityCollection(this, "/services/authentication/users");
    }

    public Service login(String username, String password) {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("username", username);
        args.put("password", password);
        ResponseMessage response = super.post("/services/auth/login/", args);
        // UNDONE: Check status
        String sessionKey = parseXml(response)
            .getElementsByTagName("sessionKey")
            .item(0)
            .getTextContent();
        this.token = "Splunk " + sessionKey;
        return this;
    }

    // Forget the session token
    public Service logout() {
        this.token = null;
        return this;
    }

    // Returns the response content as an XML DOM.
    // UNDONE: The following helper is only used by the login method, should
    // find a way to combine this with a similar helper in com.splunk.atom.
    public Document parseXml(ResponseMessage response) {
        try {
            InputStream content = response.getContent();
            DocumentBuilderFactory factory = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new InputStreamReader(content));
            return builder.parse(inputSource);
        }
        catch (Exception e) {
            // UNDONE: SplunkException
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseMessage send(RequestMessage request) {
        request.getHeader().put("Authorization", token);
        return super.send(request);
    }

    // UNDONE
    public Object streamConnect(RequestMessage request) throws IOException {
        request.getHeader().put("Authorization", token);
        return (Object)super.streamConnect(request);
    }
}

