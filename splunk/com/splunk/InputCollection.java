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

import java.util.Map;

// InputCollection is a heterogenous collection where each item contains a
// kind property that indicates the kind of input and corresponding item type.
public class InputCollection extends EntityCollection<Input> {

    // CONSIDER: We can probably initialize the following based on platform and
    // avoid adding the Windows inputs to the list on non-Windows platforms.
    static InputKind[] kinds = new InputKind[] {
        InputKind.Monitor,
        InputKind.Script,
        InputKind.Tcp,
        InputKind.TcpSplunk,
        InputKind.Udp,
        InputKind.WindowsActiveDirectory,
        InputKind.WindowsEventLog,
        InputKind.WindowsPerfmon,
        InputKind.WindowsRegistry,
        InputKind.WindowsWmi
    };

    InputCollection(Service service) {
        super(service, "data/inputs");
    }

    @Override public Input create(String name) {
        throw new UnsupportedOperationException();
    }

    @Override public Input create(String name, Map args) {
        throw new UnsupportedOperationException();
    }

    public <T extends Input> T create(String name, InputKind kind) {
        return create(name, kind, null);
    }

    public <T extends Input> T
    create(String name, InputKind kind, Map<String, Object> args) {
        args = Args.create(args).add("name", name);
        String path = this.path + "/" + kind.relpath;
        service.post(path, args);
        invalidate();
        return (T)get(name);
    }

    @Override protected Input createItem(AtomEntry entry) {
        String path = itemPath(entry);
        InputKind kind = itemKind(path);
        Class inputClass = kind.inputClass;
        return createItem(inputClass, path);
    }

    // Return the InputKind of the item associated with the given path.
    protected InputKind itemKind(String path) {
        for (InputKind kind : kinds) {
            // UNDONE: Is there a better way to determine input kind from the
            // contents of the entities atom response?
            if (path.indexOf("data/inputs/" + kind.relpath) > 0)
                return kind;
        }
        return InputKind.Unknown; // Didn't recognize the input kind
    }

    @Override public InputCollection refresh() {
        items.clear();

        // Iterate over all input kinds and collect all instances.
        for (InputKind kind : kinds) {
            String relpath = kind.relpath;
            String inputs = String.format("%s/%s?count=-1", path, relpath);
            ResponseMessage response;
            try {
                response = service.get(inputs);
            }
            catch (HttpException e) {
                // On some platforms certain input endpoints don't exist, for
                // example the Windows inputs endpoints don't exist on non-
                // Windows platforms.
                if (e.getStatus() == 404) continue;
                throw e;
            }
            AtomFeed feed = AtomFeed.parse(response.getContent());
            load(feed);
        }

        return this;
    }
}

