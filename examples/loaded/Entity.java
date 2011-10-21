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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;
import com.splunk.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Iterator;

public class Entity extends Resource {
    public Map<String, Object> content;
    public String title;

    public Entity(Service service, String path) {
        super(service, path);
    }

    public ResponseMessage get() throws IOException {
        return service.get(path);
    }

    void load(AtomEntry entry) {
        super.load(entry);
        this.content = entry.content;
        this.title = entry.title;
    }
}

