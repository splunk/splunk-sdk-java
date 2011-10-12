package com.splunk;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/12/11
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Input extends Entity {

    public Input(Service service, String name) {
        super(service, "/services/data/inputs/" + name);
    }

    // UNDONE: kind-ness
}
