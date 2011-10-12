package com.splunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity extends Endpoint {

    public Entity() {
        // default constructor - needed?
    }

    public Entity(Service service, String path) {
        super(service, path);
    }

    public Map<String,String> read(List<String> items) throws Exception {
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

    public Map<String,String> readmeta() throws Exception {
        List<String> itemList = new ArrayList<String>();
        itemList.add("eai:acl");
        itemList.add("eai:attributes");
        return read(itemList);
    }

    public void update(Map<String,String> args) throws Exception {
        super.post(args);
    }

    public Element disable() throws Exception {
        return super.post("disable");
    }

    public Element enable () throws Exception {
        return super.post("enable");
    }

    public Element reload () throws Exception {
        return super.post("_reload");
    }

    public Element delete(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path, args).getContent());
    }

    public Element delete() throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path).getContent());
    }
}

