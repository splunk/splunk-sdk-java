package com.splunk.modularinput;

/**
 * Class representing a parameter as part of a modular input instance that
 * contains only a single value. This corresponds to XML fragments of the form
 *
 * <param name="param1">value11</param>
 *
 */
public class SingleValueParameter extends Parameter {
    private final String name;
    private final String value;

    // Package private by design.
    SingleValueParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of this parameter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the parameter as found (as a String), without trying to coerce it to another type.
     *
     * If your field is boolean or numeric, use getBoolean or one of getInt, getLong, getFloat, and getDouble
     * instead.
     *
     * @return the value of this parameter as a String.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Tries to coerce the value of this parameter to a boolean. A range of values (true, t, on, 1, y, yes) are
     * interpreted as true, and a similar range (false, f, off, 0, no, n) as false. Everything else, including null,
     * results in a MalformedDataException.
     *
     * @return the value of this parameter coerced to a boolean.
     * @throws MalformedDataException if the value cannot be coerced to a boolean.
     */
    public boolean getBoolean() throws MalformedDataException {
        return XmlUtil.normalizeBoolean(getValue());
    }

    /**
     * Coerces the value of this field to an int.
     *
     * @return an int parsed from this parameter's value.
     */
    public int getInt() {
        return Integer.parseInt(getValue());
    }

    /**
     * Coerces the value of this field to a long.
     *
     * @return a long parsed from this parameter's value.
     */
    public long getLong() {
        return Long.parseLong(getValue());
    }

    /**
     * Coerces the value of this field to a float.
     *
     * @return a float parsed from this parameter's value.
     */
    public float getFloat() {
        return Float.parseFloat(getValue());
    }

    /**
     * Coerces the value of this field to a double.
     *
     * @return a double parsed from this parameter's value.
     */
    public double getDouble() {
        return Double.parseDouble(getValue());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SingleValueParameter)) {
            return false;
        } else {
            SingleValueParameter that = (SingleValueParameter)other;
            return this.getValue().equals(that.getValue()) && this.getName().equals(that.getName());
        }
    }

    @Override
    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) ^
                (this.value == null ? 0 : this.value.hashCode());
    }
}
