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

import java.io.IOException;
import java.util.List;

public class Service extends com.splunk.Service {
    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public EntityCollection getApplications() {
        return new EntityCollection(this, "/services/apps/local");
    }

    public EntityCollection getConfigurations() {
        return null; // UNDONE
    }

    public List<String> getCapabilities() {
        return null; // UNDONE
    }

    public EntityCollection getIndexes() {
        return new EntityCollection(this, "/services/data/indexes");
    }

    public Object getInfo() {
        return null; // UNDONE
    }

    public EntityCollection getInputs() {
        return null; //  UNDONE: flatten?
    }

    public EntityCollection getJobs() {
        return new EntityCollection(this, "/services/search/jobs");
    }

    public EntityCollection getLoggers() {
        return new EntityCollection(this, "/services/server/logger");
    }

    public Object getMessages() {
        return null; // UNDONE
    }

    public EntityCollection getRoles() {
        return new EntityCollection(this, "/services/authentication/roles");
    }

    public Object getSettings() {
        return null; // UNDONE
    }

    public EntityCollection getUsers() {
        return new EntityCollection(this, "/services/authentication/users");
    }

    // public Object parse(String query) {}
}

