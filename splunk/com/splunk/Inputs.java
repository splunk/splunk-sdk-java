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

public class Inputs extends Collection {

    public Inputs(Service service) {
        super(service, "/services/data/inputs/");
    }

    // UNDONE:

/*

    // wkc -- may move some to super class?

    def create(self, kind, name, **kwargs):
        """Creates an input of the given kind, with the given name & args."""
        response = self.post(self._kindmap[kind], name=name, **kwargs)
        return self.refresh()[self.itemkey(kind, name)]

    def delete(self, key):
        """Deletes the input with the given key."""
        response = self.service.delete(self._infos[key]['path'])
        self.refresh()
        return self

    def itemkey(self, kind, name):
        """Constructs a key from the given kind and item name."""
        if not kind in self._kindmap.keys():
            raise ValueError("Unknown kind '%s'" % kind)
        return "%s:%s" % (kind, name)

    def itemmeta(self, kind):
        """Returns metadata for members of the given kind."""
        response = self.get("%s/_new" % self._kindmap[kind])
        content = load(response, MATCH_ENTRY_CONTENT)
        return record({
            'eai:acl': content['eai:acl'],
            'eai:attributes': content['eai:attributes']
        })

    @property
    def kinds(self):
        """Returns the list of kinds that this collection may contain."""
        return self._kindmap.keys()

    def kindpath(self, kind):
        """Returns the path to resources of the given kind."""
        return self.path + self._kindmap[kind]

    # args: kind*
    def list(self, *args):
        """Returns a list of collection keys, optionally filtered by kind."""
        if len(args) == 0: return self._infos.keys()
        return [k for k, v in self._infos.iteritems() if v['kind'] in args]

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
