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

import com.splunk.atom.AtomObject;
import com.splunk.http.ResponseMessage;
import com.splunk.Args;

import java.io.IOException;
import java.util.Map;

public abstract class Resource extends Endpoint {
    Map<String, String> actions;
    String id;
    Boolean maybe = false;

    public Resource(Service service, String path) {
        super(service, path);
    }

    public Map<String, String> getActions() {
        validate();
        return this.actions;
    }

    public String getId() {
        validate();
        return this.id;
    }

    public void invalidate() {
        maybe = false;
    }

    void invoke(String action) {
        invoke(action, null);
    }

    void invoke(String action, Args args) {
        String path = getActions().get(action);
        if (path == null) {
            String message = String.format(
                "Action invalid for this resoruces: '%s'", action);
            throw new RuntimeException(message);
        }
        ResponseMessage response;
        try {
            response = service.post(path, args);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        assert(response.getStatus() == 200); // UNDONE
    }

    void load(AtomObject value) {
        this.actions = value.links;
        this.id = value.id;
        this.maybe = true; // Maybe up to date :) ..
    }

    public abstract void refresh();

    public void validate() {
        if (maybe == false) refresh();
    }
}

