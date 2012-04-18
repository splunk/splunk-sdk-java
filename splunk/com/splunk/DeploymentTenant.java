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
     * Indicates whether this deployment server in a multi-tenant configuration
     * should review its configuration and inform the deployment client if 
     * something is new or updated.
     *
     * @return {@code true} if this deployment server notifies the deployment
     * client for new or changed configurations, {@code false} if not.
     */
    public boolean getCheckNew() {
        return getBoolean("check-new", false);
    }

    /**
     * Returns inclusive criteria for determining deployment client access to
     * this deployment server. The index is form 0 to 9 inclusive.
     *
     * @param index The whitelist index.
     * @return Criteria for determining deployment client access to this
     * deployment server.
     */
    public String getWhiteListByIndex(int index) {
        return getString(String.format("whitelist.%d", index));
    }

    /**
     * Sets whether the deployment tenant is enabled or disabled. Note that
     * this effect is not immediate; Splunk must be restarted to take effect.
     *
     * Note that the supported disabled mechanism, is to use the
     * @{code disable} and {@code enable} action.
     *
     * @param disabled {@code true} to disabled to deployment client,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets whether this deployment server reviews the information in its
     * configuration to find out if there is something new or updated to push
     * out to its deployment clients. If {@code true} this deployment server
     * reviews the information, {@code false} and this deployment server does
     * not review the information.
     *
     * @param checkNew Whether or not the information is reviewed.
     */
    public void setCheckNew(boolean checkNew) {
        setCacheValue("check-new", checkNew);
    }
}

