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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class UploadTest extends SDKTestCase {
    @Test
    public void testOneshot() throws IOException {
        String filename = locateSystemLog();
        if (System.getenv("TRAVIS_CI") != null) {
            File tempfile = File.createTempFile((new Date()).toString(), "");
            tempfile.deleteOnExit();

            FileWriter f = new FileWriter(tempfile, true);
            f.append("some data here");

            filename = tempfile.getAbsolutePath();
        }
        else if (System.getenv("SPLUNK_HOME") != null) {
            filename = System.getenv("SPLUNK_HOME") + "/var/log/splunk/splunkd.log";
        }
        service.getUploads().create(filename);
        
        for (Upload oneshot : service.getUploads().values()) {
            oneshot.getBytesIndexed();
            oneshot.getOffset();
            oneshot.getSize();
            oneshot.getSize();
            oneshot.getSourcesIndexed();
            oneshot.getSpoolTime();
        }
    }
}
