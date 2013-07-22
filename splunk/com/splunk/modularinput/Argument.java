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
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Class representing an argument to a modular input kind.
 *
 * Argument is meant to be used with Scheme to generate an XML definition of the modular input
 * kind that Splunk understands.
 */
public class Argument {
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public enum DataType { BOOLEAN, NUMBER, STRING };

    // Name used to identify this argument in Splunk.
    protected String name;

    // A human readable description of the argument.
    protected String description = null;

    // A string specifying how the argument should be validated, if using internal validation. If using
    // external validation, this will be ignored.
    protected String validation = null;

    // The data type of this field, one of boolean, number, or string.
    protected DataType dataType = DataType.STRING;

    // Must this argument be specified when editing an existing modular input of this kind?
    protected boolean requiredOnEdit = false;

    // Must this argument be specified when creating an existing modular input of this kind?
    protected boolean requiredOnCreate = false;

    public Argument(String name) {
        this.name = name;
    }

    /**
     * Return the name that Splunk uses to represent this argument.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name that Splunk will use to represent this argument.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the human readable description of this argument.
     */
    public String getDescription() {
        return  this.description;
    }

    /**
     * Set the human readable description of this argument.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the validation to be used on this argument, if using internal validation.
     */
    public String getValidation() {
        return this.validation;
    }

    /**
     * Set the validation string that internal validation should use on this argument.
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     * Return the data type of this argument.
     */
    public DataType getDataType() {
        return this.dataType;
    }

    /**
     * Set the data type of this argument.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Return whether a value for this argument must be specified when editing an existing modular input of this kind.
     */
    public boolean isRequiredOnEdit() {
        return this.requiredOnEdit;
    }

    /**
     * Set whether a value for this argument must be specified when editing an existing modular input of this kind.
     */
    public void setRequiredOnEdit(boolean requiredOnEdit) {
        this.requiredOnEdit = requiredOnEdit;
    }

    /**
     * Return whether a value for this argument must be specified when creating a modular input of this kind.
     */
    public boolean isRequiredOnCreate() {
        return this.requiredOnCreate;
    }

    /**
     * Set whether a value for this argument must be specified when creating a modular input of this kind.
     */
    public void setRequiredOnCreate(boolean requiredOnCreate) {
        this.requiredOnCreate = requiredOnCreate;
    }

    /**
     * Return an Element object representing this argument that can be added to another XML document for the Scheme.
     */
    public void addToDocument(Document doc, Node parent) {
        Element arg = doc.createElement("arg");
        arg.setAttribute("name", this.name);

        if (this.description != null) {
            Element description = doc.createElement("description");
            description.appendChild(doc.createTextNode(this.description));
            arg.appendChild(description);
        }

        if (this.validation != null) {
            Element validation = doc.createElement("validation");
            validation.appendChild(doc.createTextNode(this.validation));
            arg.appendChild(validation);
        }

        Element dataType = doc.createElement("data_type");
        dataType.appendChild(doc.createTextNode(this.dataType.toString().toLowerCase()));
        arg.appendChild(dataType);

        Element requiredOnEdit = doc.createElement("required_on_edit");
        requiredOnEdit.appendChild(doc.createTextNode(Boolean.toString(this.requiredOnEdit)));
        arg.appendChild(requiredOnEdit);

        Element requiredOnCreate = doc.createElement("required_on_create");
        requiredOnCreate.appendChild(doc.createTextNode(Boolean.toString(this.requiredOnCreate)));
        arg.appendChild(requiredOnCreate);

        parent.appendChild(arg);
    }

}
