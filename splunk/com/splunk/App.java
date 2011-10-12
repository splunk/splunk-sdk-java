package com.splunk;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/7/11
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class App extends Entity {
    
    public App(Service service, String relpath) {
        super(service, "/services/apps/local/" + relpath);
    }

    public Element read(String relpath) throws Exception {
        return super.get(relpath);
    }

    public Element read() throws Exception {
        return super.get();
    }    
}
