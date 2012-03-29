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
 * The {@code DeploymentServer} class represents a Splunk deployment server, and provides
 * access to the configurations of all deployment servers.
 */
public class DeploymentServer extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The deployment server endpoint.
     */
    DeploymentServer(Service service, String path) {
        super(service, path);
    }

    /**
     * Indicates whether this deployment server reviews its configuration
     * information and informs the deployment client if something is new or
     * updated.
     *
     * @return {@code true} if this deployment server notifies the deployment
     * client of new or changed configurations, {@code false} if not.
     */
    public boolean getCheckNew() {
        return getBoolean("check-new", false);
    }

    /**
     * Returns inclusive criteria for determining deployment client access to
     * this deployment server.
     *
     * @return Criteria for determining deployment client access to this
     * deployment server.
     */
    public String getWhiteList0() {
        return getString("whitelist.0");
    }
}
