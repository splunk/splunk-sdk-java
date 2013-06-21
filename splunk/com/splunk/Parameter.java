package com.splunk;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class Parameter {
    public abstract String getName();

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
