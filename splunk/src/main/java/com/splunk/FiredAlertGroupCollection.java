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
 * The {@code FiredAlertGroupCollection} class represents a collection of fired 
 * alert groups.
 */
public class FiredAlertGroupCollection
    extends EntityCollection<FiredAlertGroup>{

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    FiredAlertGroupCollection(Service service) {
        super(service, "alerts/fired_alerts", FiredAlertGroup.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them (see {@link CollectionArgs}).
     */
    FiredAlertGroupCollection(Service service, Args args) {
        super(service, "alerts/fired_alerts", FiredAlertGroup.class, args);
    }
}
