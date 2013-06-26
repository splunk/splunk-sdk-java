package com.splunk.modularinput;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Created with IntelliJ IDEA.
 * User: fross
 * Date: 6/21/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlUtil {


    static String textInNode(Node node, String errorMessage) throws MalformedDataException {
        Node child = node.getFirstChild();
        if (child.getNodeType() != child.TEXT_NODE) {
            throw new MalformedDataException(errorMessage);
        } else {
            return ((Text)child).getData();
        }
    }

    static boolean normalizeBoolean(String s) throws MalformedDataException {
        if (s == null) {
            throw new MalformedDataException("Cannot interpret null as a boolean.");
        }

        String value = s.trim().toLowerCase();

        if (value.equals("true") || value.equals("t") || value.equals("on") ||
                value.equals("yes") || value.equals("y") || value.equals("1")) {
            return true;
        } else if (value.equals("false") || value.equals("f") || value.equals("off") ||
                value.equals("no") || value.equals("n") || value.equals("0")) {
            return false;
        } else {
            throw new MalformedDataException("Cannot interpret string \"" + value + "\" as a boolean.");
        }

    }
}
