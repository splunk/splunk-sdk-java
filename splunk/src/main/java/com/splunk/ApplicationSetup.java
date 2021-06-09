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

/**
 * The {@code ApplicationSetup} class represents the setup information for a
 * Splunk app.
 */
public class ApplicationSetup extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The application endpoint.
     */
    ApplicationSetup(Service service, String path) {
        super(service, path + "/setup");
    }

    /**
     * Indicates whether to reload the objects contained in the 
     * locally-installed app.
     *
     * @return {@code true} if objects are reloaded, {@code false} if not.
     */
    public boolean getRefresh() {
        return getBoolean("refresh", false);
    }

    /**
     * Returns the app's setup information in XML format.
     *
     * @return The setup information for the app.
     */
    public String getSetupXml() {
        return getString("eai:setup");
    }

     // Because all other keys are dynamic and context specific, they should
     // be retrieved using Map (dictionary) get access.
}

