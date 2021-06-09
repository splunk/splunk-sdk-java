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

import com.google.gson.JsonObject;

/**
 * Represents a column split on a timestamp valued field in a pivot.
 */
public class TimestampPivotColumnSplit extends PivotColumnSplit {
    private final TimestampBinning binning;

    TimestampPivotColumnSplit(DataModelObject owner, String fieldName, TimestampBinning binning) {
        super(owner, fieldName);
        this.binning = binning;
    }

    public TimestampBinning getBinning() { return this.binning; }

    @Override
    JsonObject toJson() {
        JsonObject root = new JsonObject();
        addCommonFields(root);
        root.addProperty("period", binning.toString());
        return root;
    }
}
