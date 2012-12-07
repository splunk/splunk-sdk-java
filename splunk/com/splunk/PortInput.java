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
 * The {@code PortInput} class represents a superclass of inputs that are bound 
 * to a particular port and can be host restricted.
 * 
 * This class collects common functionality to minimize duplication and is not 
 * part of the public API.
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
     * Overrides the {@code update} method to additionally update this input's 
     * path if the {@code RestrictToHost} property is changed.
     */
    @Override
    public void update(Map<String, Object> args) {
        if (args.containsKey("restrictToHost")) {
            throw new UnsupportedOperationException(
                    "You cannot update the restrictToHost parameter " +
                    "on an existing input with the SDK.");
        } else {
            super.update(args);
        }
    }
}
