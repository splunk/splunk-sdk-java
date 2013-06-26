package com.splunk.modularinput;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class representing the metadata for a modular input kind.
 *
 * A Scheme specifies a title, description, several options of how Splunk should run modular inputs of this
 * kind, and a set of arguments which define a particular modular input's properties.
 *
 * The primary use of Scheme is to abstract away the construction of XML to feed to Splunk.
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
     * Return the title of this modular input kind.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of this modular input kind.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the human readable description of this modular input kind.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the human readable description of this modular input kind.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return whether Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input (true) or use the validation predicates specified by the arguments (false).
     */
    public boolean isUseExternalValidation() {
        return useExternalValidation;
    }

    /**
     * Specify whether Splunk should use the modular input kind script to validate the arguments
     * of a particular modular input (true) or use the validation predicates specified by the arguments (false).
     */
    public void setUseExternalValidation(boolean useExternalValidation) {
        this.useExternalValidation = useExternalValidation;
    }

    /**
     * Return whether Splunk should run all modular inputs of this kind via one instance of the script
     * or start an instance for each modular input.
     */
    public boolean isUseSingleInstance() {
        return useSingleInstance;
    }

    /**
     * Specify whether Splunk should run all modular inputs of this kind via one instance of the script
     * or start an instance for each modular input.
     */
    public void setUseSingleInstance(boolean useSingleInstance) {
        this.useSingleInstance = useSingleInstance;
    }

    /**
     * Return whether this modular input kind will send events to Splunk as XML (the default and preferred
     * value) or plain text.
     */
    public StreamingMode getStreamingMode() {
        return streamingMode;
    }

    /**
     * Specify whether this modular input kind will send events to Splunk as XML (the default and preferred
     * value) or plain text.
     */
    public void setStreamingMode(StreamingMode streamingMode) {
        this.streamingMode = streamingMode;
    }

    /**
     * Return all the arguments to this modular input kind.
     */
    public List<Argument> getArguments() {
        return arguments;
    }

    /**
     * Replace the current list of arguments with the specified one.
     */
    public void setArguments(List<Argument> arguments) {
        this.arguments = new ArrayList<Argument>(arguments);
    }

    /**
     * Append an argument to those this modular input kind takes.
     */
    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    /**
     * Generates an XML encoding of this scheme to be passed to Splunk.
     *
     * @return an org.w3c.dom.Document object containing the XML of this scheme.
     * @throws ParserConfigurationException if there was a problem configuring the XML libraries.
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
