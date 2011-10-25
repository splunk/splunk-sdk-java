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

import java.util.HashMap;
import java.util.Map;

// UNDONE: testing. may need to perform a get after posts to return
// same element (as opposed to parent element).
public class Job extends Entity {

    public Job(Service service, String name) {
        super(service, "/services/search/jobs/" + name);
    }

    public Job cancel() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "cancel");
        super.post("control", args);
        return this;
    }

    public Job disable_preview() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "disablepreview");
        super.post("control", args);
        return this;
    }

    public Job enable_preview() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "enablepreview");
        super.post("control", args);
        return this;
    }

    public Job finalise() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "finalize");
        super.post("control", args);
        return this;
    }

    public Job pause() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "pause");
        super.post("control", args);
        return this;
    }

    public Job unpause() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "unpause");
        super.post("control", args);
        return this;
    }

    public Job setpriority(int priority ) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "setpriority");
        args.put("priority", Integer.toString(priority));
        super.post("control", args);
        return this;
    }

    public Job touch() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "touch");
        super.post("control", args);
        return this;
    }

    public Job setttl(int ttl ) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "setttl");
        args.put("ttl", Integer.toString(ttl));
        super.post("control", args);
        return this;
    }

    /*  UNDONE:

    def events(self, **kwargs):
        return self.get("events", **kwargs).body

    def preview(self, **kwargs):
        return self.get("results_preview", **kwargs).body

    def read(self, *args):
        response = self.get()
        content = load(response).entry.content
        return _filter_content(content, *args)

    def results(self, **kwargs):
        return self.get("results", **kwargs).body

    def searchlog(self, **kwargs):
        return self.get("search.log", **kwargs).body

    def summary(self, **kwargs):
        return self.get("summary", **kwargs).body

    def timeline(self, **kwargs):
        return self.get("timeline", **kwargs).body

     */
}
