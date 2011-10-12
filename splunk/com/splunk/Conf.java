package com.splunk;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/12/11
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.Map;

public class Conf extends Entity {

    public Conf(Service service, String name) {
        super(service, "/services/configs/conf-" + name);
    }

    public Element submit(Map<String,String> stanza) throws Exception {
        // UNDONE: test to see if this is correct usage
        return super.post(stanza);
    }

    public Element read(String relpath) throws Exception {
        return super.get(relpath);
    }

    public Element read() throws Exception {
        return super.get();
    }
}
