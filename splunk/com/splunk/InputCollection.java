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
 * value that indicates the specific type of input.
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
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
     * Creates a stub for a new data input.
     *
     * @param name Depending on the type of input, a string that contains: 
     * <ul><li>The filename or directory and path (for monitor and oneshot 
     * inputs)</li>
     * <li> The script name (for script inputs)</li>
     * <li> The port number (for TCP and UDP inputs)</li>
     * <li> The collection name (for Windows Perfmon and WMI inputs)</li>
     * <li> The stanza (for Windows Registry inputs)</li>
     * <li> The name of the configuration (for Windows AD inputs)</li></ul>
     * @return No return value.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a stub for a new data input based on additional arguments. 
     *
     * @param name Depending on the type of data input, a string that contains: 
     * <ul><li>The filename or directory and path (for monitor and oneshot 
     * inputs)</li>
     * <li> The script name (for script inputs)</li>
     * <li> The port number (for TCP and UDP inputs)</li>
     * <li> The collection name (for Windows Perfmon and WMI inputs)</li>
     * <li> The stanza (for Windows Registry inputs)</li>
     * <li> The name of the configuration (for Windows AD inputs)</li></ul>
     * @param args Optional arguments to define the data input. For a list of 
     * the available parameters, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2#inputparams" 
     * target="_blank">Input parameters</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" 
     * target="_blank">dev.splunk.com</a>.
     * @return No return value.
     * @throws UnsupportedOperationException
     */
    @Override public Input create(String name, Map args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new data input based on the input kind.
     *
     * @param name Depending on the type of data input, a string that contains: 
     * <ul><li>The filename or directory and path (for monitor and oneshot 
     * inputs)</li>
     * <li> The script name (for script inputs)</li>
     * <li> The port number (for TCP and UDP inputs)</li>
     * <li> The collection name (for Windows Perfmon and WMI inputs)</li>
     * <li> The stanza (for Windows Registry inputs)</li>
     * <li> The name of the configuration (for Windows AD inputs)</li></ul>
     * @param kind A member of {@code InputKind}, indicating the type of input.
     * @param <T> The implicit type of the input.
     * @return The {@code Input} that was created.
     */
    public <T extends Input> T create(String name, InputKind kind) {
        return (T)create(name, kind, (Map<String, Object>)null);
    }

    /**
     * Creates a new data input based on the input kind and additional 
     * arguments. 
     *
     * @param name Depending on the type of data input, a string that contains: 
     * <ul><li>The filename or directory and path (for monitor and oneshot 
     * inputs)</li>
     * <li> The script name (for script inputs)</li>
     * <li> The port number (for TCP and UDP inputs)</li>
     * <li> The collection name (for Windows Perfmon and WMI inputs)</li>
     * <li> The stanza (for Windows Registry inputs)</li>
     * <li> The name of the configuration (for Windows AD inputs)</li></ul>
     * @param kind A member of {@code InputKind}, indicating the type of input.
     * @param args Optional arguments to define the data input. For a list of 
     * the available parameters, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2#inputparams" 
     * target="_blank">Input parameters</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" 
     * target="_blank">dev.splunk.com</a>.
     * @param <T> The implicit type of the input.
     * @return The {@code Input} that was created.
     */
    public <T extends Input> T
    create(String name, InputKind kind, Map<String, Object> args) {
        args = Args.create(args).add("name", name);
        String path = this.path + "/" + kind.getRelativePath();
        service.post(path, args);
        
        invalidate();
        
        return (T)get(name);
    }
    
    /**
     * Creates a new data input based on an Atom entry.
     *
     * @param entry The {@code AtomEntry} object describing the entry.
     * @return The {@code Input} that was created.
     */
    @Override
    protected Input createItem(AtomEntry entry) {
        String path = itemPath(entry);
        InputKind kind = itemKind(path);
        Class inputClass = kind.getInputClass();
        return createItem(inputClass, path, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override public Input get(Object key) {
        return retrieveInput((String)key);
    }

    /**
     * Returns the value of a scoped, namespace-constrained key, if it 
     * exists within this collection.
     *
     * @param key The key to look up.
     * @param namespace The namespace to constrain the search to.
     * @return The value indexed by the key, or {@code null} if it doesn't 
     * exist.
     */
   public Input get(Object key, Args namespace) {
       Util.ensureNamespaceIsExact(namespace);
       return retrieveInput((String)key, namespace);
   }

    /**
     * Returns the input kind for a given path.
     *
     * @param path The relative endpoint path (the path that follows 
     * data/inputs).
     * @return A member of {@code InputKind}, indicating the type of input.
     */
    protected InputKind itemKind(String path) {
        String relpathWithInputName = Util.substringAfter(path, "/data/inputs/", null);
        for (InputKind kind : inputKinds) {
            if (relpathWithInputName.startsWith(kind.getRelativePath())) {
                return kind;
            }
        }
        
        // Not good. This means that there is an input of an unknown kind.
        return InputKind.Unknown;
    }

    /**
     * Return a set of all the input kinds recognized by the Splunk server.
     *
     * @return A set of {@code InputKind}s.
     */
    public Set<InputKind> getInputKinds() {
        return this.inputKinds;
    }

    /**
     * Indicates whether a given string matches the input name (string 
     * equality). For scripted inputs, which are listed by their full path, this
     * method compares only the final component of the filename for a match.
     *
     * @param kind A member of {@code InputKind}, indicating the type of input.
     * @param searchFor A string to search for.
     * @param searchIn The string that contains the input name.
     * @return {@code true} if the string matches the input name, {@code false}
     * if not.
     */
    protected static boolean matchesInputName(InputKind kind, String searchFor, String searchIn) {
        if (kind == InputKind.Script) {
            return searchIn.endsWith("/" + searchFor) || searchIn.endsWith("\\" + searchFor);
        } else {
            return searchFor.equals(searchIn);
        }
    }


    /**
     * Assembles a set of all the input kinds that are available on this Splunk
     * instance. To list all inputs, pass an empty list to {@code subPath}. Or, 
     * specify a component of the path such as "tcp" to list all TCP inputs. 
     *
     * @param subPath A list of strings containing the components of the
     * endpoint path that follow data/inputs/.
     * @return A set of available {@code InputKind}s.
     */
    private Set<InputKind> assembleInputKindSet(List<String> subPath) {
        Set<InputKind> kinds = new HashSet<InputKind>();
        ResponseMessage response = service.get(this.path + "/" + Util.join("/", subPath));
        AtomFeed feed = AtomFeed.parseStream(response.getContent());
        for (AtomEntry entry : feed.entries) {
            String itemKeyName = itemKey(entry);

            boolean hasCreateLink = false;
            for (String linkName : entry.links.keySet()) {
                if (linkName.equals("create")) {
                    hasCreateLink = true;
                }
            }

            List<String> thisSubPath = new ArrayList<String>(subPath);
            thisSubPath.add(itemKeyName);
            
            String relpath = Util.join("/", thisSubPath);

            if (relpath.equals("all") || relpath.equals("tcp/ssl")) {
                // Skip these input types
                continue;
            } else if (hasCreateLink) {
                // Found an InputKind leaf
                InputKind newKind = InputKind.create(relpath);
                kinds.add(newKind);
            } else {
                Set<InputKind> subKinds = assembleInputKindSet(thisSubPath);
                kinds.addAll(subKinds);
            }
        }
        return kinds;
    }

    /**
     * Refreshes the {@code inputKinds} field on this object.
     */
    private void refreshInputKinds() {
        Set<InputKind> kinds = assembleInputKindSet(new ArrayList<String>());
        
        this.inputKinds.clear();
        this.inputKinds.addAll(kinds);
    }

    /**
     * Refreshes this input collection.
     *
     * @return The refreshed {@code InputCollection}.
     */
    @Override public InputCollection refresh() {
        // Populate this.inputKinds
        refreshInputKinds();

        items.clear();

        // Iterate over all input kinds and collect all instances.
        for (InputKind kind : this.inputKinds) {
            if (service.versionIsAtLeast("6.0.0")) {
                // In Splunk 6 and later, the registry endpoint has been deprecated in favor of the new
                // WinRegMon modular input, but both now point to the same place. To avoid duplicates, we have
                // to read only one of them.
                if (kind.getKind().equals("registry")) {
                    continue;
                }
            }
            String relpath = kind.getRelativePath();
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
        Util.ensureNamespaceIsExact(namespace);
        
        Input input = retrieveInput(key, namespace);
        if (input != null) {
            input.remove();
        }
        return input;
    }

    private Input retrieveInput(String key) {
        validate();
        
        // Because scripted input names are not 1:1 with the original name
        // (they are the absolute path on the Splunk instance followed by
        // the original name), we will iterate over the entities in the list,
        // and if we find one that matches, return it.
        Set<Entry<String, LinkedList<Input>>> set = items.entrySet();
        for (Entry<String, LinkedList<Input>> entry: set) {
            String entryKey = entry.getKey();
            LinkedList<Input> entryValue = entry.getValue();
            InputKind kind = entryValue.get(0).getKind();

            if (InputCollection.matchesInputName(kind, key, entryKey)) {
                if (entryValue.size() > 1) {
                    throw new SplunkException(SplunkException.AMBIGUOUS,
                            "Multiple inputs matched " + key + "; specify a namespace to disambiguate.");
                } else {
                    return entryValue.get(0);
                }
            }
        }
        return null;
    }

    private Input retrieveInput(String key, Args namespace) {
        Util.ensureNamespaceIsExact(namespace);
        validate();
        
        // Because scripted input names are not 1:1 with the original name
        // (they are the absolute path on the Splunk instance followed by
        // the original name), we will iterate over the entities in the list,
        // and if we find one that matches, return it.
        String pathMatcher = service.fullpath("", namespace);
        Set<Entry<String, LinkedList<Input>>> set = items.entrySet();
        for (Entry<String, LinkedList<Input>> entry: set) {
            String entryKey = entry.getKey();
            LinkedList<Input> entryValue = entry.getValue();
            InputKind kind = entryValue.get(0).getKind();

            if (InputCollection.matchesInputName(kind, key, entryKey)) {
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

