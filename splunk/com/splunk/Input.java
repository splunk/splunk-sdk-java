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
 * The {@code Input} class represents a data input. This class is the base for all
 * typed {@code Input} classes and is also used when Splunk does not recognize an 
 * input type (<i>input kind</i>).
 */
public class Input extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The input endpoint.
     */
    Input(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns unknown input kind. Overridden in sub-classes.
     *
     * @return Unknown input kind.
     */
    public InputKind getKind() {
        return InputKind.Unknown;
    }
}
