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
 * Represents a calculation on a data model object done by a regular expression.
 */
public class RegexpDataModelCalculation extends DataModelCalculation {
    private final String expression;
    private final String inputField;

    RegexpDataModelCalculation(String[] owner, String calculationID,
                               Map<String, DataModelField> generatedFields, String comment,
                               boolean editable, String inputField, String expression) {
        super(owner, calculationID, generatedFields, comment, editable);
        this.expression = expression;
        this.inputField = inputField;
    }

    /**
     * @return regular expression specifying this calculation
     */
    public String getExpression() { return this.expression; }

    /**
     * @return name of the field to apply the regular expression to.
     */
    public String getInputField() { return this.inputField; }
}
