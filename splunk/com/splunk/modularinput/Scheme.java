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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;


/**
 * The {@code Scheme} class represents the metadata for a modular input kind.
 *
 * A {@code Scheme} specifies a title, description, several options of how Splunk should run modular inputs of this
 * kind, and a set of arguments which define a particular modular input's properties.
 *
 * The primary use of {@code Scheme} is to abstract away the construction of XML to feed to Splunk.
 */
public class Scheme {
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public enum StreamingMode { SIMPLE, XML };

    // Name of this module input kind. <tt>title</tt> will be used as the URL scheme when
    // specifying particular modular inputs. For example, if <tt>title</tt> is <tt>"abc"</tt>,
    // a particular modular input of this kind would be referenced as <tt>abc://some_name</tt>.
    protected String title;

    // Human readable description of this modular input kind.
    protected String description = null;

    // Should this script be called by Splunk to validate the configuration of modular inputs of this kind?
    // If false, then Splunk does some basic sanity checking.
    protected boolean useExternalValidation = true;

    // Should all modular inputs of this kind share a single instance of this script?
    protected boolean useSingleInstance = false;

    // Will events be streamed to Splunk from this modular input in simple text or in XML? XML is the default
    // and should be preferred unless you have a really good reason to choose otherwise.
    protected StreamingMode streamingMode = StreamingMode.XML;

    // A List of all the arguments that this modular input kind takes.
    protected List<Argument> arguments;

    public Scheme(String title) {
        this.title = title;
        this.arguments = new ArrayList<Argument>();
    }

    /**
     * Gets the title of this modular input kind.
     *
     * @return The title of this modular input kind.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this modular input kind.
     *
     * @param title The title of this modular input kind.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the human readable description of this modular input kind.
     *
     * @return The human readable description of this modular input kind.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the human readable description of this modular input kind.
     *
     * @param description The human readable description of this modular input kind.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns whether Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input or use the validation predicates specified by the arguments.
     *
     * @return {@code true} if Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input, {@code false} if it should use the validation predicates specified by the arguments.
     */
    public boolean isUseExternalValidation() {
        return useExternalValidation;
    }

    /**
     * Specifies whether Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input (true) or use the validation predicates specified by the arguments (false).
     *
     * @param useExternalValidation {@code true} if Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input, {@code false} if it should use the validation predicates specified by the arguments.
     */
    public void setUseExternalValidation(boolean useExternalValidation) {
        this.useExternalValidation = useExternalValidation;
    }

    /**
     * Returns whether Splunk should run all modular inputs of this kind via one instance of the script
     * or start an instance for each modular input.
     *
     * @return {@code true} if Splunk should run all modular inputs of this kind via one instance of the script,
     * {@code false} if it should start an instance for each modular input.
     */
    public boolean isUseSingleInstance() {
        return useSingleInstance;
    }

    /**
     * Specifies whether Splunk should run all modular inputs of this kind via one instance of the script
     * or start an instance for each modular input.
     *
     * @param useSingleInstance {@code true} if Splunk should run all modular inputs of this kind via one instance of the script,
     * {@code false} if it should start an instance for each modular input.
     */
    public void setUseSingleInstance(boolean useSingleInstance) {
        this.useSingleInstance = useSingleInstance;
    }

    /**
     * Returns whether this modular input kind will send events to Splunk as XML (the default and preferred
     * value) or plain text.
     *
     * @return The streaming mode.
     */
    public StreamingMode getStreamingMode() {
        return streamingMode;
    }

    /**
     * Specifies whether this modular input kind will send events to Splunk as XML (the default and preferred
     * value) or plain text.
     *
     * @param streamingMode The streaming mode.
     */
    public void setStreamingMode(StreamingMode streamingMode) {
        this.streamingMode = streamingMode;
    }

    /**
     * Returns all the arguments to this modular input kind.
     *
     * @return A list of all the arguments to this modular input kind.
     */
    public List<Argument> getArguments() {
        return arguments;
    }

    /**
     * Replaces the current list of arguments with the specified one.
     * 
     * @param arguments The list of arguments with which to replace the current
     * list of arguments.
     */
    public void setArguments(List<Argument> arguments) {
        this.arguments = new ArrayList<Argument>(arguments);
    }

    /**
     * Appends an argument to the arguments that this modular input kind takes.
     *
     * @param argument The argument to append to the arguments. 
     */
    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    /**
     * Generates an XML encoding of this scheme to be passed to Splunk.
     *
     * @return An {@code org.w3c.dom.Document} object containing the XML of this scheme.
     * @throws ParserConfigurationException If there was a problem configuring the XML libraries.
     */
    Document toXml() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        Element root = doc.createElement("scheme");
        doc.appendChild(root);

        Element title = doc.createElement("title");
        title.appendChild(doc.createTextNode(this.title));
        root.appendChild(title);

        if (this.description != null) {
            Element description = doc.createElement("description");
            description.appendChild(doc.createTextNode(this.description));
            root.appendChild(description);
        }

        Element useExternalValidation = doc.createElement("use_external_validation");
        useExternalValidation.appendChild(doc.createTextNode(Boolean.toString(this.useExternalValidation)));
        root.appendChild(useExternalValidation);

        Element useSingleInstance = doc.createElement("use_single_instance");
        useSingleInstance.appendChild(doc.createTextNode(Boolean.toString(this.useSingleInstance)));
        root.appendChild(useSingleInstance);

        Element streamingMode = doc.createElement("streaming_mode");
        streamingMode.appendChild(doc.createTextNode(this.streamingMode == StreamingMode.SIMPLE ? "simple" : "xml"));
        root.appendChild(streamingMode);

        Element endpoint = doc.createElement("endpoint");
        root.appendChild(endpoint);

        Element args = doc.createElement("args");
        endpoint.appendChild(args);

        for (Argument arg : this.arguments) {
            arg.addToDocument(doc, args);
        }

        return doc;
    }
}
