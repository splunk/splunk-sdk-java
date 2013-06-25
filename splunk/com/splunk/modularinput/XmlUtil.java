package com.splunk.modularinput;

import com.splunk.modularinput.MalformedDataException;
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
}
