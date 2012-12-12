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
 * The {@code IndexCollection} class represents a collection of indexes.
 */
public class IndexCollection extends EntityCollection<Index> {
    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    IndexCollection(Service service) {
        super(service, "data/indexes", Index.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them (see {@link IndexCollectionArgs}).
     */
    IndexCollection(Service service, Args args) {
        super(service, "data/indexes", Index.class, args);
    }

    /**
     * {@inheritDoc}
     */
    public Index remove(String key) {
        if (this.service.versionCompare("5.0") < 0) {
            throw new UnsupportedOperationException(
                    "Indexes cannot be deleted via the REST API in versions " +
                    "prior to 5.0");
        } else {
            return (Index)super.remove(key);
        }
    }
}
