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

/**
 * Representation of the Splunk Application setup information.
 */
public class ApplicationSetup extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path the full-path of the parent endpoint.
     */
    ApplicationSetup(Service service, String path) {
        super(service, path + "/setup");
    }

    /**
     * Returns the app's setup information in XML format.
     *
     * @return The app's setup information in XML format.
     */
    public String getSetupXml() {
        return getString("eai:setup");
    }

    /*
     * Because all other keys are dynamic and context specific, they should
     * be retrieved via standard Map access.
     */
}

