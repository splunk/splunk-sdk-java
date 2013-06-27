package com.splunk.modularinput;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Utility functions for handling XML that are used in several places.
 */
class XmlUtil {

    /**
     * Given an org.w3c.dom.Node which is an element containing text, returns the text. If there
     * is anything but text inside the element, throws a MalformedDataException with errorMessage as the reason.
     *
     * @param node The Node object to pull text out of.
     * @param errorMessage The reason provided if there is something besides text in the element.
     * @return a String of the text in the element.
     * @throws MalformedDataException when there is anything besides text in the element.
     */
    static String textInNode(Node node, String errorMessage) throws MalformedDataException {
        Node child = node.getFirstChild();
        if (child.getNodeType() != child.TEXT_NODE) {
            throw new MalformedDataException(errorMessage);
        } else {
            return ((Text)child).getData();
        }
    }

    /**
     * Coerce all the forms of boolean Splunk may return to proper booleans.
     *
     * @param s a String containing the boolean from Splunk.
     * @return a Java boolean.
     * @throws MalformedDataException if the string can't be coerced to a boolean.
     */
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
