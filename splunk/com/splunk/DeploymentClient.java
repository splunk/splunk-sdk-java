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
    public DeploymentClient(Service service) {
        super(service, "deployment/client");
    }

    public void disable() {
        // disable is not handled through the standard enable/disable action
        // paths; rather it is an edit (i.e. update) action path.
        Args args = new Args();
        args.put("disabled", "true");
        super.update(args);
    }

    public void enable() {
        // enable is not handled through the standard enable/disable action
        // path; rather it is an edit (i.e. update) action path.
        Args args = new Args();
        args.put("disabled", "false");
        super.update(args);
    }

    public String [] getServerClasses() {
        return getStringArray("serverClasses", null);
    }

    public String getTargetUri() {
        return getString("targetUri", null);
    }

    void load(AtomEntry entry) {
        super.load(entry);
        if (entry == null)
            setTitle("deploymentclient");
    }

    public void reload() {
        get("deployment-client/Reload");
        invalidate();
    }
}

