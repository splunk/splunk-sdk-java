package com.splunk;

/**
 * Created with IntelliJ IDEA.
 * User: fross
 * Date: 6/21/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleValueParameter extends Parameter {
    String name;
    String value;

    public SingleValueParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SingleValueParameter)) {
            return false;
        } else {
            SingleValueParameter that = (SingleValueParameter)other;
            return this.getValue().equals(that.getValue()) && this.name.equals(that.name);
        }
    }
}
