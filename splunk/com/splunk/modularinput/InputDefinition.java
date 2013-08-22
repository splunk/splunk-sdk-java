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

import com.splunk.modularinput.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code InputDefinition} class encodes the XML defining inputs that Splunk passes to
 * a modular input script as a Java object.
 */
public class InputDefinition {
    // We use a map to hold all parameters such as server host, server URI, etc. instead of individual fields
    // so that additions to the input definition contract in the future won't break this implementation. It also
    // simplifies the parsing code below.
    private Map<String,String> metadata;

    private Map<String, Map<String, Parameter>> inputs;

    private final String serverHostField = "server_host";
    private final String serverUriField = "server_uri";
    private final String checkpointDirField = "checkpoint_dir";
    private final String sessionKeyField = "session_key";

    // Package private on purpose
    InputDefinition() {
        inputs = new HashMap<String, Map<String, Parameter>>();
        metadata = new HashMap<String, String>();
    }

    /**
     * Gets the name of the field to fetch.
     * 
     * In future versions of Splunk, there may be additional fields on the {@code InputDefinition}. {@code getField} permits
     * access to them in case you are constrained to an old version of the Splunk SDK for Java.
     *
     * @param fieldName The name of the field to fetch.
     * @return The field.
     */
    public String getField(String fieldName) {
        return this.metadata.get(fieldName);
    }

    /**
     * Sets the name of the server on which this modular input is being run.
     */
    public void setServerHost(String serverHost) {
        this.metadata.put(serverHostField, serverHost);
    }

    /**
     * Gets the name of the server on which this modular input is being run.
     *
     * @return The name of the server on which this modular input is being run.
     */
    public String getServerHost() {
        return this.metadata.get(serverHostField);
    }

    /**
     * Sets the URI to reach the server on which this modular input is being run.
     *
     * @param serverUri The URI to reach the server on which this modular input is being run.
     */
    public void setServerUri(String serverUri) {
        this.metadata.put(serverUriField, serverUri);
    }

    /**
     * Gets the URI to the server on which this modular input is being run.
     *
     * @return The URI to the server on which this modular input is being run.
     */
    public String getServerUri() {
        return this.metadata.get(serverUriField);
    }

    /**
     * Sets the path to which to write checkpoint files.
     *
     * @param checkpointDir The path to which to write checkpoint files.
     */
    public void setCheckpointDir(String checkpointDir) {
        this.metadata.put(checkpointDirField, checkpointDir);
    }

    /**
     * Gets the path to which to write checkpoint files for restarting inputs.
     *
     * @return The path to which to write checkpoint files for restarting inputs.
     */
    public String getCheckpointDir() {
        return this.metadata.get(checkpointDirField);
    }

    /**
     * Sets a session key that can be used to access splunkd's REST API.
     *
     * @param sessionKey A session key that can be used to access splunkd's REST API.
     */
    public void setSessionKey(String sessionKey) {
        this.metadata.put(sessionKeyField, sessionKey);
    }

    /**
     * Sets a session providing access to splunkd's REST API on this host.
     *
     * @return A session key providing access to splunkd's REST API on this host.
     */
    public String getSessionKey() {
        return this.metadata.get(sessionKeyField);
    }

    /**
     * Adds an input to the set of inputs on this {@code InputDefinition}.
     *
     * @param name The name of this input (e.g., foobar://this-input-name).
     * @param parameters A collection of {@code Parameter} objects giving the settings for this input.
     */
    public void addInput(String name, Collection<Parameter> parameters) {
        Map<String, Parameter> paramMap = new HashMap<String, Parameter>();

        for (Parameter p : parameters) {
            paramMap.put(p.getName(), p);
        }

        this.inputs.put(name, paramMap);
    }

    /**
     * @return A map of all the inputs specified in this {@code InputDefinition}.
     */
    public Map<String, Map<String, Parameter>> getInputs() {
        return this.inputs;
    }

    /**
     * Parses a stream containing XML into an InputDefinition.
     *
     * @param stream The stream containing XML to parse.
     * @return An {@code InputDefinition} object.
     * @throws ParserConfigurationException If there are errors in setting up the parser (which indicates system
     *           configuration issues).
     * @throws IOException If there is an error in reading from the stream.
     * @throws SAXException When the XML is invalid.
     * @throws MalformedDataException When the XML does specify a valid set of inputs.
     */
    public static InputDefinition parseDefinition(InputStream stream) throws ParserConfigurationException,
            IOException, SAXException, MalformedDataException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(stream);

        InputDefinition definition = new InputDefinition();
        for (Node node = doc.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == node.TEXT_NODE) {
                continue;
            } else if (node.getNodeName().equals("configuration")) {
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == child.TEXT_NODE) {
                        continue;
                    }
                    if (!child.getNodeName().equals("stanza")) {
                        throw new MalformedDataException("Expected stanza element; found " + child.getNodeName());
                    }
                    String name = child.getAttributes().getNamedItem("name").getNodeValue();
                    List<Parameter> parameter = Parameter.nodeToParameterList(child);
                    definition.addInput(name, parameter);
                }
            } else {
                definition.metadata.put(
                        node.getNodeName(),
                        XmlUtil.textInNode(node, "Expected a text value in element " + node.getNodeName())
                );
            }
        }

        return definition;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InputDefinition)) {
            return false;
        }
        InputDefinition that = (InputDefinition)other;
        return this.metadata.equals(that.metadata) && this.inputs.equals(that.inputs);
    }

    @Override
    public int hashCode() {
        return this.metadata.hashCode() ^ (this.getInputs() == null ? 0 : this.getInputs().hashCode());
    }
}
