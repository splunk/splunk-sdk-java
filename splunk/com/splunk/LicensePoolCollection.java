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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code LicensePoolCollection} class represents a collection of license pools.
 */
public class LicensePoolCollection extends EntityCollection<LicensePool> {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    LicensePoolCollection(Service service) {
        super(service, "licenser/pools", LicensePool.class);
    }

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    LicensePoolCollection(Service service, Args args) {
        super(service, "licenser/pools", LicensePool.class, args);
    }

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param namespace This collection's namespace.
     */
    LicensePoolCollection(Service service, HashMap<String, String> namespace) {
        super(service, "licenser/pools", LicensePool.class, namespace);
    }

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param args Arguments use at instantiation, such as count and offset.
     * @param namespace This collection's namespace.
     */
    LicensePoolCollection(
            Service service, Args args, HashMap<String, String> namespace) {
        super(service, "licenser/pools", LicensePool.class, args, namespace);
    }

    /**
     * Creates a license pool.
     *
     * Quota can be one of the following: a number, a number followed by MB or
     * GB, or "MAX" (only one license pool in a license stack can be set
     * to "MAX").
     *
     * @param name The name of the new license pool.
     * @param quota The indexing quota of this license pool. This can also be
     *              a literal MAX or number followed by MB or GB. For example
     *              10GB.
     * @param stackId The stack ID corresponding to this license pool.
     * @return The new license pool.
     */
    public LicensePool create(String name, String quota, String stackId) {
        return create(name, quota, stackId, null);
    }

    /**
     * Creates a license pool.
     *
     * Quota can be one of the following: a number, a number followed by MB or
     * GB, or "MAX" (only one license pool in a license stack can be set
     * to "MAX").
     *
     * @param name The name of the new license pool.
     * @param quota The indexing quota of this license pool. This can also be
     *              a literal MAX or number followed by MB or GB. For example
     *              10GB.
     * @param stackId The stack ID corresponding to this license pool.
     * @param args Optional arguments.
     * @return The new license pool.
     */
    public LicensePool 
    create(String name, String quota, String stackId, Map args) {
        args = Args.create(args);
        args.put("quota", quota);
        args.put("stack_id", stackId);
        return create(name, args);
    }
}
