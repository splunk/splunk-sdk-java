/*
 * Copyright 2012 Splunk, Inc.
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

/**
 * The {@code InputCollection} class represents a collection of inputs. The 
 * collection is heterogeneous and each member contains an {@code InputKind} property
 * that indicates the specific type of input (<i>input kind</i>).
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
     * @param service The connected {@code Service} instance.
     */
    InputCollection(Service service) {
        super(service, "data/inputs");
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    InputCollection(Service service, Args args) {
        super(service, "data/inputs", args);
    }

    /**
     * Creates stub.
     *
     * @param name The name of the input.
     * @return No return value.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a stub by providing additional arguments.
     * @see <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTinput" target="_blank">For valid arguments, see the POST requests for the /data/inputs/ endpoints in the Splunk REST API documentation.</a>
     *
     * @param name The name of the input.
     * @param args Optional arguments.
     * @return No return value.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name, Map args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a specific kind of input.
     *
     * @param name The name of the input.
     * @param kind The specific kind of input.
     * @param <T> The implicit type of the input.
     * @return The input that was created.
     */
    public <T extends Input> T create(String name, InputKind kind) {
        return (T)create(name, kind, (Map<String, Object>)null);
    }

    /**
     * Creates a specific kind of input by providing arguments.
     * @see <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTinput" target="_blank">For valid arguments, see the POST requests for the /data/inputs/ endpoints in the Splunk REST API documentation.</a>
     *
     * @param name The name of the input.
     * @param kind The specific kind of input.
     * @param args Optional arguments.
     * @param <T> The implicit type of the input.
     * @return The input that was created.
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
     * Creates an {@code Input} resource item.
     *
     * @param entry The {@code AtomEntry} object describing the entry.
     * @return The input that was created.
     */
    @Override protected Input createItem(AtomEntry entry) {
        String path = itemPath(entry);
        InputKind kind = itemKind(path);
        Class inputClass = kind.inputClass;
        return createItem(inputClass, path, null);
    }

    /**
     * Returns the path's {@code InputKind} value.
     *
     * @param path The input path.
     * @return The kind of input.
     */
    protected InputKind itemKind(String path) {
        for (InputKind kind : kinds) {
            if (path.indexOf("data/inputs/" + kind.relpath) > 0)
                return kind;
        }
        return InputKind.Unknown; // Didn't recognize the input kind
    }

    /**
     * Refreshes this input collection.
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

