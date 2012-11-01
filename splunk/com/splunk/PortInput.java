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

import java.util.Map;

/**
 * Superclass of Inputs that are bound to a particular port and that can be
 * host-restricted.
 * 
 * This collects common functionality to minimize duplication. It is not part
 * of the public API.
 */
abstract class PortInput extends Input {
    
    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The input endpoint.
     */
    PortInput(Service service, String path) {
        super(service, path);
    }
    
    /**
     * Returns the port that this input is listening on.
     */
    public int getPort() {
        String[] nameComponents = this.getName().split(":");
        String portString = nameComponents[nameComponents.length - 1];
        return Integer.parseInt(portString);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Overrides update() to additionally update this input's path if the
     * 'restrictToHost' property is changed.
     */
    @Override
    public void update(Map<String, Object> args) {
        // Is the host restriction being updated?
        Object newHostObject = toUpdate.get("restrictToHost");
        if (args.containsKey("restrictToHost")) {
            newHostObject = args.get("restrictToHost");
        }
        String newHost = (newHostObject == null) ? null : newHostObject.toString();
        
        if (newHost != null && service.versionCompare("5.0") < 0) {
            throw new IllegalStateException(
                    "Cannot update 'restrictToHost' property on Splunk < 5.0.");
        }
        
        String port = null;
        if (newHost != null) {
            // Save old port
            String[] components = getTitle().split(":");
            port = components[components.length - 1];
        }
        
        super.update(args);
        
        if (newHost != null) {
            String newEntityName = newHost.equals("") ? port : newHost + ":" + port;
            
            // Update path with new entity name
            String[] pathComponents = this.path.split("/");
            pathComponents[pathComponents.length - 1] = newEntityName;
            this.path = Util.join("/", pathComponents);
            
            // Update title with new entity name
            this.title = newEntityName;
        }
    }
}
