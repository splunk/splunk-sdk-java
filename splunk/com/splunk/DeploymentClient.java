/*
 * Copyright 2012 Splunk, Inc.
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

/**
 * The {@code DeploymentClient} class represents a Splunk deployment client, providing access
 * to deployment client configuration and status.
 */
public class DeploymentClient extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    DeploymentClient(Service service) {
        super(service, "deployment/client");
    }

    /**
     * Displays the action path.
     *
     * @param action The requested action.
     * @return The action path.
     */
    @Override protected String actionPath(String action) {
        if (action.equals("edit"))
            return path + "/deployment-client";
        return super.actionPath(action);
    }

    /**
     * Disables the deployment client.
     */
    @Override public void disable() {
        /*
         * {@code disable} is not handled through the standard enable/disable action
         * paths; rather it is an edit (i.e. update) action path.
         */
        Args args = new Args("disabled", true);
        update(args);
    }

    /**
     * Enables the deployment client.
     */
    @Override public void enable() {
        /*
         * {@code enable} is not handled through the standard enable/disable action
         * path; rather it is an edit (i.e. update) action path.
         */
        Args args = new Args("disabled", false);
        update(args);
    }

    /**
     * Returns the list of server classes.
     *
     * @return The list of server classes, or {@code null} if not specified.
     */
    public String [] getServerClasses() {
        return getStringArray("serverClasses", null);
    }

    /**
     * Returns the target URI of the deployment server for this deployment
     * client.
     *
     * @return The target URI of the deployment server in the 
     * format "server:port", or {@code null} if not specified.
     */
    public String getTargetUri() {
        return getString("targetUri", null);
    }

    /**
     * Reloads the deployment client from the configuration file.
     */
    public void reload() {
        service.get(path + "/deployment-client/Reload");
        invalidate();
    }
}

