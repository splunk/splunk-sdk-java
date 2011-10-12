package com.splunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/6/11
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.HashMap;
import java.util.Map;

public class Collection extends Endpoint {

    public Collection() {
        // default constructor - needed?
    }

    public Collection(Service serv, String path) {
        super(serv, path);
    }

    private Map<String,String> read(List<String> items) throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        Element element = super.get();
        for (String item: items) {
            for (Entry entry: element.entry) {
                for (String key: entry.content.keySet()) {
                    if (key.startsWith(item)) {
                        response.put(key, entry.content.get(key));
                    }
                }
            }
        }
        return response;
    }

    public boolean contains(String key) throws Exception {
        List<String> names = list(); //UNDONE: check to see if this is what we should be looking through.
        return names.contains(key);
    }

    public List<String> list() throws Exception {
        List<String> retList = new ArrayList<String>();
        Element element = super.get();

         for (Entry entry: element.entry) {
             retList.add(entry.title);
         }
        return retList;
    }

    public Map<String,String> itemmeta() throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        List<String> items = new ArrayList<String>();
        items.add("eai:acl");
        items.add("eai:attributes");

        Element element = super.get();
        for (String item: items) {
            for (Entry entry: element.entry) {
                for (String key: entry.content.keySet()) {
                    if (key.startsWith(item)) {
                        response.put(key, entry.content.get(key));
                    }
                }
            }
        }
        return response;
    }
}
