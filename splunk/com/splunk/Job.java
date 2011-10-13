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

public class Job extends Entity {

    public Job(Service service, String name) {
        super(service, "/services/search/jobs/" + name);
    }

    // UNDONE: return entities?

    public Element cancel() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "cancel");
        return super.post("control", args);
    }

    public Element disable_preview() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "disablepreview");
        return super.post("control", args);
    }

    public Element enable_preview() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "enablepreview");
        return super.post("control", args);
    }

    public Element finalise() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "finalize");
        return super.post("control", args);
    }

    public Element pause() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "pause");
        return super.post("control", args);
    }

    public Element unpause() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "unpause");
        return super.post("control", args);
    }

    public Element setpriority(int priority ) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "setpriority");
        args.put("priority", Integer.toString(priority));
        return super.post("control", args);
    }

    public Element touch() throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "touch");
        return super.post("control", args);
    }

    public Element setttl(int ttl ) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("action", "setttl");
        args.put("ttl", Integer.toString(ttl));
        return super.post("control", args);
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
