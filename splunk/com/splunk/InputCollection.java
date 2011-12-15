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

/**
 * Representation of a collection of inputs. The collection is heterogeneous
 * and each member contains a kind-property that indicates the specific
 * kind of input.
 */
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

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     */
    InputCollection(Service service) {
        super(service, "data/inputs");
    }

    /**
     * Create stub.
     *
     * @param name The name of the input.
     * @return no return.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create stub.
     *
     * @param name The name of the input.
     * @param args Optional arguments.
     * @return no return.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name, Map args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a specific kind up input.
     *
     * @param name The name of the input created.
     * @param kind The specific kind of input created.
     * @param <T> The implicit type of the input created.
     * @return The created input.
     */
    public <T extends Input> T create(String name, InputKind kind) {
        return (T)create(name, kind, null);
    }

    /**
     * Creates a specific kind up input.
     *
     * @param name The name of the input created.
     * @param kind The specific kind of input created.
     * @param args Optional arguments.
     * @param <T> The implicit type of the input created.
     * @return The created input.
     */
    public <T extends Input> T
    create(String name, InputKind kind, Map<String, Object> args) {
        args = Args.create(args).add("name", name);
        String path = this.path + "/" + kind.relpath;
        service.post(path, args);
        invalidate();
        return (T)get(name);
    }

    /**
     * Create an Input resource item.
     *
     * @param entry The Atom object describing the entry.
     * @return the created input.
     */
    @Override protected Input createItem(AtomEntry entry) {
        String path = itemPath(entry);
        InputKind kind = itemKind(path);
        Class inputClass = kind.inputClass;
        return createItem(inputClass, path);
    }

    /**
     * Returns the the path's InputKind.
     *
     * @param path The input path.
     * @return The input kind.
     */
    protected InputKind itemKind(String path) {
        for (InputKind kind : kinds) {
            if (path.indexOf("data/inputs/" + kind.relpath) > 0)
                return kind;
        }
        return InputKind.Unknown; // Didn't recognize the input kind
    }

    /**
     * Refresh this input collection.
     *
     * @return The refreshed input collection.
     */
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

