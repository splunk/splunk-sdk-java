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

public class Inputs extends Collection {

    private Map<String,String> kindMap = new HashMap<String, String>();
    private void initmap() {
        kindMap.put("ad", "ad");
        kindMap.put("monitor", "monitor");
        kindMap.put("registry", "registry");
        kindMap.put("script", "script");
        kindMap.put("ad", "ad");
        kindMap.put("tcp", "tcp/raw");
        kindMap.put("splunktcp", "tcp/cooked");
        kindMap.put("udp", "udp");
        kindMap.put("win-event-log-collections", "win-event-log-collections");
        kindMap.put("win-perfmon", "win-perfmon");
        kindMap.put("win-wmi-collections", "win-wmi-collections");
    }

    public Inputs(Service service) {
        super(service, "/services/data/inputs/");
        initmap();
    }

    public Element create(String kind,
                          String name,
                          Map<String,String> args) throws Exception {
        if (!kindMap.containsKey(kind)) {
            throw new Exception("Input creation requires a valid 'kind', from: "
                    + kindMap);
        }
        args.put("name", name);
        super.post(kindMap.get(kind), args);
        return super.get();
    }

    public Element create(String kind, String name) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        return create(kind, name, args);
    }

    public Element delete(String kind, String name) throws Exception {
        if (!kindMap.containsKey(kind)) {
            throw new Exception("Input creation requires a valid 'kind', from: "
                    + kindMap);
        }
        return super.delete(kindMap.get(kind) + "/" + name);
    }

    public Map<String,String> kinds() {
        return kindMap;
    }

    public String kindpath(String kind) {
        return kindMap.get(kind);
    }

    public String itemkey(String kind, String name) throws Exception {
        if (!kindMap.containsKey(kind)) {
            throw new Exception("Input creation requires a valid 'kind', from: "
                    + kindMap);
        }
        return kind + ":" + name;
    }


/*
    // UNDONE:

    def delete(self, key):
        """Deletes the input with the given key."""
        response = self.service.delete(self._infos[key]['path'])
        self.refresh()
        return self

    def itemmeta(self, kind):
        """Returns metadata for members of the given kind."""
        response = self.get("%s/_new" % self._kindmap[kind])
        content = load(response, MATCH_ENTRY_CONTENT)
        return record({
            'eai:acl': content['eai:acl'],
            'eai:attributes': content['eai:attributes']
        })

    # Refreshes the
    def refresh(self):
        """Refreshes the internal directory of entities and entity metadata."""
        self._infos = {}
        for kind in self.kinds:

            response = None
            try:
                response = self.service.get(self.kindpath(kind), count=-1)
            except HTTPError as e:
                if e.status == 404:
                    continue # Nothing of this kind
                else:
                    raise

            entry = load(response).feed.get('entry', None)
            if entry is None: continue
            if not isinstance(entry, list): entry = [entry]
            for item in entry:
                name = item.title
                key = self.itemkey(kind, name)
                path = urlparse(item.id).path
                links = dict([(link.rel, link.href) for link in item.link])
                self._infos[key] = {
                    'key': key,
                    'kind': kind,
                    'name': name,
                    'path': path,
                    'links': links,
                }
        return self

*/
}
