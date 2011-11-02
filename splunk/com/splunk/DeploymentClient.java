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

import com.splunk.atom.AtomEntry;
import com.splunk.atom.AtomFeed;
import com.splunk.http.ResponseMessage;

public class DeploymentClient extends Entity {
    public DeploymentClient(Service service, String path) {
        super(service, path);
    }

    public String getTargetUri() {
        return getString("targetUri");
    }

    public void reload() {
        super.get("deployment-client/reload");
        invalidate();
    }

    // this really shouldn't be here -- unsure if there is a better way
    static DeploymentClient read(Service service, String path) {
        ResponseMessage response = service.get(path);
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.parse(response.getContent());
        int count = feed.entries.size();
        if (count == 0) return null;
        assert(count == 1);
        AtomEntry entry = feed.entries.get(0);
        DeploymentClient result = new DeploymentClient(service, path);
        result.load(entry);
        return result;
    }
}

