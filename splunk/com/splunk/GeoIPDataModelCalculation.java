/*
 * Copyright 2014 Splunk, Inc.
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
 * Represents a GeoIP lookup on a data model object, which uses an input
 * field to add geographic information to the output.
 */

public class GeoIPDataModelCalculation extends DataModelCalculation {
    private final String inputField;

    GeoIPDataModelCalculation(String[] ownerLineage, String calculationID,
                              Map<String, DataModelField> generatedFields, String comment,
                              boolean editable, String inputField) {
        super(ownerLineage, calculationID, generatedFields, comment, editable);
        this.inputField = inputField;
    }

    /**
     * @return field to do GeoIP lookup on
     */
    public String getInputField() { return this.inputField; }
}
