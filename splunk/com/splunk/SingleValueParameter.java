package com.splunk;

/**
 * Class representing a parameter as part of a modular input instance that
 * contains only a single value. This corresponds to XML fragments of the form
 *
 * <param name="param1">value11</param>
 *
 */
public class SingleValueParameter extends Parameter {
    protected String name;
    protected String value;

    // Package private by design.
    SingleValueParameter(String name, String value) {
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
