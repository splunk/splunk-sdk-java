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

import java.util.Map;

/**
 * Representation of the Collection of License Pools.
 */
public class LicensePoolCollection extends EntityCollection<LicensePool> {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     */
    LicensePoolCollection(Service service) {
        super(service, "licenser/pools", LicensePool.class);
    }

    /**
     * Creates a license pool.
     *
     * Quota can be MAX (Only one pool can use MAX size), a number or a number
     * followed by MB or GB.
     *
     * @param name The name of this new license pool.
     * @param quota The quota in bytes of this license pool.
     * @param stackId The stack ID corresponding to this license pool.
     * @return The new license pool.
     */
    public LicensePool create(String name, int quota, String stackId) {
        return create(name, quota, stackId, null);
    }

    /**
     * Creates a license pool.
     *
     * Quota can be MAX (Only one pool can use MAX size), a number or a number
     * followed by MB or GB.
     *
     * @param name The name of this new license pool.
     * @param quota The quota in bytes of this license pool.
     * @param stackId The stack ID corresponding to this license pool.
     * @param args Optional arguments.
     * @return The new license pool.
     */
    public LicensePool 
    create(String name, int quota, String stackId, Map args) {
        args = Args.create(args);
        args.put("quota", quota);
        args.put("stack_id", stackId);
        return create(name, args);
    }
}
