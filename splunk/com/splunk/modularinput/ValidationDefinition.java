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
 * The {@code ValidationDefinition} class represents the XML sent by Splunk for external validation of a new modular input.
 */
public class ValidationDefinition {
    private Map<String, String> metadata;

    private Map<String, Parameter> parameters;

    private final String serverHostField = "server_host";
    private final String serverUriField = "server_uri";
    private final String checkpointDirField = "checkpoint_dir";
    private final String sessionKeyField = "session_key";
    private final String nameField = "name";

    // Package private on purpose.
    ValidationDefinition() {
        super();
        metadata = new HashMap<String, String>();
    }

    /**
     * Sets the name of the server on which this modular input is being run.
     *
     * @param serverHost The name of the server on which this modular input is being run.
     */
    void setServerHost(String serverHost) {
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
    void setServerUri(String serverUri) {
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
     * Sets the path to write checkpoint files in.
     *
     * @param checkpointDir The path to write checkpoint files in.
     */
    void setCheckpointDir(String checkpointDir) {
        this.metadata.put(checkpointDirField, checkpointDir);
    }

    /**
     * Gets the path to write checkpoint files for restarting inputs in.
     *
     * @return The path to write checkpoint files for restarting inputs in.
     */
    public String getCheckpointDir() {
        return this.metadata.get(checkpointDirField);
    }

    /**
     * Sets a session key that can be used to access splunkd's REST API.
     *
     * @param sessionKey A session key that can be used to access splunkd's REST API.
     */
    void setSessionKey(String sessionKey) {
        this.metadata.put(sessionKeyField, sessionKey);
    }

    /**
     * Gets a session key providing access to splunkd's REST API on this host.
     *
     * @return A session key providing access to splunkd's REST API on this host.
     */
    public String getSessionKey() {
        return this.metadata.get(sessionKeyField);
    }

    /**
     * Sets the name of the proposed modular input instance.
     *
     * @param name The name of the proposed modular input instance.
     */
    void setName(String name) {
        this.metadata.put(nameField, name);
    }

    /**
     * Gets the name of the proposed modular input instance.
     *
     * @return The name of the proposed modular input instance.
     */
    public String getName() {
        return this.metadata.get(nameField);
    }

    /**
     * Sets a list of {@code Parameter} objects giving the proposed configuration.
     *
     * @param parameters A list of {@code Parameter} objects giving the proposed configuration.
     */
    public void setParameters(Collection<Parameter> parameters) {
        Map<String, Parameter> paramMap = new HashMap<String, Parameter>();
        for (Parameter p : parameters) {
            paramMap.put(p.getName(), p);
        }
        this.parameters = paramMap;
    }

    /**
     * @return The parameters on the proposed input.
     */
    public Map<String, Parameter> getParameters() {
        return this.parameters;
    }

    /**
     * Create a ValidationDefinition from a provided stream containing XML. The XML typically will look like
     *
     * <pre>
     * {@code
     * <items>
     *     <server_host>myHost</server_host>
     *     <server_uri>https://127.0.0.1:8089</server_uri>
     *     <session_key>123102983109283019283</session_key>
     *     <checkpoint_dir>/opt/splunk/var/lib/splunk/modinputs</checkpoint_dir>
     *     <item name="myScheme">
     *         <param name="param1">value1</param>
     *         <param_list name="param2">
     *             <value>value2</value>
     *             <value>value3</value>
     *             <value>value4</value>
     *         </param_list>
     *     </item>
     * </items>
     * }
     * </pre>
     *
     * @param stream containing XML to parse.
     * @return a ValidationDefinition.
     * @throws ParserConfigurationException if there are errors in setting up the parser (which indicates system
     *           configuration issues).
     * @throws IOException if there is an error in reading from the stream.
     * @throws SAXException when the XML is invalid.
     * @throws MalformedDataException when the XML does not meet the required schema.
     */
    public static ValidationDefinition parseDefinition(InputStream stream) throws ParserConfigurationException,
            IOException, SAXException, MalformedDataException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(stream);

        ValidationDefinition definition = new ValidationDefinition();
        for (Node node = doc.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == node.TEXT_NODE || node.getNodeType() == node.COMMENT_NODE) {
                continue;
            } else if (node.getNodeName().equals("item")) {
                String name = node.getAttributes().getNamedItem("name").getNodeValue();
                definition.setName(name);

                List<Parameter> parameter = Parameter.nodeToParameterList(node);
                definition.setParameters(parameter);
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
        if (!(other instanceof ValidationDefinition)) {
            return false;
        }
        ValidationDefinition that = (ValidationDefinition)other;
        return this.metadata.equals(that.metadata) && this.parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return this.metadata.hashCode() ^ (this.parameters == null ? 0 : this.parameters.hashCode());
    }

}
