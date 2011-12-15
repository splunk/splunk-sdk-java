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

/**
 * Representation of a Splunk deployment tenant.
 */
public class DeploymentTenant extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The deployment tenant endpoint.
     */
    DeploymentTenant(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns whether or not this deployment server should review its
     * configuration, in a multi-tenant configuration, and inform the deployment
     * client if something is new or updated.
     *
     * @return Whether this deployment server notifies the deployment client for
     * new or changed configurations.
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

