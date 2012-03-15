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

import org.junit.Test;

public class UploadTest extends SplunkTestCase {
    @Test public void testOneshot() {

        Service service = connect();

        ServiceInfo info = service.getInfo();
        String filename;
        if (info.getOsName().equals("Windows"))
            filename = "C:\\Windows\\WindowsUpdate.log"; // normally here
        else if (info.getOsName().equals("Linux"))
            filename = "/var/log/syslog";
        else if (info.getOsName().equals("Darwin")) {
            filename = "/var/log/system.log";
        } else {
            throw new Error("OS: " + info.getOsName() + " not supported");
        }

        service.getUploads().create(filename);
        EntityCollection<Upload> oneshots = service.getUploads();

        for (Upload oneshot: oneshots.values()) {
            oneshot.getBytesIndexed();
            oneshot.getOffset();
            oneshot.getSize();
            oneshot.getSize();
            oneshot.getSourcesIndexed();
            oneshot.getSpoolTime();
        }
    }
}