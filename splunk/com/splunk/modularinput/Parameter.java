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

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Parameter} class is a base class for parameters of modular inputs. It has two subclasses: {@code SingleValueParameter}
 * and {@code MultiValueParameter}.
 *
 * All parameters should be constructed with the static {@code nodeToParameterList} method, which takes an XML {@code org.w3c.dom.Node}
 * object as its argument and returns a list of {@code Parameter} objects, single valued or multi valued as needed.
 */
public abstract class Parameter {
    public abstract String getName();

    // Package private to enforce using the nodeToParameterList function to create Parameter objects.
    Parameter() {
        super();
    }

    /**
     * Generates a list of {@code Parameter} objects from an {@code org.w3c.dom.Node} object containing a set of parameters. The node
     * may be any element, but is expected to contain elements param or param_list, as in
     *
     * <pre>
     * {@code
     * <stanza name="foobar://aaa">
     *     <param name="param1">value1</param>
     *     <param name="param2">value2</param>
     *     <param name="disabled">0</param>
     *     <param name="index">default</param>
     *     <param_list name="multiValue">
     *         <value>value1</value>
     *         <value>value2</value>
     *     </param_list>
     * </stanza>
     * }
     * </pre>
     *
     * @param node An {@code org.w3c.dom.Node} object containing the parameter list as children.
     * @return A list of Parameter objects extracted from the XML.
     * @throws com.splunk.modularinput.MalformedDataException If the XML does not specify a valid parameter list.
     */
    public static List<Parameter> nodeToParameterList(Node node) throws MalformedDataException {
        List<Parameter> parameters = new ArrayList<Parameter>();

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == child.TEXT_NODE) {
                continue;
            }
            if (child.getNodeName() == "param") {
                // This is a single value parameter
                String name = child.getAttributes().getNamedItem("name").getNodeValue();
                String value = XmlUtil.textInNode(child, "Element param with name=\"" + name +
                        "\" did not contain text.");
                parameters.add(new SingleValueParameter(name, value));
            } else if (child.getNodeName() == "param_list") {
                String name = child.getAttributes().getNamedItem("name").getNodeValue();
                MultiValueParameter parameter = new MultiValueParameter(name);
                for (Node valueNode = child.getFirstChild(); valueNode != null; valueNode = valueNode.getNextSibling()) {
                    if (valueNode.getNodeType() == valueNode.TEXT_NODE) continue;
                    if (valueNode.getNodeName() != "value") {
                        throw new MalformedDataException("Expected a value element in parameter named " +
                                child.getNodeName() + "; found " + valueNode.getNodeName());
                    } else {
                        parameter.appendValue(XmlUtil.textInNode(
                                valueNode,
                                "value element in parameter named " + child.getNodeName() + " did not contain text."
                        ));
                    }
                }
                parameters.add(parameter);
            } else {
                throw new MalformedDataException("Bad parameter element named " + child.getNodeName());
            }
        }

        return parameters;
    }
}
