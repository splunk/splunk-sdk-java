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
 * The {@code DeploymentTenant} class represents a Splunk deployment tenant, and 
 * provides access to the multi-tenants configuration for this Splunk instance. 
 */
public class DeploymentTenant extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The deployment tenant endpoint.
     */
    DeploymentTenant(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns inclusive criteria for determining deployment client access to
     * this deployment server.
     *
     * @param index The whitelist index. The only valid index is 0.
     * @return Criteria for determining deployment client access to this
     * deployment server.
     */
    public String getWhitelistByIndex(int index) {
        return getString(String.format("whitelist.%d", index), null);
    }

    /**
     * Sets whether the deployment tenant is enabled or disabled.
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this 
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     * @see Entity#disable
     * @see Entity#enable
     *
     * @param disabled {@code true} to disabled to deployment client,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }
}

