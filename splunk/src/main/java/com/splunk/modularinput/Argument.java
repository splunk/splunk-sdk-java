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
 * The {@code Argument} class represents an argument to a modular input kind.
 *
 * {@code Argument} is meant to be used with {@code Scheme} to generate an XML definition of the modular input
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
     * Gets the name that Splunk uses to represent this argument.
     *
     * @return The argument's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name that Splunk will use to represent this argument.
     * 
     * @param name
     *      The name that Splunk will use to represent this argument.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the human readable description of this argument.
     *
     * @return The argument's description.
     */
    public String getDescription() {
        return  this.description;
    }

    /**
     * Sets the human readable description of this argument.
     * 
     * @param description
     *      The human readable description of this argument.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the validation to be used on this argument, if using internal validation.
     *
     * @return The validation used on the argument.
     */
    public String getValidation() {
        return this.validation;
    }

    /**
     * Sets the validation string that internal validation should use on this argument.
     * 
     * @param validation
     *      The validation string that internal validation should use on this argument.
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     * Gets the data type of this argument.
     *
     * @return The data type of the argument.
     */
    public DataType getDataType() {
        return this.dataType;
    }

    /**
     * Sets the data type of this argument.
     * 
     * @param dataType
     *      The data type of this argument.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns whether a value for this argument must be specified when editing an existing modular input of this kind.
     *
     * @return {@code true} if a value for this argument must be specified when editing an existing modular input of this kind, 
     *      {@code false} if not.
     */
    public boolean isRequiredOnEdit() {
        return this.requiredOnEdit;
    }

    /**
     * Sets whether a value for this argument must be specified when editing an existing modular input of this kind.
     * 
     * @param requiredOnEdit
     *      {@code true} if a value for this argument must be specified when editing an existing modular input of this kind, 
     *      {@code false} if not.
     */
    public void setRequiredOnEdit(boolean requiredOnEdit) {
        this.requiredOnEdit = requiredOnEdit;
    }

    /**
     * Returns whether a value for this argument must be specified when creating a modular input of this kind.
     *
     * @return {@code true} if a value for this argument must be specified when creating a modular input of this kind, 
     *      {@code false} if not.
     */
    public boolean isRequiredOnCreate() {
        return this.requiredOnCreate;
    }

    /**
     * Sets whether a value for this argument must be specified when creating a modular input of this kind.
     * 
     * @param requiredOnCreate
     *      {@code true} if a value for this argument must be specified when creating a modular input of this kind, 
     *      {@code false} if not.
     */
    public void setRequiredOnCreate(boolean requiredOnCreate) {
        this.requiredOnCreate = requiredOnCreate;
    }

    /**
     * Returns an {@code Element} object representing this argument that can be added to another XML document for the {@code Scheme}.
     * 
     * @param doc
     *      The XML document.
     * @param parent
     *      The parent object.
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
