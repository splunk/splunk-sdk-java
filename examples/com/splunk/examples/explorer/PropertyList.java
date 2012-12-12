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

import java.util.ArrayList;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

public class PropertyList extends ArrayList<PropertyInfo> {
    public void add(Class datatype, String getter) {
        add(datatype, getter, null);
    }

    public void add(Class datatype, String getter, String setter) {
        add(new PropertyInfo(datatype, getter, setter));
    }

    public Sheet createSheet(Object object) {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set props = Sheet.createPropertiesSet();
        try {
            for (PropertyInfo info : this) {
                props.put(new PropertySupport.Reflection(
                    object, info.datatype, info.getter, info.setter));
            }
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sheet.put(props);
        return sheet;
    }
}
