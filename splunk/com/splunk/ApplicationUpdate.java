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

package com.splunk;

public class ApplicationUpdate extends Entity {
    public ApplicationUpdate(Service service, String path) {
        super(service, path);
    }

    public String getChecksum() {
        return getString("update.checksum", null);
    }

    public String getChecksumType() {
        return getString("update.checksum.type", null);
    }

    public String getHomepage() {
        return getString("update.homepage", null);
    }

    public String getUpdateName() {
        return getString("update.name", null);
    }

    public int getSize() {
        return getInteger("update.size", -1);
    }

    public String getVersion() {
        return getString("update.version", null);
    }

    public boolean isImplicitIdRequired() {
        return getBoolean("update.implicit_id_required", false);
    }

}