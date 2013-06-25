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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InputDefinition encodes the XML defining inputs that Splunk passes to
 * a modular input script as a Java object.
 */
public class InputDefinition {
    private String serverHost;
    private String serverUri;
    private String checkpointDir;
    private String sessionKey;
    private Map<String, List<Parameter>> inputs;

    // Package private on purpose
    InputDefinition() {
        inputs = new HashMap<String, List<Parameter>>();
    }

    /**
     * Set the name of the server on which this modular input is being run.
     */
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    /**
     * @return the name of the server on which this modular input is being run.
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * @param serverUri The URI to reach the server on which this modular input is being run.
     */
    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    /**
     * @return the URI to the server on which this modular input is being run.
     */
    public String getServerUri() {
        return serverUri;
    }

    /**
     * @param checkpointDir The path to write checkpoint files in.
     */
    public void setCheckpointDir(String checkpointDir) {
        this.checkpointDir = checkpointDir;
    }

    /**
     * @return the path to write checkpoint files for restarting inputs in.
     */
    public String getCheckpointDir() {
        return checkpointDir;
    }

    /**
     * @param sessionKey A session key that can be used to access splunkd's REST API.
     */
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    /**
     * @return A session key providing access to splunkd's REST API on this host.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * Add an input to the set of inputs on this InputDefinition.
     *
     * @param name The name of this input (e.g., foobar://this-input-name).
     * @param parameters A list of Parameter objects giving the settings for this input.
     */
    public void addInput(String name, List<Parameter> parameters) {
        this.inputs.put(name, parameters);
    }

    /**
     * @return A map of all the inputs specified in this InputDefinition.
     */
    public Map<String, List<Parameter>> getInputs() {
        return this.inputs;
    }

    /**
     * Parse a stream containing XML into an InputDefinition.
     *
     * @param stream The stream containing XML to parse.
     * @return an InputDefinition object.
     * @throws ParserConfigurationException if there are errors in setting up the parser (which indicates system
     *           configuration issues).
     * @throws IOException if there is an error in reading from the stream.
     * @throws SAXException when the XML is invalid.
     * @throws MalformedDataException when the XML does specify a valid set of inputs.
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
            } else if (node.getNodeName() == "server_host") {
                definition.setServerHost(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element server_host"
                ));
            } else if (node.getNodeName() == "server_uri") {
                definition.setServerUri(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element server_uri"
                ));
            } else if (node.getNodeName() == "checkpoint_dir") {
                definition.setCheckpointDir(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element checkpoint_dir"
                ));
            } else if (node.getNodeName() == "session_key") {
                definition.setSessionKey(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element session_key"
                ));
            } else if (node.getNodeName() == "configuration") {
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == child.TEXT_NODE) {
                        continue;
                    }
                    if (child.getNodeName() != "stanza") {
                        throw new MalformedDataException("Expected stanza element; found " + child.getNodeName());
                    }
                    String name = child.getAttributes().getNamedItem("name").getNodeValue();
                    List<Parameter> parameter = Parameter.nodeToParameterList(child);
                    definition.addInput(name, parameter);
                }
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
        boolean parametersEqual = true;
        return this.getServerUri().equals(that.getServerUri()) &&
                this.getServerHost().equals(that.getServerHost()) &&
                this.getCheckpointDir().equals(that.getCheckpointDir()) &&
                this.getSessionKey().equals(that.getSessionKey()) &&
                this.getInputs().equals(that.getInputs());
    }
}
