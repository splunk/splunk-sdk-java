package com.splunk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: fross
 * Date: 6/18/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
class Scheme {
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public enum StreamingMode { SIMPLE, XML };

    // Name of this module input kind. <tt>title</tt> will be used as the URL scheme when
    // specifying particular modular inputs. For example, if <tt>title</tt> is <tt>"abc"</tt>,
    // a particular modular input of this kind would be referenced as <tt>abc://some_name</tt>.
    protected String title;

    // Human readable description of this modular input kind.
    protected String description;

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

    public Scheme(String title, String description) {
        this.title = title;
        this.description = description;
        this.arguments = new ArrayList<Argument>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUseExternalValidation() {
        return useExternalValidation;
    }

    public void setUseExternalValidation(boolean useExternalValidation) {
        this.useExternalValidation = useExternalValidation;
    }

    public boolean isUseSingleInstance() {
        return useSingleInstance;
    }

    public void setUseSingleInstance(boolean useSingleInstance) {
        this.useSingleInstance = useSingleInstance;
    }

    public StreamingMode getStreamingMode() {
        return streamingMode;
    }

    public void setStreamingMode(StreamingMode streamingMode) {
        this.streamingMode = streamingMode;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = new ArrayList<Argument>(arguments);
    }

    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    public static class StringBufferOutputStream extends OutputStream
    {
        private StringBuffer textBuffer = new StringBuffer();

        /**
         *
         */
        public StringBufferOutputStream()
        {
            super();
        }

        /*
         * @see java.io.OutputStream#write(int)
         */
        public void write(int b) throws IOException
        {
            char a = (char)b;
            textBuffer.append(a);
        }

        public String toString()
        {
            return textBuffer.toString();
        }

        public void clear()
        {
            textBuffer.delete(0, textBuffer.length());
        }
    }

    public String toXML() throws ParserConfigurationException, TransformerException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        Element root = doc.createElement("scheme");
        doc.appendChild(root);

        Element title = doc.createElement("title");
        title.appendChild(doc.createTextNode("abcd"));
        root.appendChild(title);

        Element description = doc.createElement("description");
        description.appendChild(doc.createTextNode("쎼 and 쎶 and <&> für"));
        root.appendChild(description);

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

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StringBufferOutputStream buffer = new StringBufferOutputStream();
        StreamResult result = new StreamResult(buffer);
        transformer.transform(source, result);
        String xml = result.getOutputStream().toString();
        return xml;
    }
}
