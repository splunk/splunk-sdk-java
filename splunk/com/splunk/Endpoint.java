package com.splunk;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/6/11
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Endpoint {

    public Service service = null;
    public String path = null;

    public Endpoint() {

    }

    public Endpoint(Service serv, String pth) {
        service = serv;
        path = pth;
    }

    // UNDONE: some overloads may not be relevant.

    public Element get(String relpath) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.get(path + relpath).getContent());
    }

    public Element get() throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.get(path).getContent());
    }



    public Element post(String relpath, Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path + relpath, args).getContent());
    }

    public Element post(String relpath) throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path + relpath, args).getContent());
    }

    public Element post(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path, args).getContent());
    }

    public Element post() throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path, args).getContent());
    }
}
