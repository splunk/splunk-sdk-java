/*
 * Copyright 2013 Splunk, Inc.
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

package com.splunk.modularinput;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * The {@code XmlUtil} class contains utility functions for handling XML that are used in several places.
 */
class XmlUtil {

    /**
     * Given an {@code org.w3c.dom.Node} which is an element containing text, returns the text. If there
     * is anything but text inside the element, throws a {@code MalformedDataException} with {@code errorMessage} as the reason.
     *
     * @param node The {@code Node} object to pull text out of.
     * @param errorMessage The reason provided if there is something besides text in the element.
     * @return A String of the text in the element.
     * @throws MalformedDataException When there is anything besides text in the element.
     */
    static String textInNode(Node node, String errorMessage) throws MalformedDataException {
        Node child = node.getFirstChild();
        if (child.getNodeType() != child.TEXT_NODE) {
            throw new MalformedDataException(errorMessage);
        } else {
            return ((Text)child).getData();
        }
    }

    /**
     * Coerces all the forms of Boolean Splunk may return to proper Booleans.
     *
     * @param s A String containing the Boolean from Splunk.
     * @return A Java Boolean.
     * @throws MalformedDataException If the string can't be coerced to a Boolean.
     */
    static boolean normalizeBoolean(String s) throws MalformedDataException {
        if (s == null) {
            throw new MalformedDataException("Cannot interpret null as a boolean.");
        }

        String value = s.trim().toLowerCase();

        if (value.equals("true") || value.equals("t") || value.equals("on") ||
                value.equals("yes") || value.equals("y") || value.equals("1")) {
            return true;
        } else if (value.equals("false") || value.equals("f") || value.equals("off") ||
                value.equals("no") || value.equals("n") || value.equals("0")) {
            return false;
        } else {
            throw new MalformedDataException("Cannot interpret string \"" + value + "\" as a boolean.");
        }

    }
}
