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

public class DeploymentClient extends Entity {
    DeploymentClient(Service service) {
        super(service, "deployment/client");
    }

    @Override protected String actionPath(String action) {
        if (action.equals("edit"))
            return path + "/deployment-client";
        return super.actionPath(action);
    }

    @Override public void disable() {
        // disable is not handled through the standard enable/disable action
        // paths; rather it is an edit (i.e. update) action path.
        Args args = new Args("disabled", true);
        update(args);
    }

    @Override public void enable() {
        // enable is not handled through the standard enable/disable action
        // path; rather it is an edit (i.e. update) action path.
        Args args = new Args("disabled", false);
        update(args);
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
        service.get(path + "/deployment-client/Reload");
        invalidate();
    }
}

