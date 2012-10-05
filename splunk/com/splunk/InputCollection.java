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

import java.util.*;

/**
 * The {@code InputCollection} class represents a collection of inputs. The 
 * collection is heterogeneous and each member contains an {@code InputKind}
 * value that indicates the specific type of input (<i>input kind</i>).
 */
public class InputCollection extends EntityCollection<Input> {
    protected Set<InputKind> inputKinds = new HashSet<InputKind>();

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
     * @param service The connected {@code Service} instance.
     * @param args Arguments to use when you instantiate the entity, such as 
     * "count" and "offset".
     */
    InputCollection(Service service, Args args) {
        super(service, "data/inputs", args);
    }

    /** {@inheritDoc} */
    @Override public boolean containsKey(Object key) {
        Input input = retrieveInput((String)key);
        return (input != null);
    }

    /**
     * Creates a stub.
     *
     * @param name The name of the input based on the type: the filename or
     * directory and path (monitor, oneshot), the script name (script), the port
     * number (TCP, UDP), the collection name (Windows perfmon, WMI), the stanza
     * (Windows Registry), or the name of the configuration (AD).
     * @return No return value.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a stub by providing additional arguments. For details, see the
     * POST request arguments for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTinput" 
     * target="_blank">data/inputs/* endpoints</a> in the Splunk REST API 
     * documentation.
     *
     * @param name The name of the input based on the type: the filename or
     * directory and path (monitor, oneshot), the script name (script), the port
     * number (TCP, UDP), the collection name (Windows perfmon, WMI), the stanza
     * (Windows Registry), or the name of the configuration (AD).
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
     * @param name The name of the input based on the type: the filename or
     * directory and path (monitor, oneshot), the script name (script), the port
     * number (TCP, UDP), the collection name (Windows perfmon, WMI), the stanza
     * (Windows Registry), or the name of the configuration (AD).
     * @param kind The specific kind of input.
     * @param <T> The implicit type of the input.
     * @return The input that was created.
     */
    public <T extends Input> T create(String name, InputKind kind) {
        return (T)create(name, kind, (Map<String, Object>)null);
    }

    /**
     * Creates a specific kind of input by providing arguments.For details, see the
     * POST request arguments for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTinput" 
     * target="_blank">data/inputs/* endpoints</a> in the Splunk REST API 
     * documentation.
     *
     * @param name The name of the input based on the type: the filename or
     * directory and path (monitor, oneshot), the script name (script), the port
     * number (TCP, UDP), the collection name (Windows perfmon, WMI), the stanza
     * (Windows Registry), or the name of the configuration (AD).
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
     * {@inheritDoc}
     */
    @Override public Input get(Object key) {
        return retrieveInput((String)key);

    }

    /**
     * Returns the value of a scoped, namespace-constrained key if it exists 
     * within this collection.
     *
     * @param key The key to look up.
     * @param namespace The namespace to constrain the search to.
     * @return The value indexed by the key, or {@code null} if it doesn't 
     * exist.
     */

    
   public Input get(Object key, Args namespace) {
       return retrieveInput((String)key, namespace);
   }

    /**
     * Returns the path's {@code InputKind} value.
     *
     * @param path The input path.
     * @return The kind of input.
     */
    protected InputKind itemKind(String path) {
        for (InputKind kind : this.inputKinds) {
            if (path.indexOf("data/inputs/" + kind.relpath) > 0)
                return kind;
        }
        return InputKind.Unknown; // Didn't recognize the input kind
    }

    public Set<InputKind> getInputKinds() {
        return this.inputKinds;
    }

    /**
     * Matches a string to an input name.
     *
     * In most cases this is the same as string equality, but for scripted inputs,
     * which are listed by their full path, we want to match the final component
     * of the filename instead.
     */
    public static boolean matchInputName(InputKind kind, String searchFor, String searchIn) {
        if (kind == InputKind.Script) {
            return searchIn.endsWith("/" + searchFor) || searchIn.endsWith("\\" + searchFor);
        } else {
            return searchFor.equals(searchIn);
        }
    }

    /**
     * Join the strings in {@code joinees}, separated by {@code joiner}.
     *
     * For example, {@code join("/", list)} where {@code list} contains the strings {@code "a"},
     * {@code "b"}, and {@code "c"} returns "a/b/c";
     *
     * @param joiner String to interpolate between each element of {@code joinees}.
     * @param joinees List of strings to join.
     * @return The strines in {@code joinees} concatenated with a copy of {@code joiner} between each.
     */
    public static String join(String joiner, List<String> joinees) {
        if (joinees.isEmpty()) {
            return "";
        } else {
            String joined = joinees.get(0);
            for (String s : joinees.subList(1, joinees.size())) {
                joined = joined + joiner + s;
            }
            return joined;
        }
    }

    /**
     * Recursively assemble a set of all the {@code InputKind}s available on this Splunk
     * instance.
     *
     * @param subPath A list of strings giving the components of the URL.
     * @return A set of {@code InputKind} objects.
     */
    protected Set<InputKind> assembleInputKindSet(List<String> subPath) {
        Set<InputKind> kinds = new HashSet<InputKind>();
        ResponseMessage response = service.get(this.path + "/" + join("/", subPath));
        AtomFeed feed = AtomFeed.parseStream(response.getContent());
        for (AtomEntry entry : feed.entries) {
            String relpath = itemKey(entry);

            boolean hasCreateLink = false;
            for (String linkName : entry.links.keySet()) {
                if (linkName.equals("create")) {
                    hasCreateLink = true;
                }
            }

            List<String> thisSubPath = new ArrayList<String>(subPath);
            thisSubPath.add(relpath);

            if (entry.title.equals("all") || join("/", thisSubPath).equals("tcp/ssl")) {
                continue;
            } else if (hasCreateLink) {
                InputKind newKind = InputKind.makeInputKind(relpath);
                kinds.add(newKind);
            } else {
                Set<InputKind> subKinds = assembleInputKindSet(thisSubPath);
                kinds.addAll(subKinds);
            }
        }
        return kinds;
    }

    /**
     * Refresh the {@code inputKinds} field on this object.
     */
    protected void refreshInputKinds() {
        List<String> basePath = new ArrayList<String>();
        Set<InputKind> kinds = assembleInputKindSet(basePath);
        this.inputKinds.clear();
        this.inputKinds.addAll(kinds);
    }

    /**
     * Refreshes this input collection.
     *
     * @return The refreshed input collection.
     */
    @Override public InputCollection refresh() {
        refreshInputKinds();

        items.clear();

        // Iterate over all input kinds and collect all instances.
        for (InputKind kind : this.inputKinds) {
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
            AtomFeed feed;
            try {
                feed = AtomFeed.parseStream(response.getContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            load(feed);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Input remove(String key) {
        Input input = retrieveInput(key);
        if (input != null) {
            input.remove();
        }
        return input;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Input remove(
            String key, Args namespace) {
        Input input = retrieveInput(key, namespace);
        if (input != null) {
            input.remove();
        }
        return input;
    }

    private Input retrieveInput(String key) {
        validate();
        // Because scripted input names are not 1:1 with the original name
        // (they are the absolute path on the splunk instance followed by
        // the original name), we will iterate over the entities in the list,
        // and if we find one that matches, return it.
        Set<Entry<String, LinkedList<Input>>> set =  items.entrySet();
        for (Entry<String, LinkedList<Input>> entry: set) {
            String entryKey = entry.getKey();
            LinkedList<Input> entryValue = entry.getValue();
            InputKind kind = entryValue.get(0).getKind();

            if (InputCollection.matchInputName(kind, key, entryKey)) {
                    if (entryValue.size() > 1) {
                        throw new SplunkException(SplunkException.AMBIGUOUS,
                                "Key has multiple values, specify a namespace");
                    } else {
                        return entryValue.get(0);
                    }
                }
        }
        return null;
    }

    private Input retrieveInput(String key, Args namespace) {
        validate();
        // because scripted input names are not 1:1 with the original name
        // (they are the absolute path on the splunk instance followed by
        // the original name), we will iterate over the entities in the list,
        // and if we find one that matches, return it.
        String pathMatcher = service.fullpath("", namespace);
        Set<Entry<String, LinkedList<Input>>> set =  items.entrySet();
        for (Entry<String, LinkedList<Input>> entry: set) {
            String entryKey = entry.getKey();
            LinkedList<Input> entryValue = entry.getValue();
            InputKind kind = entryValue.get(0).getKind();

            if (InputCollection.matchInputName(kind, key, entryKey)) {
                for (Input entity: entryValue) {
                    if (entity.path.startsWith(pathMatcher)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
}

