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
 * The {@code DeploymentServer} class represents a Splunk deployment server,
 * and provides access to the configurations of all deployment servers.
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
     * Returns inclusive criteria for determining deployment client access to
     * this deployment server.
     *
     * @param index The index of the whitelist entry to return.
     * @return A list of included client addresses, or {@code null} if not
     * specified.
     */
    public String getWhitelistByIndex(int index) {
        return getString(String.format("whitelist.%d", index), null);
    }

    /**
     * Sets whether the deployment server is enabled or disabled.
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this 
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     * @see Entity#disable
     * @see Entity#enable
     *
     * @param disabled {@code true} to disable the deployment client,
     * {@code false} to enable it.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }
}
