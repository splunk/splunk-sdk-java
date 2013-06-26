package com.splunk.modularinput;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Class representing the XML sent by Splunk for external validation of a new modular input.
 */
public class ValidationDefinition {
    private String serverHost;
    private String serverUri;
    private String checkpointDir;
    private String sessionKey;
    private String name;
    private List<Parameter> parameters;

    // Package private on purpose.
    ValidationDefinition() {
        super();
    }

    /**
     * Set the name of the server on which this modular input is being run.
     */
    void setServerHost(String serverHost) {
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
    void setServerUri(String serverUri) {
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
    void setCheckpointDir(String checkpointDir) {
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
    void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    /**
     * @return A session key providing access to splunkd's REST API on this host.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * @param name The name of the proposed modular input instance.
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * @return The name of the proposed modular input instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param parameters a list of Parameter objects giving the proposed configuration.
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return The parameters on the proposed input.
     */
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    /**
     * Create a ValidationDefinition from a provided stream containing XML. The XML typically will look like
     *
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
     *
     * @param stream containing XML to parse.
     * @return a ValidationDefinition.
     * @throws ParserConfigurationException if there are errors in setting up the parser (which indicates system
     *           configuration issues).
     * @throws IOException if there is an error in reading from the stream.
     * @throws SAXException when the XML is invalid.
     * @throws MalformedDataException when the XML does not meet the required schema.
     */
    public static ValidationDefinition parseDefinition(InputStream stream) throws ParserConfigurationException, IOException, SAXException, MalformedDataException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(stream);

        ValidationDefinition definition = new ValidationDefinition();
        for (Node node = doc.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == node.TEXT_NODE) {
                continue;
            } else if (node.getNodeName().equals("server_host")) {
                definition.setServerHost(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element server_host"
                ));
            } else if (node.getNodeName().equals("server_uri")) {
                definition.setServerUri(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element server_uri"
                ));
            } else if (node.getNodeName().equals("checkpoint_dir")) {
                definition.setCheckpointDir(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element checkpoint_dir"
                ));
            } else if (node.getNodeName().equals("session_key")) {
                definition.setSessionKey(XmlUtil.textInNode(
                        node,
                        "Expected a text value in element session_key"
                ));
            } else if (node.getNodeName().equals("item")) {
                String name = node.getAttributes().getNamedItem("name").getNodeValue();
                definition.setName(name);

                List<Parameter> parameter = Parameter.nodeToParameterList(node);
                definition.setParameters(parameter);
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
        return this.getServerUri().equals(that.getServerUri()) &&
                this.getServerHost().equals(that.getServerHost()) &&
                this.getCheckpointDir().equals(that.getCheckpointDir()) &&
                this.getSessionKey().equals(that.getSessionKey()) &&
                this.getParameters().equals(that.getParameters());
    }

    @Override
    public int hashCode() {
        return (this.getServerUri() == null ? 0 : this.getServerUri().hashCode()) ^
                (this.getServerHost() == null ? 0 : this.getServerHost().hashCode()) ^
                (this.getCheckpointDir() == null ? 0 : this.getCheckpointDir().hashCode()) ^
                (this.getSessionKey() == null ? 0 : this.getSessionKey().hashCode()) ^
                (this.getParameters() == null ? 0 : this.getParameters().hashCode());
    }

}
