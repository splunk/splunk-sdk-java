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

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * The {@code Xml} class represents a collection of XML utilities.
 */
public class Xml {

    /**
     * Parses the given input stream and returns it as an XML document object
     * model (DOM).
     *
     * @param input The {@code InputStream} to parse.
     * @return The XML DOM.
     */
    public static Document parse(InputStream input) {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(input);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

