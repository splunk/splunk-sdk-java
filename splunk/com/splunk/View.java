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
 * The {@code View} class represents a specific Splunk view, which you can view, modify, and remove.
 */
public class View extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The application endpoint.
     */
    View(Service service, String path) {
        super(service, path);
    }
    
    /**
     * Indicates whether the view is visible and navigable from Splunk Web.
     *
     * @return {@code true} if the view is visible and navigable from Splunk
     * Web, {@code false} if not.
     */
    public boolean isVisible() {
        return getBoolean("isVisible", false);
    }
    
    /**
     * Sets whether the view is visible and navigable from Splunk Web.
     *
     * @param visible {@code true} if the view can be visible and navigable
     * from Splunk Web, {@code false} if not.
     */
    public void setVisible(boolean visible) {
        setCacheValue("isVisible", visible);
    }
    
    /**
     * Indicates whether the view is a dashboard and navigable from Splunk Web.
     *
     * @return {@code true} if the view is visible and navigable from Splunk
     * Web, {@code false} if not.
     */
    public boolean isDashboard() {
        return getBoolean("isDashboard", false);
    }
    
    /**
     * Sets whether the view is a dashboard and navigable from Splunk Web.
     *
     * @param visible {@code true} if the view can be visible and navigable
     * from Splunk Web, {@code false} if not.
     */
    public void setDashboard(boolean isDashboard) {
        setCacheValue("isDashboard", isDashboard);
    }

    /**
     * Returns the view's label (its name).
     *
     * @return The label, or {@code null} if not specified.
     */
    public String getLabel() {
        return getString("label", null);
    }
    
    /**
     * Sets the view's name, which is displayed in Splunk Web. The name should be
     * between 5-80 characters and should not include the prefix "Splunk For".
     *
     * @param label The label (name) of the view.
     */
    public void setLabel(String label) {
        setCacheValue("label", label);
    }
    
    /**
     * Returns the view's eai:data
     *
     * @return The data, or {@code null} if not specified.
     */
    public String getData() {
        return getString("eai:data", null);
    }
}

