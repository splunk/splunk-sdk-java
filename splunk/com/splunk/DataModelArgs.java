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

/**
 * Class representing arguments to creating a data model.
 *
 * At the moment this is a very minimal class, supporting only the
 * description field for writing raw JSON to the server.
 */
public class DataModelArgs extends Args {
    public String getRawJsonDescription() { return (String) get("description"); }
    public void setRawJsonDescription(String rawJson) { put("description", rawJson); }
}
