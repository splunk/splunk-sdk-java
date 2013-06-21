package com.splunk;

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
 */
public class InputDefinition {


    private String serverHost;
    private String serverUri;
    private String checkpointDir;
    private String sessionKey;

    Map<String, List<Parameter>> inputs;

    public InputDefinition() {
        inputs = new HashMap<String, List<Parameter>>();
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setCheckpointDir(String checkpointDir) {
        this.checkpointDir = checkpointDir;
    }

    public String getCheckpointDir() {
        return checkpointDir;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void addInput(String name, List<Parameter> parameters) {
        this.inputs.put(name, parameters);
    }

    public Map<String, List<Parameter>> getInputs() {
        return this.inputs;
    }

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
