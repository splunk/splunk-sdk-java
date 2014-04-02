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

import java.util.Collection;
import java.util.Map;

/**
 * Represents a lookup calculation on a data model object.
 *
 * A lookup calculation takes the value of an input field and uses it to
 * look up new values for the output fields in a lookup.
 */
public class LookupCalculation extends Calculation {
    private final String lookupFieldName;
    private final String inputField;
    private final String lookupName;

    LookupCalculation(String[] ownerLineage, String calculationID,
                             Map<String, Field> generatedFields, String comment,
                             boolean editable, String lookupName,
                             String lookupFieldName, String inputField) {
        super(ownerLineage, calculationID, generatedFields, comment, editable);
        this.lookupName = lookupName;
        this.lookupFieldName = lookupFieldName;
        this.inputField = inputField;
    }

    /**
     * @return the name of the lookup to use
     */
    public String getLookupName() { return this.lookupName; }

    /**
     * @return the name of the field to use in the lookup
     */
    public String getLookupFieldName() { return this.lookupFieldName; }

    /**
     * @return the field on the input to lookup with
     */
    public String getInputField() { return this.inputField; }
}
