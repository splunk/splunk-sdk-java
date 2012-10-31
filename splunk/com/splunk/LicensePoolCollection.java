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

import java.util.Map;

/**
 * The {@code LicensePoolCollection} class represents a collection of license
 * pools.
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
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     */
    LicensePoolCollection(Service service, Args args) {
        super(service, "licenser/pools", LicensePool.class, args);
    }

    /**
     * Creates a license pool.
     *
     * @param name The name of the new license pool.
     * @param quota The indexing quota of this license pool, specified as:
     * <ul><li><i>number</i></li>
     * <li><i>number</i> followed by "MB" or "GB" (for example, "10GB")</li>
     * <li>"MAX" (Only one license pool can have "MAX" size in a stack.)</li>
     * </ul>
     * @param stackId The stack ID corresponding to this license pool.
     * @return The new license pool.
     */
    public LicensePool create(String name, String quota, String stackId) {
        return create(name, quota, stackId, null);
    }

    /**
     * Creates a license pool.
     *
     * @param name The name of the new license pool.
     * @param quota The indexing quota of this license pool, specified as:
     * <ul><li><i>number</i></li>
     * <li><i>number</i> followed by "MB" or "GB" (for example, "10GB")</li>
     * <li>"MAX" (Only one license pool can have "MAX" size in a stack.)</li>
     * </ul>
     * @param stackId The stack ID corresponding to this license pool.
     * @param args Optional arguments ("description" and "slaves").
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
