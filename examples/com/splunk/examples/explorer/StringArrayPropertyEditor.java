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

package com.splunk.examples.explorer;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;

public class StringArrayPropertyEditor extends PropertyEditorSupport {
    @Override public String getAsText() {
        String[] value = (String[])getValue();
        if (value == null) return "null";
        return Arrays.toString(value);
    }

    @Override public void setAsText(String value) {
        throw new UnsupportedOperationException();
    }
}

