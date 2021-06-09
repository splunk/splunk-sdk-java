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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a lookup calculation on a data model object.
 *
 * A lookup calculation takes the value of an input field and uses it to
 * look up new values for the output fields in a lookup.
 */
public class LookupDataModelCalculation extends DataModelCalculation {
    private final List<LookupFieldMapping> inputFieldMappings;
    private final String lookupName;

    public static class LookupFieldMapping {
        public String inputField;
        public String lookupField;
    }

    LookupDataModelCalculation(String[] ownerLineage, String calculationID,
                               Map<String, DataModelField> generatedFields, String comment,
                               boolean editable, String lookupName,
                               List<LookupFieldMapping> inputFieldMappings) {
        super(ownerLineage, calculationID, generatedFields, comment, editable);
        this.lookupName = lookupName;
        this.inputFieldMappings = inputFieldMappings;
    }

    /**
     * @return the name of the lookup to use
     */
    public String getLookupName() { return this.lookupName; }

    /**
     * @return the mappings from fields in the events to fields in the lookup
     */
    public List<LookupFieldMapping> getInputFieldMappings() {
        return Collections.unmodifiableList(this.inputFieldMappings);
    }
}
